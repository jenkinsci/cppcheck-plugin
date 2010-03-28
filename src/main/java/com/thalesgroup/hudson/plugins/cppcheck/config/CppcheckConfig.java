/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package com.thalesgroup.hudson.plugins.cppcheck.config;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

public class CppcheckConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cppcheckReportPattern;

    private CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation();

    private CppcheckConfigGraph configGraph = new CppcheckConfigGraph();

    public CppcheckConfig() {
    }

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
                threshold, newThreshold, failureThreshold, newFailureThreshold, healthy,
                unHealthy, severityError, severityPossibleError, severityStyle, severityPossibleStyle);

        this.configGraph = new CppcheckConfigGraph(xSize, ySize, diplayAllError,
                displaySeverityError, displaySeverityPossibleError,
                displaySeverityStyle, displaySeverityPossibleStyle);
    }


    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    @SuppressWarnings("deprecated")
    private Object readResolve() {

        if (configSeverityEvaluation == null) {
            configSeverityEvaluation = new CppcheckConfigSeverityEvaluation();
            configSeverityEvaluation.setSeverityError(severityError);
            configSeverityEvaluation.setSeverityPossibleError(severityPossibleError);
            configSeverityEvaluation.setSeverityStyle(severityStyle);
            configSeverityEvaluation.setSeverityPossibleStyle(severityPossibleStyle);

            if (threshold != null) {
                configSeverityEvaluation.setThreshold(threshold);
            }
            if (newThreshold != null) {
                configSeverityEvaluation.setNewThreshold(newThreshold);
            }
            if (failureThreshold != null) {
                configSeverityEvaluation.setFailureThreshold(failureThreshold);
            }
            if (newFailureThreshold != null) {
                configSeverityEvaluation.setNewFailureThreshold(newFailureThreshold);
            }
            if (healthy != null) {
                configSeverityEvaluation.setHealthy(healthy);
            }
            if (unHealthy != null) {
                configSeverityEvaluation.setUnHealthy(unHealthy);
            }
        }

        return this;
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

    // Backward compatibility. Do not remove.
    // CPPCHECK:OFF
    @Deprecated
    private transient String threshold;

    @Deprecated
    private transient String newThreshold;

    @Deprecated
    private transient String failureThreshold;

    @Deprecated
    private transient String newFailureThreshold;

    @Deprecated
    private transient String healthy;

    @Deprecated
    private transient String unHealthy;

    @Deprecated
    private transient boolean severityError = true;

    @Deprecated
    private transient boolean severityPossibleError = true;

    @Deprecated
    private transient boolean severityStyle = true;

    @Deprecated
    private transient boolean severityPossibleStyle = true;

}
