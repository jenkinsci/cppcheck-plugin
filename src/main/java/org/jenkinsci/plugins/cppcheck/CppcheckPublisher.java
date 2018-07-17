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
        CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation(
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy, unHealthy,
                severityError,
                severityWarning,
                severityStyle,
                severityPerformance,
                severityInformation,
                severityNoCategory,
                severityPortability);
        cppcheckConfig.setConfigSeverityEvaluation(configSeverityEvaluation);
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
        cppcheckConfig.setConfigGraph(configGraph);
    }

    @DataBoundSetter
    public void setPattern(String pattern) {
    	this.pattern = pattern;
    }
    public String getPattern() {
        return pattern;
    }
    @DataBoundSetter
    public void setNewThreshold(String newThreshold) {
    	this.newThreshold = newThreshold;
    }
    public String getNewThreshold() {
        return this.newThreshold;
    }
    @DataBoundSetter
    public void setFailureThreshold(String failureThreshold) {
    	this.failureThreshold = failureThreshold;
    }
    public String getFailureThreshold() {
        return this.failureThreshold;
    }
    @DataBoundSetter
    public void setNewFailureThreshold(String newFailureThreshold) {
    	this.newFailureThreshold = newFailureThreshold;
    }
    public String getNewFailureThreshold() {
        return this.newFailureThreshold;
    }
    @DataBoundSetter
    public void setHealthy(String healthy) {
    	this.healthy = healthy;
    }
    public String getHealthy() {
        return this.healthy;
    }
    @DataBoundSetter
    public void setUnHealthy(String unHealthy) {
    	this.unHealthy = unHealthy;
    }
    public String getUnHealthy() {
        return this.unHealthy;
    }
    @DataBoundSetter
    public void setIgnoreBlankFiles(boolean ignoreBlankFiles) {
    	this.ignoreBlankFiles = ignoreBlankFiles;
    }
    public boolean getIgnoreBlankFiles() {
        return this.ignoreBlankFiles;
    }
    @DataBoundSetter
    public void setAllowNoReport(boolean allowNoReport) {
    	this.allowNoReport = allowNoReport;
    }
    public boolean getAllowNoReport() {
        return this.allowNoReport;
    }
    @DataBoundSetter
    public void setSeverityError(boolean severityError) {
    	this.severityError = severityError;
    }
    public boolean getSeverityError() {
        return this.severityError;
    }
    @DataBoundSetter
    public void setSeverityWarning(boolean severityWarning) {
    	this.severityWarning = severityWarning;
    }
    public boolean getSeverityWarning() {
        return this.severityWarning;
    }
    @DataBoundSetter
    public void setSeverityStyle(boolean severityStyle) {
    	this.severityStyle = severityStyle;
    }
    public boolean getSeverityStyle() {
        return this.severityStyle;
    }
    @DataBoundSetter
    public void setSeverityPerformance(boolean severityPerformance) {
    	this.severityPerformance = severityPerformance;
    }
    public boolean getSeverityPerformance() {
        return this.severityPerformance;
    }
    @DataBoundSetter
    public void setSeverityInformation(boolean severityInformation) {
    	this.severityInformation = severityInformation;
    }
    public boolean getSeverityInformation() {
        return this.severityInformation;
    }
    @DataBoundSetter
    public void setSeverityNoCategory(boolean severityNoCategory) {
    	this.severityNoCategory = severityNoCategory;
    }
    public boolean getSeverityNoCategory() {
        return this.severityNoCategory;
    }
    @DataBoundSetter
    public void setSeverityPortability(boolean severityPortability) {
    	this.severityPortability = severityPortability;
    }
    public boolean getSeverityPortability() {
        return this.severityPortability;
    }
    @DataBoundSetter
    public void setDisplayAllErrors(boolean displayAllErrors) {
    	this.displayAllErrors = displayAllErrors;
    }
    public boolean getDisplayAllErrors() {
        return this.displayAllErrors;
    }
    @DataBoundSetter
    public void setDisplayErrorSeverity(boolean displayErrorSeverity) {
    	this.displayErrorSeverity = displayErrorSeverity;
    }
    public boolean getDisplayErrorSeverity() {
        return this.displayErrorSeverity;
    }
    @DataBoundSetter
    public void setDisplayWarningSeverity(boolean displayWarningSeverity) {
    	this.displayWarningSeverity = displayWarningSeverity;
    }
    public boolean getDisplayWarningSeverity() {
        return this.displayWarningSeverity;
    }
    @DataBoundSetter
    public void setDisplayStyleSeverity(boolean displayStyleSeverity) {
    	this.displayStyleSeverity = displayStyleSeverity;
    }
    public boolean getDisplayStyleSeverity() {
        return this.displayStyleSeverity;
    }
    @DataBoundSetter
    public void setDisplayPerformanceSeverity(boolean displayPerformanceSeverity) {
    	this.displayPerformanceSeverity = displayPerformanceSeverity;
    }
    public boolean getDisplayPerformanceSeverity() {
        return this.displayPerformanceSeverity;
    }
    @DataBoundSetter
    public void setDisplayNoCategorySeverity(boolean displayNoCategorySeverity) {
    	this.displayNoCategorySeverity = displayNoCategorySeverity;
    }
    public boolean getDisplayNoCategorySeverity() {
        return this.displayNoCategorySeverity;
    }
    @DataBoundSetter
    public void setDisplayPortabilitySeverity(boolean displayPortabilitySeverity) {
    	this.displayPortabilitySeverity = displayPortabilitySeverity;
    }
    public boolean getDisplayPortabilitySeverity() {
        return this.displayPortabilitySeverity;
    }
    @DataBoundSetter
    public void setXSize(int xSize) {
    	this.xSize = xSize;
    }
    public int getXSize() {
        return this.xSize;
    }
    @DataBoundSetter
    public void setYSize(int ySize) {
    	this.ySize = ySize;
    }
    public int getYSize() {
        return this.ySize;
    }
    @DataBoundSetter
    public void setNumBuildsInGraph(int numBuildsInGraph) {
    	this.numBuildsInGraph = numBuildsInGraph;
    }
    public int getNumBuildsInGraph() {
        return this.numBuildsInGraph;
    }


    @Deprecated
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
    	    	
            if (this.canContinue(build.getResult())) {
                CppcheckLogger.log(listener, "Starting the cppcheck analysis.");
                
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
