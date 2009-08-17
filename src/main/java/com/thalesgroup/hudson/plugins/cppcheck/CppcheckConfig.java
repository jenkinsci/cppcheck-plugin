package com.thalesgroup.hudson.plugins.cppcheck;

import org.kohsuke.stapler.DataBoundConstructor;

public class CppcheckConfig {

    private String cppcheckReportPattern;

    private String threshold;

    private String newThreshold;

    private String failureThreshold;

    private String newFailureThreshold;

    private String healthy;

    private String unHealthy;

    private boolean severityError = true;
    
    private boolean severityPossibleError = true;
    
    private boolean severityStyle = true;
    
    private boolean severityPossibleStyle = true;
	           
    public CppcheckConfig(){    	
    }
    
    @DataBoundConstructor
    public CppcheckConfig(String cppcheckReportPattern, String threshold,
			String newThreshold, String failureThreshold,
			String newFailureThreshold, String healthy, String unHealthy,
			boolean severityError, boolean severityPossibleError,
			boolean severityStyle, boolean severityPossibleStyle) {

		this.cppcheckReportPattern = cppcheckReportPattern;
		this.threshold = threshold;
		this.newThreshold = newThreshold;
		this.failureThreshold = failureThreshold;
		this.newFailureThreshold = newFailureThreshold;
		this.healthy = healthy;
		this.unHealthy = unHealthy;
		this.severityError = severityError;
		this.severityPossibleError = severityPossibleError;
		this.severityStyle = severityStyle;
		this.severityPossibleStyle = severityPossibleStyle;
	}

	public String getCppcheckReportPattern() {
		return cppcheckReportPattern;
	}

	public void setCppcheckReportPattern(String cppcheckReportPattern) {
		this.cppcheckReportPattern = cppcheckReportPattern;
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

	public void setNewThreshold(String newThreshold) {
		this.newThreshold = newThreshold;
	}

	public String getFailureThreshold() {
		return failureThreshold;
	}

	public void setFailureThreshold(String failureThreshold) {
		this.failureThreshold = failureThreshold;
	}

	public String getNewFailureThreshold() {
		return newFailureThreshold;
	}

	public void setNewFailureThreshold(String newFailureThreshold) {
		this.newFailureThreshold = newFailureThreshold;
	}

	public String getHealthy() {
		return healthy;
	}

	public void setHealthy(String healthy) {
		this.healthy = healthy;
	}

	public String getUnHealthy() {
		return unHealthy;
	}

	public void setUnHealthy(String unHealthy) {
		this.unHealthy = unHealthy;
	}

	public boolean isSeverityError() {
		return severityError;
	}

	public void setseverityError(boolean severityError) {
		this.severityError = severityError;
	}

	public boolean isSeverityPossibleError() {
		return severityPossibleError;
	}

	public void setseverityPossibleError(boolean severityPossibleError) {
		this.severityPossibleError = severityPossibleError;
	}

	public boolean isSeverityStyle() {
		return severityStyle;
	}

	public void setseverityStyle(boolean severityStyle) {
		this.severityStyle = severityStyle;
	}

	public boolean isSeverityPossibleStyle() {
		return severityPossibleStyle;
	}

	public void setseverityPossibleStyle(boolean severityPossibleStyle) {
		this.severityPossibleStyle = severityPossibleStyle;
	}

	public boolean isAllSeverities(){
		return isSeverityError() && isSeverityPossibleError() && isSeverityPossibleStyle() && isSeverityStyle();
	}
}
