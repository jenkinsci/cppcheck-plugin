package org.jenkinsci.plugins.cppcheck.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfigSeverityEvaluation implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private String threshold = "";

    private String newThreshold = "";

    private String failureThreshold = "";

    private String newFailureThreshold = "";

    private String healthy = "";

    private String unHealthy = "";

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

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }
    public String getThreshold() {
        return threshold;
    }
    public void setNewThreshold(String newThreshold) {
       this.newThreshold = newThreshold;
    }
    public String getNewThreshold() {
        return newThreshold;
    }
    public void setFailureThreshold(String failureThreshold) {
       this.failureThreshold = failureThreshold;
    }
    public String getFailureThreshold() {
        return failureThreshold;
    }
    public void setNewFailureThreshold(String newFailureThreshold) {
       this.newFailureThreshold = newFailureThreshold;
    }
    public String getNewFailureThreshold() {
        return newFailureThreshold;
    }
    public void setHealthy(String healthy) {
       this.healthy = healthy;
    }
    public String getHealthy() {
        return healthy;
    }
    public void setUnHealthy(String unHealthy) {
       this.unHealthy = unHealthy;
    }
    public String getUnHealthy() {
        return unHealthy;
    }
    public void setSeverityError(boolean severityError) {
       this.severityError = severityError;
    }
    public boolean isSeverityError() {
        return severityError;
    }
    public void setSeverityWarning(boolean severityWarning) {
       this.severityWarning = severityWarning;
    }
    public boolean isSeverityWarning() {
        return severityWarning;
    }
    public void setSeverityStyle(boolean severityStyle) {
       this.severityStyle = severityStyle;
    }
    public boolean isSeverityStyle() {
        return severityStyle;
    }
    public void setSeverityPerformance(boolean severityPerformance) {
       this.severityPerformance = severityPerformance;
    }
    public boolean isSeverityPerformance() {
        return severityPerformance;
    }
    public void setSeverityInformation(boolean severityInformation) {
       this.severityInformation = severityInformation;
    }
    public boolean isSeverityInformation() {
        return severityInformation;
    }
    public void setSeverityNoCategory(boolean severityNoCategory) {
       this.severityNoCategory = severityNoCategory;
    }
    public boolean isSeverityNoCategory() {
        return severityNoCategory;
    }
    public void setSeverityPortability(boolean severityPortability) {
       this.severityPortability = severityPortability;
    }
    public boolean isSeverityPortability() {
        return severityPortability;
    }
}
