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
