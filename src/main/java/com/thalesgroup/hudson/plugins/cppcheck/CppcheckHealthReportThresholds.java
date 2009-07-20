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


package com.thalesgroup.hudson.plugins.cppcheck;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;


public class CppcheckHealthReportThresholds implements Serializable{

    private final String threshold;

    private final String newThreshold;

    private final String failureThreshold;

    private final String newFailureThreshold;

    private final String healthy;

    private final String unHealthy;

    private final String thresholdLimit;


    public CppcheckHealthReportThresholds(String threshold, String newThreshold, String failureThreshold, String newFailureThreshold, String healthy, String unHealthy, String thresholdLimit) {
        this.threshold = threshold;
        this.newThreshold = newThreshold;
        this.failureThreshold = failureThreshold;
        this.newFailureThreshold = newFailureThreshold;
        this.healthy = healthy;
        this.unHealthy = unHealthy;
        this.thresholdLimit=thresholdLimit;
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

    public String getThresholdLimit() {
        return thresholdLimit;
    }

    public static int convert(String threshold) {
        if (isValid(threshold)) {
            if (StringUtils.isNotBlank(threshold)) {
                try {
                    return Integer.valueOf(threshold);
                }
                catch (NumberFormatException exception) {
                    // not valid
                }
            }
        }
        throw new IllegalArgumentException("Not a parsable integer value >= 0: " + threshold);
    }

    public static  boolean isValid(final String threshold) {
        if (StringUtils.isNotBlank(threshold)) {
            try {
                return Integer.valueOf(threshold) >= 0;
            }
            catch (NumberFormatException exception) {
                // not valid
            }
        }
        return false;
    }

}
