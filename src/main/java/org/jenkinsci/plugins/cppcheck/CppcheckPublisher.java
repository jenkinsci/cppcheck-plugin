package org.jenkinsci.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import jenkins.tasks.SimpleBuildStep;

import org.jenkinsci.plugins.cppcheck.CppcheckResult;
import org.jenkinsci.plugins.cppcheck.CppcheckBuildAction;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;
import org.jenkinsci.plugins.cppcheck.util.CppcheckBuildResultEvaluator;
import org.jenkinsci.plugins.cppcheck.util.CppcheckLogger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.Symbol;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Gregory Boissinot
 */
public class CppcheckPublisher extends Recorder implements SimpleBuildStep {
    /**
     * XML file with source container data. Lazy loading instead of data in build.xml.
     * 
     * @since 1.15
     */
    public static final String XML_FILE_DETAILS = "cppcheck_details.xml";

    private CppcheckConfig cppcheckConfig;

    @DataBoundConstructor
    public CppcheckPublisher(String pattern,
                             boolean ignoreBlankFiles, String threshold,
                             boolean allowNoReport,
                             String newThreshold, String failureThreshold,
                             String newFailureThreshold, String healthy, String unHealthy,
                             boolean severityError,
                             boolean severityWarning,
                             boolean severityStyle,
                             boolean severityPerformance,
                             boolean severityInformation,
                             boolean severityNoCategory,
                             boolean severityPortability,
                             int xSize, int ySize,
                             int numBuildsInGraph,
                             boolean displayAllErrors,
                             boolean displayErrorSeverity,
                             boolean displayWarningSeverity,
                             boolean displayStyleSeverity,
                             boolean displayPerformanceSeverity,
                             boolean displayInformationSeverity,
                             boolean displayNoCategorySeverity,
                             boolean displayPortabilitySeverity) {

        cppcheckConfig = new CppcheckConfig();

        cppcheckConfig.setPattern(pattern);
        cppcheckConfig.setAllowNoReport(allowNoReport);
        cppcheckConfig.setIgnoreBlankFiles(ignoreBlankFiles);
        
        cppcheckConfig.setConfigSeverityEvaluation(
        	new CppcheckConfigSeverityEvaluation(
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy, unHealthy,
                severityError,
                severityWarning,
                severityStyle,
                severityPerformance,
                severityInformation,
                severityNoCategory,
                severityPortability));
        
        cppcheckConfig.setConfigGraph( new CppcheckConfigGraph(
                xSize, ySize, numBuildsInGraph,
                displayAllErrors,
                displayErrorSeverity,
                displayWarningSeverity,
                displayStyleSeverity,
                displayPerformanceSeverity,
                displayInformationSeverity,
                displayNoCategorySeverity,
                displayPortabilitySeverity));
    }


    public CppcheckPublisher(CppcheckConfig cppcheckConfig) {
        this.cppcheckConfig = cppcheckConfig;
    }

    public CppcheckConfig getCppcheckConfig() {
        return cppcheckConfig;
    }

    protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public void perform(@Nonnull Run<?,?> build, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener listener) throws InterruptedException, IOException {
    	
    	System.out.println("perform...");
    	
            if (this.canContinue(build.getResult())) {
                CppcheckLogger.log(listener, "ccc Starting the cppcheck analysis.");
                
                EnvVars env = build.getEnvironment(listener);
                String expandedPattern = env.expand(cppcheckConfig.getPattern());
                

                CppcheckParserResult parser = new CppcheckParserResult(listener,
                		expandedPattern, cppcheckConfig.isIgnoreBlankFiles());
                CppcheckReport cppcheckReport;
                try {
                    cppcheckReport = workspace.act(parser);
                    
                } catch (Exception e) {
                    CppcheckLogger.log(listener, "Error on cppcheck analysis: " + e);
                    build.setResult(Result.FAILURE);
                    return;
                }

                if (cppcheckReport == null) {
                    // Check if we're configured to allow not having a report
                    if (cppcheckConfig.getAllowNoReport()) {
                        return;
                    } else {
                        build.setResult(Result.FAILURE);
                        return;
                    }
                }

                CppcheckSourceContainer cppcheckSourceContainer
                        = new CppcheckSourceContainer(listener, workspace,
                                workspace, cppcheckReport.getAllErrors());

                CppcheckResult result = new CppcheckResult(cppcheckReport.getStatistics(), build);
                CppcheckConfigSeverityEvaluation severityEvaluation
                        = cppcheckConfig.getConfigSeverityEvaluation();

                Result buildResult = new CppcheckBuildResultEvaluator().evaluateBuildResult(
                        listener, result.getNumberErrorsAccordingConfiguration(severityEvaluation, false),
                        result.getNumberErrorsAccordingConfiguration(severityEvaluation, true),
                        severityEvaluation);

                if (buildResult != Result.SUCCESS) {
                    build.setResult(buildResult);
                }

                CppcheckLogger.log(listener, "Starting buildaction.");
                
                CppcheckBuildAction buildAction = new CppcheckBuildAction(build, result, cppcheckConfig,
                        CppcheckBuildAction.computeHealthReportPercentage(result, severityEvaluation));

                build.addAction(buildAction);

                XmlFile xmlSourceContainer = new XmlFile(new File(build.getRootDir(),
                        XML_FILE_DETAILS));
                xmlSourceContainer.write(cppcheckSourceContainer);

                copyFilesToBuildDirectory(build.getRootDir(), launcher.getChannel(),
                        cppcheckSourceContainer.getInternalMap().values());

                CppcheckLogger.log(listener, "Ending the cppcheck analysis.");
            }
            return;
        }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) throws InterruptedException, IOException {

        if (this.canContinue(build.getResult())) {
            CppcheckLogger.log(listener, "Starting the cppcheck analysis.");
            
            EnvVars env = build.getEnvironment(listener);
            String expandedPattern = env.expand(cppcheckConfig.getPattern());
            

            CppcheckParserResult parser = new CppcheckParserResult(listener,
            		expandedPattern, cppcheckConfig.isIgnoreBlankFiles());
            CppcheckReport cppcheckReport = null;
            try {
            	FilePath oWorkspacePath = build.getWorkspace(); 
            	
            	if( oWorkspacePath != null) {            		
            		cppcheckReport = oWorkspacePath.act(parser);
            	}
            		
            } catch (Exception e) {
                CppcheckLogger.log(listener, "Error on cppcheck analysis: " + e);
                build.setResult(Result.FAILURE);
                return false;
            }

            if (cppcheckReport == null) {
                // Check if we're configured to allow not having a report
                if (cppcheckConfig.getAllowNoReport()) {
                    return true;
                } else {
                    build.setResult(Result.FAILURE);
                    return false;
                }
            }

            CppcheckSourceContainer cppcheckSourceContainer
                    = new CppcheckSourceContainer(listener, build.getWorkspace(),
                            build.getModuleRoot(), cppcheckReport.getAllErrors());

            CppcheckResult result = new CppcheckResult(cppcheckReport.getStatistics(), build);
            CppcheckConfigSeverityEvaluation severityEvaluation
                    = cppcheckConfig.getConfigSeverityEvaluation();

            Result buildResult = new CppcheckBuildResultEvaluator().evaluateBuildResult(
                    listener, result.getNumberErrorsAccordingConfiguration(severityEvaluation, false),
                    result.getNumberErrorsAccordingConfiguration(severityEvaluation, true),
                    severityEvaluation);

            if (buildResult != Result.SUCCESS) {
                build.setResult(buildResult);
            }

            CppcheckBuildAction buildAction = new CppcheckBuildAction(build, result, cppcheckConfig,
                    CppcheckBuildAction.computeHealthReportPercentage(result, severityEvaluation));

            build.addAction(buildAction);

            XmlFile xmlSourceContainer = new XmlFile(new File(build.getRootDir(),
                    XML_FILE_DETAILS));
            xmlSourceContainer.write(cppcheckSourceContainer);

            copyFilesToBuildDirectory(build.getRootDir(), launcher.getChannel(),
                    cppcheckSourceContainer.getInternalMap().values());

            CppcheckLogger.log(listener, "Ending the cppcheck analysis.");
        }
        return true;
    }

    /**
     * Copies all the source files from the workspace to the build folder.
     *
     * @param rootDir      directory to store the copied files in
     * @param channel      channel to get the files from
     * @param sourcesFiles the sources files to be copied
     * @throws IOException                   if the files could not be written
     * @throws java.io.FileNotFoundException if the files could not be written
     * @throws InterruptedException          if the user cancels the processing
     */
    private void copyFilesToBuildDirectory(final File rootDir,
            final VirtualChannel channel,
            final Collection<CppcheckWorkspaceFile> sourcesFiles)
            throws IOException, InterruptedException {

        File directory = new File(rootDir, CppcheckWorkspaceFile.DIR_WORKSPACE_FILES);
        if (!directory.exists() && !directory.mkdir()) {
            throw new IOException("Can't create directory for copy of workspace files: "
                    + directory.getAbsolutePath());
        }

        for (CppcheckWorkspaceFile file : sourcesFiles) {
            if (!file.isSourceIgnored()) {
                File masterFile = new File(directory, file.getTempName());
                if (!masterFile.exists()) {
                    FileOutputStream outputStream = new FileOutputStream(masterFile);
                    new FilePath(channel, file.getFileName()).copyTo(outputStream);
                }
            }
        }
    }

    @Extension
    @Symbol("CppcheckPublisher")
    public static final class CppcheckDescriptor extends BuildStepDescriptor<Publisher> {

        public CppcheckDescriptor() {
            super(CppcheckPublisher.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.cppcheck_PublishResults();
        }

        @Override
        public final String getHelpFile() {
            return getPluginRoot() + "help.html";
        }

        public String getPluginRoot() {
            return "/plugin/cppcheck/";
        }

        public CppcheckConfig getConfig() {
            return new CppcheckConfig();
        }
    }
    
    //getters of the data variables
    public String getPattern(){
        return cppcheckConfig.getPattern();
     }
     
     public boolean isIgnoreBlankFiles(){
        return cppcheckConfig.isIgnoreBlankFiles();
     }
     
     public boolean isAllowNoReport(){
         return cppcheckConfig.getAllowNoReport();
      }

     public String getThreshold(){
        return cppcheckConfig.getConfigSeverityEvaluation().getThreshold();
     }
     
     public String getNewThreshold(){
        return cppcheckConfig.getConfigSeverityEvaluation().getNewThreshold();
     }
     
     public String getFailureThreshold(){
        return cppcheckConfig.getConfigSeverityEvaluation().getFailureThreshold();
     }
     
     public String getNewFailureThreshold(){
        return cppcheckConfig.getConfigSeverityEvaluation().getNewFailureThreshold();
     }
     
     public String getHealthy(){
        return cppcheckConfig.getConfigSeverityEvaluation().getHealthy();
     }
     
     public String getUnHealthy(){
        return cppcheckConfig.getConfigSeverityEvaluation().getUnHealthy();
     }
     
     public boolean isSeverityError(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityError();
     }
     
     public boolean isSeverityWarning(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityWarning();
     }
     
     public boolean isSeverityStyle(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityStyle();
     }
     
     public boolean isSeverityPerformance(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityPerformance();
     }
     
     public boolean isSeverityInformation(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityInformation();
     }
     
     public boolean isSeverityNoCategory(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityNoCategory();
     }
     
     public boolean isSeverityPortability(){
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityPortability();
     }
     
     public int getXSize(){
        return cppcheckConfig.getConfigGraph().getXSize();
     }
     
     public int getYSize(){
        return cppcheckConfig.getConfigGraph().getYSize();
     }
     
     public int getNumBuildsInGraph(){
        return cppcheckConfig.getConfigGraph().getNumBuildsInGraph();
     }
     
     public boolean isDisplayAllErrors(){
        return cppcheckConfig.getConfigGraph().isDisplayAllErrors();
     }
     
     public boolean isDisplayErrorSeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayErrorSeverity();
     }
     
     public boolean isDisplayWarningSeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayWarningSeverity();
     }
     
     public boolean isDisplayStyleSeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayStyleSeverity();
     }
     
     public boolean isDisplayPerformanceSeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayPerformanceSeverity();
     }
     
     public boolean isDisplayInformationSeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayInformationSeverity();
     }
     
     public boolean isDisplayNoCategorySeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayNoCategorySeverity();
     }
     
     public boolean isDisplayPortabilitySeverity(){
        return cppcheckConfig.getConfigGraph().isDisplayPortabilitySeverity();
     }
}
