package org.jenkinsci.plugins.cppcheck.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfigSeverityEvaluation implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

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

    private boolean severityNoCategory = true;

    private boolean severityPortability = true;

    public CppcheckConfigSeverityEvaluation() {
    }

    public CppcheckConfigSeverityEvaluation(String threshold, String newThreshold,
            String failureThreshold, String newFailureThreshold, String healthy,
            String unHealthy, boolean severityError, boolean severityWarning,
            boolean severityStyle, boolean severityPerformance,
            boolean severityInformation, boolean severityNoCategory,
            boolean severityPortability) {
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
        this.severityNoCategory = severityNoCategory;
        this.severityPortability = severityPortability;
    }

    public String getThreshold() {
        return threshold;
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

    public boolean isSeverityNoCategory() {
        return severityNoCategory;
    }

    public boolean isSeverityPortability() {
        return severityPortability;
    }
}
