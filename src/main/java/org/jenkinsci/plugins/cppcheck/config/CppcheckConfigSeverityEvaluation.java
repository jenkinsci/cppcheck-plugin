package org.jenkinsci.plugins.cppcheck.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfigSeverityEvaluation implements Serializable {

    private String threshold;

    private String newThreshold;

    private String failureThreshold;

    private String newFailureThreshold;

    private String healthy;

    private String unHealthy;

    private boolean severityError = true;

    private boolean severityWarning = true;

    private boolean severityStyle = true;

    private boolean severityPerformance = true;

    private boolean severityInformation = true;

    public CppcheckConfigSeverityEvaluation() {
    }

    public CppcheckConfigSeverityEvaluation(String threshold, String newThreshold, String failureThreshold, String newFailureThreshold, String healthy, String unHealthy, boolean severityError, boolean severityWarning, boolean severityStyle, boolean severityPerformance, boolean severityInformation) {
        this.threshold = threshold;
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
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getNewThreshold() {
        return newThreshold;
    }

    public String getFailureThreshold() {
        return failureThreshold;
    }

    public String getNewFailureThreshold() {
        return newFailureThreshold;
    }

    public String getHealthy() {
        return healthy;
    }

    public String getUnHealthy() {
        return unHealthy;
    }

    public boolean isSeverityError() {
        return severityError;
    }

    public boolean isSeverityWarning() {
        return severityWarning;
    }

    public boolean isSeverityStyle() {
        return severityStyle;
    }

    public boolean isSeverityPerformance() {
        return severityPerformance;
    }

    public boolean isSeverityInformation() {
        return severityInformation;
    }
}
