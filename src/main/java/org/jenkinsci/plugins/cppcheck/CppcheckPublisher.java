package org.jenkinsci.plugins.cppcheck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.jenkinsci.Symbol;
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

    private CppcheckConfig config;

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

        config = new CppcheckConfig();

        config.setPattern(pattern);
        config.setAllowNoReport(allowNoReport);
        config.setIgnoreBlankFiles(ignoreBlankFiles);
        CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation(
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy, unHealthy,
                severityError,
                severityWarning,
                severityStyle,
                severityPerformance,
                severityInformation,
                severityNoCategory,
                severityPortability);
        config.setConfigSeverityEvaluation(configSeverityEvaluation);
        CppcheckConfigGraph configGraph = new CppcheckConfigGraph(
                xSize, ySize, numBuildsInGraph,
                displayAllErrors,
                displayErrorSeverity,
                displayWarningSeverity,
                displayStyleSeverity,
                displayPerformanceSeverity,
                displayInformationSeverity,
                displayNoCategorySeverity,
                displayPortabilitySeverity);
        config.setConfigGraph(configGraph);
    }

    @DataBoundSetter
    public void setPattern(String pattern) {
        config.setPattern(pattern);
    }
    public String getPattern() {
        return config.getPattern();
    }
    @DataBoundSetter
    public void setThreshold(String threshold) {
        config.getConfigSeverityEvaluation().setThreshold(threshold);
    }
    public String getThreshold() {
        return config.getConfigSeverityEvaluation().getThreshold();
    }

    @DataBoundSetter
    public void setNewThreshold(String newThreshold) {
        config.getConfigSeverityEvaluation().setNewThreshold(newThreshold);
    }
    public String getNewThreshold() {
        return config.getConfigSeverityEvaluation().getNewThreshold();
    }
    @DataBoundSetter
    public void setFailureThreshold(String failureThreshold) {
        config.getConfigSeverityEvaluation().setFailureThreshold(failureThreshold);
    }
    public String getFailureThreshold() {
        return config.getConfigSeverityEvaluation().getFailureThreshold();
    }
    @DataBoundSetter
    public void setNewFailureThreshold(String newFailureThreshold) {
        config.getConfigSeverityEvaluation().setNewFailureThreshold(newFailureThreshold);
    }
    public String getNewFailureThreshold() {
        return config.getConfigSeverityEvaluation().getNewFailureThreshold();
    }
    @DataBoundSetter
    public void setHealthy(String healthy) {
        config.getConfigSeverityEvaluation().setHealthy(healthy);
    }
    public String getHealthy() {
        return config.getConfigSeverityEvaluation().getHealthy();
    }
    @DataBoundSetter
    public void setUnHealthy(String unHealthy) {
        config.getConfigSeverityEvaluation().setUnHealthy(unHealthy);
    }
    public String getUnHealthy() {
        return config.getConfigSeverityEvaluation().getUnHealthy();
    }
    @DataBoundSetter
    public void setIgnoreBlankFiles(boolean ignoreBlankFiles) {
        config.setIgnoreBlankFiles(ignoreBlankFiles);
    }
    public boolean getIgnoreBlankFiles() {
        return config.getIgnoreBlankFiles();
    }
    public boolean isIgnoreBlankFiles() {
        return config.isIgnoreBlankFiles();
    }
    @DataBoundSetter
    public void setAllowNoReport(boolean allowNoReport) {
        config.setAllowNoReport(allowNoReport);
    }
    public boolean getAllowNoReport() {
        return config.getAllowNoReport();
    }
    @DataBoundSetter
    public void setSeverityError(boolean severityError) {
        config.getConfigSeverityEvaluation().setSeverityError(severityError);
    }
    public boolean getSeverityError() {
        return config.getConfigSeverityEvaluation().getSeverityError();
    }
    @DataBoundSetter
    public void setSeverityWarning(boolean severityWarning) {
        config.getConfigSeverityEvaluation().setSeverityWarning(severityWarning);
    }
    public boolean getSeverityWarning() {
        return config.getConfigSeverityEvaluation().getSeverityWarning();
    }
    @DataBoundSetter
    public void setSeverityStyle(boolean severityStyle) {
        config.getConfigSeverityEvaluation().setSeverityStyle(severityStyle);
    }
    public boolean getSeverityStyle() {
        return config.getConfigSeverityEvaluation().getSeverityStyle();
    }
    @DataBoundSetter
    public void setSeverityPerformance(boolean severityPerformance) {
        config.getConfigSeverityEvaluation().setSeverityPerformance(severityPerformance);
    }
    public boolean getSeverityPerformance() {
        return config.getConfigSeverityEvaluation().getSeverityPerformance();
    }
    @DataBoundSetter
    public void setSeverityInformation(boolean severityInformation) {
        config.getConfigSeverityEvaluation().setSeverityInformation(severityInformation);
    }
    public boolean getSeverityInformation() {
        return config.getConfigSeverityEvaluation().getSeverityInformation();
    }
    @DataBoundSetter
    public void setSeverityNoCategory(boolean severityNoCategory) {
        config.getConfigSeverityEvaluation().setSeverityNoCategory(severityNoCategory);
    }
    public boolean getSeverityNoCategory() {
        return config.getConfigSeverityEvaluation().getSeverityNoCategory();
    }
    @DataBoundSetter
    public void setSeverityPortability(boolean severityPortability) {
        config.getConfigSeverityEvaluation().setSeverityPortability(severityPortability);
    }
    public boolean getSeverityPortability() {
        return config.getConfigSeverityEvaluation().getSeverityPortability();
    }
    @DataBoundSetter
    public void setDisplayAllErrors(boolean displayAllErrors) {
        config.getConfigGraph().setDisplayAllErrors(displayAllErrors);
    }
    public boolean getDisplayAllErrors() {
        return config.getConfigGraph().getDisplayAllErrors();
    }
    @DataBoundSetter
    public void setDisplayErrorSeverity(boolean displayErrorSeverity) {
        config.getConfigGraph().setDisplayErrorSeverity(displayErrorSeverity);
    }
    public boolean getDisplayErrorSeverity() {
        return config.getConfigGraph().getDisplayErrorSeverity();
    }
    @DataBoundSetter
    public void setDisplayWarningSeverity(boolean displayWarningSeverity) {
        config.getConfigGraph().setDisplayWarningSeverity(displayWarningSeverity);
    }
    public boolean getDisplayWarningSeverity() {
        return config.getConfigGraph().getDisplayWarningSeverity();
    }
    @DataBoundSetter
    public void setDisplayStyleSeverity(boolean displayStyleSeverity) {
        config.getConfigGraph().setDisplayStyleSeverity(displayStyleSeverity);
    }
    public boolean getDisplayStyleSeverity() {
        return config.getConfigGraph().getDisplayStyleSeverity();
    }
    @DataBoundSetter
    public void setDisplayPerformanceSeverity(boolean displayPerformanceSeverity) {
        config.getConfigGraph().setDisplayPerformanceSeverity(displayPerformanceSeverity);
    }
    public boolean getDisplayPerformanceSeverity() {
        return config.getConfigGraph().getDisplayPerformanceSeverity();
    }
    @DataBoundSetter
    public void setDisplayNoCategorySeverity(boolean displayNoCategorySeverity) {
        config.getConfigGraph().setDisplayNoCategorySeverity(displayNoCategorySeverity);
    }
    public boolean getDisplayNoCategorySeverity() {
        return config.getConfigGraph().getDisplayNoCategorySeverity();
    }
    @DataBoundSetter
    public void setDisplayPortabilitySeverity(boolean displayPortabilitySeverity) {
        config.getConfigGraph().setDisplayPortabilitySeverity(displayPortabilitySeverity);
    }
    public boolean getDisplayPortabilitySeverity() {
        return config.getConfigGraph().getDisplayPortabilitySeverity();
    }
    @DataBoundSetter
    public void setXSize(int xSize) {
        config.getConfigGraph().setXSize(xSize);
    }
    public int getXSize() {
        return config.getConfigGraph().getXSize();
    }
    @DataBoundSetter
    public void setYSize(int ySize) {
        config.getConfigGraph().setYSize(ySize);
    }
    public int getYSize() {
        return config.getConfigGraph().getYSize();
    }
    @DataBoundSetter
    public void setNumBuildsInGraph(int numBuildsInGraph) {
        config.getConfigGraph().setNumBuildsInGraph(numBuildsInGraph);
    }
    public int getNumBuildsInGraph() {
        return config.getConfigGraph().getNumBuildsInGraph();
    }


    @Deprecated
    public CppcheckPublisher(CppcheckConfig config) {
        this.config = config;
    }

    public CppcheckConfig getCppcheckConfig() {
        return config;
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
    	    	
            if (this.canContinue(build.getResult())) {
                CppcheckLogger.log(listener, "Starting the cppcheck analysis.");
                
                EnvVars env = build.getEnvironment(listener);
                String expandedPattern = env.expand(config.getPattern());
                

                CppcheckParserResult parser = new CppcheckParserResult(listener,
                                expandedPattern, config.isIgnoreBlankFiles());
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
                    if (config.getAllowNoReport()) {
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
                        = config.getConfigSeverityEvaluation();

                Result buildResult = new CppcheckBuildResultEvaluator().evaluateBuildResult(
                        listener, result.getNumberErrorsAccordingConfiguration(severityEvaluation, false),
                        result.getNumberErrorsAccordingConfiguration(severityEvaluation, true),
                        severityEvaluation);

                if (buildResult != Result.SUCCESS) {
                    build.setResult(buildResult);
                }

                CppcheckBuildAction buildAction = new CppcheckBuildAction(build, result, config,
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
            String expandedPattern = env.expand(config.getPattern());
            

            CppcheckParserResult parser = new CppcheckParserResult(listener,
                        expandedPattern, config.isIgnoreBlankFiles());
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
                if (config.getAllowNoReport()) {
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
                    = config.getConfigSeverityEvaluation();

            Result buildResult = new CppcheckBuildResultEvaluator().evaluateBuildResult(
                    listener, result.getNumberErrorsAccordingConfiguration(severityEvaluation, false),
                    result.getNumberErrorsAccordingConfiguration(severityEvaluation, true),
                    severityEvaluation);

            if (buildResult != Result.SUCCESS) {
                build.setResult(buildResult);
            }

            CppcheckBuildAction buildAction = new CppcheckBuildAction(build, result, config,
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

    @Extension @Symbol("publishCppcheck")
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

        public String getFunctionName() {
            return "publishCppcheck";
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
}
