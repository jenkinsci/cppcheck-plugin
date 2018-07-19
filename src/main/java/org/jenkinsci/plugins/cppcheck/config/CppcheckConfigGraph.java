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
    private int numBuildsInGraph = 0; // numBuildsInGraph <= 1 means unlimited
    private boolean displayAllErrors = true;
    private boolean displayErrorSeverity;
    private boolean displayWarningSeverity;
    private boolean displayStyleSeverity;
    private boolean displayPerformanceSeverity;
    private boolean displayInformationSeverity;
    private boolean displayNoCategorySeverity;
    private boolean displayPortabilitySeverity;

    public CppcheckConfigGraph() {
    }

    public CppcheckConfigGraph(int xSize, int ySize, int numBuildsInGraph,
            boolean displayAllErrors,
            boolean displayErrorSeverity, boolean displayWarningSeverity,
            boolean displayStyleSeverity, boolean displayPerformanceSeverity,
            boolean displayInformationSeverity, boolean displayNoCategorySeverity,
            boolean displayPortabilitySeverity) {
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
    }

    public int getXSize() {
        return xSize;
    }
    public void setXSize(int xSize) {
        this.xSize = xSize;
    }
    public int getYSize() {
        return ySize;
    }
    public void setYSize(int ySize) {
        this.ySize = ySize;
    }

    public int getNumBuildsInGraph() {
        return numBuildsInGraph;
    }
    public void setNumBuildsInGraph(int numBuildsInGraph) {
        this.numBuildsInGraph = numBuildsInGraph;
    }

    public boolean isDisplayAllErrors() {
        return displayAllErrors;
    }
    public boolean getDisplayAllErrors() {
        return displayAllErrors;
    }
    public void setDisplayAllErrors(boolean displayAllErrors) {
        this.displayAllErrors = displayAllErrors;
    }

    public boolean isDisplayErrorSeverity() {
        return displayErrorSeverity;
    }
    public boolean getDisplayErrorSeverity() {
        return displayErrorSeverity;
    }
    public void setDisplayErrorSeverity(boolean displayErrorSeverity) {
        this.displayErrorSeverity = displayErrorSeverity;
    }

    public boolean isDisplayWarningSeverity() {
        return displayWarningSeverity;
    }
    public boolean getDisplayWarningSeverity() {
        return displayWarningSeverity;
    }
    public void setDisplayWarningSeverity(boolean displayWarningSeverity) {
        this.displayWarningSeverity = displayWarningSeverity;
    }

    public boolean isDisplayStyleSeverity() {
        return displayStyleSeverity;
    }
    public boolean getDisplayStyleSeverity() {
        return displayStyleSeverity;
    }
    public void setDisplayStyleSeverity(boolean displayStyleSeverity) {
        this.displayStyleSeverity = displayStyleSeverity;
    }

    public boolean isDisplayPerformanceSeverity() {
        return displayPerformanceSeverity;
    }
    public boolean getDisplayPerformanceSeverity() {
        return displayPerformanceSeverity;
    }
    public void setDisplayPerformanceSeverity(boolean displayPerformanceSeverity) {
        this.displayPerformanceSeverity = displayPerformanceSeverity;
    }
    public boolean isDisplayInformationSeverity() {
        return displayInformationSeverity;
    }
    public boolean getDisplayInformationSeverity() {
        return displayInformationSeverity;
    }
    public void setDisplayInformationSeverity(boolean displayInformationSeverity) {
        this.displayInformationSeverity = displayInformationSeverity;
    }

    public boolean isDisplayNoCategorySeverity() {
        return displayNoCategorySeverity;
    }
    public boolean getDisplayNoCategorySeverity() {
        return displayNoCategorySeverity;
    }
    public void setDisplayNoCategorySeverity(boolean displayNoCategorySeverity) {
        this.displayNoCategorySeverity = displayNoCategorySeverity;
    }

    public boolean isDisplayPortabilitySeverity() {
        return displayPortabilitySeverity;
    }
    public boolean getDisplayPortabilitySeverity() {
        return displayPortabilitySeverity;
    }
    public void setDisplayPortabilitySeverity(boolean displayPortabilitySeverity) {
        this.displayPortabilitySeverity = displayPortabilitySeverity;
    }

}
