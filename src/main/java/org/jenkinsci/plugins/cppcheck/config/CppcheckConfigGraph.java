package org.jenkinsci.plugins.cppcheck.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfigGraph implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_CHART_WIDTH = 500;
    public static final int DEFAULT_CHART_HEIGHT = 200;

    private int xSize = DEFAULT_CHART_WIDTH;
    private int ySize = DEFAULT_CHART_HEIGHT;
    private int numBuildsInGraph = 0; // numBuildsInGraph <= 0 means unlimited
    private boolean displayAllErrors = true;
    private boolean displayErrorSeverity = false;
    private boolean displayWarningSeverity = false;
    private boolean displayStyleSeverity = false;
    private boolean displayPerformanceSeverity = false;
    private boolean displayInformationSeverity = false;
    private boolean displayNoCategorySeverity = false;
    private boolean displayPortabilitySeverity = false;

    public CppcheckConfigGraph() {
    }

    public CppcheckConfigGraph(int xSize, int ySize, int numBuildsInGraph,
            boolean displayAllErrors,
            boolean displayErrorSeverity, boolean displayWarningSeverity,
            boolean displayStyleSeverity, boolean displayPerformanceSeverity,
            boolean displayInformationSeverity, boolean displayNoCategorySeverity,
            boolean displayPortabilitySeverity) {
    	if ( xSize > 0 && ySize > 0) {
    		this.xSize = xSize;
    		this.ySize = ySize;
    	}
        this.numBuildsInGraph = numBuildsInGraph;
        this.displayAllErrors = displayAllErrors;
        this.displayErrorSeverity = displayErrorSeverity;
        this.displayWarningSeverity = displayWarningSeverity;
        this.displayStyleSeverity = displayStyleSeverity;
        this.displayPerformanceSeverity = displayPerformanceSeverity;
        this.displayInformationSeverity = displayInformationSeverity;
        this.displayNoCategorySeverity = displayNoCategorySeverity;
        this.displayPortabilitySeverity = displayPortabilitySeverity;
    }

    public void setXSize(int xSize) {
        if(xSize>0){this.xSize = xSize; }
    }
    public int getXSize() {
        return xSize;
    }
    public void setYSize(int ySize) {
        if(ySize>0){this.ySize = ySize; }
    }
    public int getYSize() {
        return ySize;
    }
    public void setNumBuildsInGraph(int numBuildsInGraph) {
        this.numBuildsInGraph = numBuildsInGraph;
    }
    public int getNumBuildsInGraph() {
        return numBuildsInGraph;
    }
    public void setDisplayAllErrors(boolean displayAllErrors) {
        this.displayAllErrors = displayAllErrors;
    }
    public boolean isDisplayAllErrors() {
        return displayAllErrors;
    }
    public void setDisplayErrorSeverity(boolean displayErrorSeverity) {
        this.displayErrorSeverity = displayErrorSeverity;
    }
    public boolean isDisplayErrorSeverity() {
        return displayErrorSeverity;
    }
    public void setDisplayWarningSeverity(boolean displayWarningSeverity) {
        this.displayWarningSeverity = displayWarningSeverity;
    }
    public boolean isDisplayWarningSeverity() {
        return displayWarningSeverity;
    }
    public void setDisplayStyleSeverity(boolean displayStyleSeverity) {
        this.displayStyleSeverity = displayStyleSeverity;
    }
    public boolean isDisplayStyleSeverity() {
        return displayStyleSeverity;
    }
    public void setDisplayPerformanceSeverity(boolean displayPerformanceSeverity) {
        this.displayPerformanceSeverity = displayPerformanceSeverity;
    }
    public boolean isDisplayPerformanceSeverity() {
        return displayPerformanceSeverity;
    }
    public void setDisplayInformationSeverity(boolean displayInformationSeverity) {
        this.displayInformationSeverity = displayInformationSeverity;
    }
    public boolean isDisplayInformationSeverity() {
        return displayInformationSeverity;
    }
    public void setDisplayNoCategorySeverity(boolean displayNoCategorySeverity) {
        this.displayNoCategorySeverity = displayNoCategorySeverity;
    }
    public boolean isDisplayNoCategorySeverity() {
        return displayNoCategorySeverity;
    }
    public void setDisplayPortabilitySeverity(boolean displayPortabilitySeverity) {
        this.displayPortabilitySeverity = displayPortabilitySeverity;
    }
    public boolean isDisplayPortabilitySeverity() {
        return displayPortabilitySeverity;
    }
}