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

import java.io.Serializable;

public class CppcheckConfigSeverityEvaluation implements Serializable {

    private static final long serialVersionUID = 1L;

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

    public CppcheckConfigSeverityEvaluation() {
    }

    public CppcheckConfigSeverityEvaluation(String threshold,
                                            String newThreshold, String failureThreshold,
                                            String newFailureThreshold, String healthy, String unHealthy,
                                            boolean severityError, boolean severityPossibleError,
                                            boolean severityStyle, boolean severityPossibleStyle) {

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

    public boolean isSeverityPossibleError() {
        return severityPossibleError;
    }

    public boolean isSeverityStyle() {
        return severityStyle;
    }

    public boolean isSeverityPossibleStyle() {
        return severityPossibleStyle;
    }

    public boolean isAllSeverities() {
        return isSeverityError() && isSeverityPossibleError() && isSeverityPossibleStyle() && isSeverityStyle();
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public void setNewThreshold(String newThreshold) {
        this.newThreshold = newThreshold;
    }

    public void setFailureThreshold(String failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public void setNewFailureThreshold(String newFailureThreshold) {
        this.newFailureThreshold = newFailureThreshold;
    }

    public void setHealthy(String healthy) {
        this.healthy = healthy;
    }

    public void setUnHealthy(String unHealthy) {
        this.unHealthy = unHealthy;
    }

    public void setSeverityError(boolean severityError) {
        this.severityError = severityError;
    }

    public void setSeverityPossibleError(boolean severityPossibleError) {
        this.severityPossibleError = severityPossibleError;
    }

    public void setSeverityStyle(boolean severityStyle) {
        this.severityStyle = severityStyle;
    }

    public void setSeverityPossibleStyle(boolean severityPossibleStyle) {
        this.severityPossibleStyle = severityPossibleStyle;
    }
}
