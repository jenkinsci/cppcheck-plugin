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

package com.thalesgroup.hudson.plugins.cppcheck.util;

import static com.thalesgroup.hudson.plugins.cppcheck.CppcheckHealthReportThresholds.convert;
import static com.thalesgroup.hudson.plugins.cppcheck.CppcheckHealthReportThresholds.isValid;

import com.thalesgroup.hudson.plugins.cppcheck.CppcheckHealthReportThresholds;

import hudson.model.HealthReport;

public class CppcheckBuildHealthEvaluator {

    public HealthReport evaluatBuildHealth(CppcheckHealthReportThresholds cppcheckHealthReportThresholds, int nbErrorForSeverity) {
        if (cppcheckHealthReportThresholds == null) {
            // no thresholds => no report
            return null;
        }

        if (isHealthyReportEnabled(cppcheckHealthReportThresholds)) {
            int percentage;
            int counter =  nbErrorForSeverity;
            
            if (counter < convert(cppcheckHealthReportThresholds.getHealthy())) {
                percentage = 100;
            }
            else if (counter > convert(cppcheckHealthReportThresholds.getUnHealthy())) {
                percentage = 0;
            }
            else {
                percentage = 100 - ((counter - convert(cppcheckHealthReportThresholds.getHealthy())) * 100
                        / (convert(cppcheckHealthReportThresholds.getUnHealthy()) - convert(cppcheckHealthReportThresholds.getHealthy())));
            }
            return new HealthReport(percentage, "Build stability for " + cppcheckHealthReportThresholds.getThresholdLimit() + " severity.");
        }
        return null;
    }
    
    
     private boolean isHealthyReportEnabled(CppcheckHealthReportThresholds cppcheckHealthReportThresholds) {
        if (isValid(cppcheckHealthReportThresholds.getHealthy()) && isValid(cppcheckHealthReportThresholds.getUnHealthy())) {
            int healthyNumber = convert(cppcheckHealthReportThresholds.getHealthy());
            int unHealthyNumber = convert(cppcheckHealthReportThresholds.getUnHealthy());
            return unHealthyNumber > healthyNumber;
        }
        return false;
    }
}
