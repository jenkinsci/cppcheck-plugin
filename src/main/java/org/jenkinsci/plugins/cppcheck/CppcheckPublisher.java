package org.jenkinsci.plugins.cppcheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;
import org.jenkinsci.plugins.cppcheck.util.CppcheckBuildResultEvaluator;
import org.jenkinsci.plugins.cppcheck.util.CppcheckLogger;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
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

    private String pattern;
    private boolean ignoreBlankFiles;
    private String threshold;
    private boolean allowNoReport;
    private String newThreshold;
    private String failureThreshold;
    private String newFailureThreshold;
    private String healthy;
    private String unHealthy;
    private boolean severityError;
    private boolean severityWarning;
    private boolean severityStyle;
    private boolean severityPerformance;
    private boolean severityInformation;
    private boolean severityNoCategory;
    private boolean severityPortability;
    private int xSize;
    private int ySize;
    private int numBuildsInGraph;
    private boolean displayAllErrors;
    private boolean displayErrorSeverity;
    private boolean displayWarningSeverity;
    private boolean displayStyleSeverity;
    private boolean displayPerformanceSeverity;
    private boolean displayInformationSeverity;
    private boolean displayNoCategorySeverity;
    private boolean displayPortabilitySeverity;


    @DataBoundConstructor
    public CppcheckPublisher() {this("", false, "", false, "", "", "", "", "", true, true, true, true, true, true, true, 500, 200, 0, true, false, false, false, false, false, false, false);}

    @Deprecated
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

    	this.pattern = pattern;
    	this.ignoreBlankFiles = ignoreBlankFiles;
    	this.threshold = threshold;
        this.allowNoReport = allowNoReport;
        this.newThreshold = newThreshold;
        this.failureThreshold = failureThreshold;
        this.newFailureThreshold = newFailureThreshold;
        this.healthy = healthy;
        this.unHealthy = unHealthy;
        this.severityError = severityError;
        this.severityWarning = severityWarning;
        this.severityStyle = severityStyle;
        this.severityPerformance = severityPerformance;
        this.severityInformation = severityInformation;
        this.severityNoCategory = severityNoCategory;
        this.severityPortability = severityPortability;
        this.xSize = xSize;
        this.ySize = ySize;
        this.numBuildsInGraph = numBuildsInGraph;
        this.displayAllErrors = displayAllErrors;
        this.displayErrorSeverity = displayErrorSeverity;
        this.displayWarningSeverity = displayWarningSeverity;
        this.displayStyleSeverity = displayStyleSeverity;
        this.displayPerformanceSeverity = displayPerformanceSeverity;
        this.displayInformationSeverity = displayInformationSeverity;
        this.displayNoCategorySeverity = displayNoCategorySeverity;
        this.displayPortabilitySeverity = displayPortabilitySeverity;

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

    @DataBoundSetter
    public void setpattern(String pattern) {
    	this.pattern = pattern;
    }
    public String getpattern() {
        return pattern;
    }
    @DataBoundSetter
    public void setnewThreshold(String newThreshold) {
    	this.newThreshold = newThreshold;
    }
    public String getnewThreshold() {
        return this.newThreshold;
    }
    @DataBoundSetter
    public void setfailureThreshold(String failureThreshold) {
    	this.failureThreshold = failureThreshold;
    }
    public String getfailureThreshold() {
        return this.failureThreshold;
    }
    @DataBoundSetter
    public void setnewFailureThreshold(String newFailureThreshold) {
    	this.newFailureThreshold = newFailureThreshold;
    }
    public String getnewFailureThreshold() {
        return this.newFailureThreshold;
    }
    @DataBoundSetter
    public void sethealthy(String healthy) {
    	this.healthy = healthy;
    }
    public String gethealthy() {
        return this.healthy;
    }
    @DataBoundSetter
    public void setunHealthy(String unHealthy) {
    	this.unHealthy = unHealthy;
    }
    public String getunHealthy() {
        return this.unHealthy;
    }
    @DataBoundSetter
    public void setignoreBlankFiles(boolean ignoreBlankFiles) {
    	this.ignoreBlankFiles = ignoreBlankFiles;
    }
    public boolean getignoreBlankFiles() {
        return this.ignoreBlankFiles;
    }
    @DataBoundSetter
    public void setallowNoReport(boolean allowNoReport) {
    	this.allowNoReport = allowNoReport;
    }
    public boolean getallowNoReport() {
        return this.allowNoReport;
    }
    @DataBoundSetter
    public void setseverityError(boolean severityError) {
    	this.severityError = severityError;
    }
    public boolean getseverityError() {
        return this.severityError;
    }
    @DataBoundSetter
    public void setseverityWarning(boolean severityWarning) {
    	this.severityWarning = severityWarning;
    }
    public boolean getseverityWarning() {
        return this.severityWarning;
    }
    @DataBoundSetter
    public void setseverityStyle(boolean severityStyle) {
    	this.severityStyle = severityStyle;
    }
    public boolean getseverityStyle() {
        return this.severityStyle;
    }
    @DataBoundSetter
    public void setseverityPerformance(boolean severityPerformance) {
    	this.severityPerformance = severityPerformance;
    }
    public boolean getseverityPerformance() {
        return this.severityPerformance;
    }
    @DataBoundSetter
    public void setseverityInformation(boolean severityInformation) {
    	this.severityInformation = severityInformation;
    }
    public boolean getseverityInformation() {
        return this.severityInformation;
    }
    @DataBoundSetter
    public void setseverityNoCategory(boolean severityNoCategory) {
    	this.severityNoCategory = severityNoCategory;
    }
    public boolean getseverityNoCategory() {
        return this.severityNoCategory;
    }
    @DataBoundSetter
    public void setseverityPortability(boolean severityPortability) {
    	this.severityPortability = severityPortability;
    }
    public boolean getseverityPortability() {
        return this.severityPortability;
    }
    @DataBoundSetter
    public void setdisplayAllErrors(boolean displayAllErrors) {
    	this.displayAllErrors = displayAllErrors;
    }
    public boolean getdisplayAllErrors() {
        return this.displayAllErrors;
    }
    @DataBoundSetter
    public void setdisplayErrorSeverity(boolean displayErrorSeverity) {
    	this.displayErrorSeverity = displayErrorSeverity;
    }
    public boolean getdisplayErrorSeverity() {
        return this.displayErrorSeverity;
    }
    @DataBoundSetter
    public void setdisplayWarningSeverity(boolean displayWarningSeverity) {
    	this.displayWarningSeverity = displayWarningSeverity;
    }
    public boolean getdisplayWarningSeverity() {
        return this.displayWarningSeverity;
    }
    @DataBoundSetter
    public void setdisplayStyleSeverity(boolean displayStyleSeverity) {
    	this.displayStyleSeverity = displayStyleSeverity;
    }
    public boolean getdisplayStyleSeverity() {
        return this.displayStyleSeverity;
    }
    @DataBoundSetter
    public void setdisplayPerformanceSeverity(boolean displayPerformanceSeverity) {
    	this.displayPerformanceSeverity = displayPerformanceSeverity;
    }
    public boolean getdisplayPerformanceSeverity() {
        return this.displayPerformanceSeverity;
    }
    @DataBoundSetter
    public void setdisplayNoCategorySeverity(boolean displayNoCategorySeverity) {
    	this.displayNoCategorySeverity = displayNoCategorySeverity;
    }
    public boolean getdisplayNoCategorySeverity() {
        return this.displayNoCategorySeverity;
    }
    @DataBoundSetter
    public void setdisplayPortabilitySeverity(boolean displayPortabilitySeverity) {
    	this.displayPortabilitySeverity = displayPortabilitySeverity;
    }
    public boolean getdisplayPortabilitySeverity() {
        return this.displayPortabilitySeverity;
    }
    @DataBoundSetter
    public void setxSize(int xSize) {
    	this.xSize = xSize;
    }
    public int getxSize() {
        return this.xSize;
    }
    @DataBoundSetter
    public void setySize(int ySize) {
    	this.ySize = ySize;
    }
    public int getySize() {
        return this.ySize;
    }
    @DataBoundSetter
    public void setnumBuildsInGraph(int numBuildsInGraph) {
    	this.numBuildsInGraph = numBuildsInGraph;
    }
    public int getnumBuildsInGraph() {
        return this.numBuildsInGraph;
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
        return BuildStepMonitor.NONE;
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
