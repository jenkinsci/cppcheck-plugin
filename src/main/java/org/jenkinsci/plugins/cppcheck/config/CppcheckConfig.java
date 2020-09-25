package org.jenkinsci.plugins.cppcheck.config;
import org.kohsuke.stapler.DataBoundSetter;


import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfig implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private String pattern;
    private String baselinePattern;
    private boolean ignoreBlankFiles;
    private boolean allowNoReport;
    private CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation();
    private CppcheckConfigGraph configGraph = new CppcheckConfigGraph();

    @DataBoundSetter
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
   
    @DataBoundSetter
    public void setBaselinePattern(String baselinePattern) {
    	this.baselinePattern = baselinePattern;
    }

    @DataBoundSetter
    public void setIgnoreBlankFiles(boolean ignoreBlankFiles) {
        this.ignoreBlankFiles = ignoreBlankFiles;
    }
    @DataBoundSetter
    public void setAllowNoReport(boolean allowNoReport) {
        this.allowNoReport = allowNoReport;
    }
    @DataBoundSetter
    public void setConfigSeverityEvaluation(CppcheckConfigSeverityEvaluation configSeverityEvaluation) {
        this.configSeverityEvaluation = configSeverityEvaluation;
    }
    @DataBoundSetter
    public void setConfigGraph(CppcheckConfigGraph configGraph) {
        this.configGraph = configGraph;
    }
    @DataBoundSetter
    public void setCppcheckReportPattern(String cppcheckReportPattern) {
        this.cppcheckReportPattern = cppcheckReportPattern;
    }
    @DataBoundSetter
    public void setUseWorkspaceAsRootPath(boolean useWorkspaceAsRootPath) {
        this.useWorkspaceAsRootPath = useWorkspaceAsRootPath;
    }

    public String getPattern() {
        return pattern;
    }
    
    public String getBaselinePattern() {
    	return baselinePattern;
    }
    
    public boolean isHasBaselinePattern() {
    	return getBaselinePattern() != null;
    }

    @Deprecated
    public String getCppcheckReportPattern() {
        return cppcheckReportPattern;
    }

    public boolean isUseWorkspaceAsRootPath() {
        return useWorkspaceAsRootPath;
    }

    public boolean getIgnoreBlankFiles() {
        return ignoreBlankFiles;
    }
    public boolean isIgnoreBlankFiles() {
        return ignoreBlankFiles;
    }

    public boolean getAllowNoReport() {
        return allowNoReport;
    }

    public CppcheckConfigSeverityEvaluation getConfigSeverityEvaluation() {
        return configSeverityEvaluation;
    }

    public CppcheckConfigGraph getConfigGraph() {
        return configGraph;
    }

    /*
    Backward compatibility
     */
    private transient String cppcheckReportPattern;
    private transient boolean useWorkspaceAsRootPath;

    private Object readResolve() {
        if (this.cppcheckReportPattern != null) {
            this.pattern = cppcheckReportPattern;
        }
        return this;
    }
}
