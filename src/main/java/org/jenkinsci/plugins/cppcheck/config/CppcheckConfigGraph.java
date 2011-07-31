package org.jenkinsci.plugins.cppcheck.config;

import java.io.Serializable;

/**
 * @author Gregory Boissinot
 */
public class CppcheckConfigGraph implements Serializable {

    public static final int DEFAULT_CHART_WIDTH = 500;
    public static final int DEFAULT_CHART_HEIGHT = 200;

    private int xSize = DEFAULT_CHART_WIDTH;
    private int ySize = DEFAULT_CHART_HEIGHT;
    private boolean displayAllErrors;
    private boolean displayErrorSeverity;
    private boolean displayWarningSeverity;
    private boolean displayStyleSeverity;
    private boolean displayPerformanceSeverity;
    private boolean displayInformationSeverity;

    public CppcheckConfigGraph() {
    }

    public CppcheckConfigGraph(int xSize, int ySize, boolean displayAllErrors, boolean displayErrorSeverity, boolean displayWarningSeverity, boolean displayStyleSeverity, boolean displayPerformanceSeverity, boolean displayInformationSeverity) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.displayAllErrors = displayAllErrors;
        this.displayErrorSeverity = displayErrorSeverity;
        this.displayWarningSeverity = displayWarningSeverity;
        this.displayStyleSeverity = displayStyleSeverity;
        this.displayPerformanceSeverity = displayPerformanceSeverity;
        this.displayInformationSeverity = displayInformationSeverity;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public boolean isDisplayAllErrors() {
        return displayAllErrors;
    }

    public boolean isDisplayErrorSeverity() {
        return displayErrorSeverity;
    }

    public boolean isDisplayWarningSeverity() {
        return displayWarningSeverity;
    }

    public boolean isDisplayStyleSeverity() {
        return displayStyleSeverity;
    }

    public boolean isDisplayPerformanceSeverity() {
        return displayPerformanceSeverity;
    }

    public boolean isDisplayInformationSeverity() {
        return displayInformationSeverity;
    }
}
