package com.thalesgroup.hudson.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.config.CppcheckConfig;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

/**
 * @author Gregory Boissinot
 */
@Deprecated
public class CppcheckPublisher extends Recorder {

    private transient CppcheckConfig cppcheckConfig;

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public static final CppcheckDescriptor DESCRIPTOR = new CppcheckDescriptor();

    /**
     * The Cppcheck Descriptor
     */
    public static final class CppcheckDescriptor extends BuildStepDescriptor<Publisher> {

        @SuppressWarnings("deprecation")
        public CppcheckDescriptor() {
            super(CppcheckPublisher.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Publish Cppcheck results";
        }

    }


    @SuppressWarnings("unused")
    private Object readResolve() {

        org.jenkinsci.plugins.cppcheck.config.CppcheckConfig config = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfig();
        config.setPattern(cppcheckConfig.getCppcheckReportPattern());
        config.setIgnoreBlankFiles(cppcheckConfig.isIgnoreBlankFiles());

        org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation configSeverityEvaluation = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation(
                cppcheckConfig.getConfigSeverityEvaluation().getThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getNewThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getFailureThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getNewFailureThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getHealthy(),
                cppcheckConfig.getConfigSeverityEvaluation().getUnHealthy(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityError(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityPossibleError(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityStyle(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle(),
                true, true, true);
        config.setConfigSeverityEvaluation(configSeverityEvaluation);

        org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph configGraph = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph(
                cppcheckConfig.getConfigGraph().getXSize(),
                cppcheckConfig.getConfigGraph().getYSize(),
                0,
                cppcheckConfig.getConfigGraph().isDiplayAllError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityPossibleError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityStyle(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityPossibleStyle(),
                true, true, true);
        config.setConfigGraph(configGraph);

        org.jenkinsci.plugins.cppcheck.CppcheckPublisher cppcheckPublisher = new org.jenkinsci.plugins.cppcheck.CppcheckPublisher(config);
        return cppcheckPublisher;
    }

}