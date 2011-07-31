package org.jenkinsci.plugins.cppcheck.config;


import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfig implements Serializable {

    private String cppcheckReportPattern;

    private boolean ignoreBlankFiles;

    private CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation();

    private CppcheckConfigGraph configGraph = new CppcheckConfigGraph();

    public CppcheckConfig() {
    }

    @DataBoundConstructor
    @SuppressWarnings("unused")
    public CppcheckConfig(String cppcheckReportPattern,
                          boolean ignoreBlankFiles, String threshold,
                          String newThreshold, String failureThreshold,
                          String newFailureThreshold, String healthy, String unHealthy,
                          boolean severityError,
                          boolean severityWarning,
                          boolean severityStyle,
                          boolean severityPerformance,
                          boolean severityInformation,
                          int xSize, int ySize,
                          boolean displayAllErrors,
                          boolean displayErrorSeverity,
                          boolean displayWarningSeverity,
                          boolean displayStyleSeverity,
                          boolean displayPerformanceSeverity,
                          boolean displayInformationSeverity) {

        this.cppcheckReportPattern = cppcheckReportPattern;
        this.ignoreBlankFiles = ignoreBlankFiles;
        this.configSeverityEvaluation = new CppcheckConfigSeverityEvaluation(
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy, unHealthy,
                severityError,
                severityWarning,
                severityStyle,
                severityPerformance,
                severityInformation);
        this.configGraph = new CppcheckConfigGraph(
                xSize, ySize,
                displayAllErrors,
                displayErrorSeverity,
                displayWarningSeverity,
                displayStyleSeverity,
                displayPerformanceSeverity,
                displayInformationSeverity);
    }

    public String getCppcheckReportPattern() {
        return cppcheckReportPattern;
    }

    public boolean isIgnoreBlankFiles() {
        return ignoreBlankFiles;
    }

    public CppcheckConfigSeverityEvaluation getConfigSeverityEvaluation() {
        return configSeverityEvaluation;
    }

    public CppcheckConfigGraph getConfigGraph() {
        return configGraph;
    }
}
