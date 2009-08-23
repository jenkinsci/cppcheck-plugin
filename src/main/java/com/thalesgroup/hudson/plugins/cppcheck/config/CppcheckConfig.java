package com.thalesgroup.hudson.plugins.cppcheck.config;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

public class CppcheckConfig implements Serializable{

    private static final long serialVersionUID = 1L;

    private String cppcheckReportPattern;
	           
    private CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation();   	   
    
    private CppcheckConfigGraph configGraph = new CppcheckConfigGraph();
    
    public CppcheckConfig(){}
    
    @DataBoundConstructor
    public CppcheckConfig(String cppcheckReportPattern, String threshold,
			String newThreshold, String failureThreshold,
			String newFailureThreshold, String healthy, String unHealthy,
			boolean severityError, boolean severityPossibleError,
			boolean severityStyle, boolean severityPossibleStyle, int xSize, int ySize, boolean diplayAllError,
			boolean displaySeverityError, boolean displaySeverityPossibleError,
			boolean displaySeverityStyle, boolean displaySeverityPossibleStyle) {

		this.cppcheckReportPattern = cppcheckReportPattern;
		
		this.configSeverityEvaluation = new CppcheckConfigSeverityEvaluation(
				threshold, newThreshold, failureThreshold,newFailureThreshold, healthy, 
				unHealthy,severityError, severityPossibleError,severityStyle, severityPossibleStyle);

		this.configGraph=new CppcheckConfigGraph(xSize, ySize, diplayAllError,
			displaySeverityError, displaySeverityPossibleError,
			displaySeverityStyle, displaySeverityPossibleStyle);
	}

	public String getCppcheckReportPattern() {
		return cppcheckReportPattern;
	}

	public void setCppcheckReportPattern(String cppcheckReportPattern) {
		this.cppcheckReportPattern = cppcheckReportPattern;
	}

	public CppcheckConfigSeverityEvaluation getConfigSeverityEvaluation() {
		return configSeverityEvaluation;
	}

	public void setConfigSeverityEvaluation(
			CppcheckConfigSeverityEvaluation configSeverityEvaluation) {
		this.configSeverityEvaluation = configSeverityEvaluation;
	}

	public CppcheckConfigGraph getConfigGraph() {
		return configGraph;
	}

	public void setConfigGraph(CppcheckConfigGraph configGraph) {
		this.configGraph = configGraph;
	}

}
