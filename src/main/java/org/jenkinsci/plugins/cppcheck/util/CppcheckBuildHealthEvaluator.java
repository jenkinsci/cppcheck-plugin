package org.jenkinsci.plugins.cppcheck.util;

import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;

/**
 * @author Gregory Boissinot
 */
public class CppcheckBuildHealthEvaluator {
    public int evaluatBuildHealth(CppcheckConfigSeverityEvaluation severityEvaluation,
            int nbErrorForSeverity) {
        if (severityEvaluation == null) {
            // no thresholds => no report
            return -1;
        }

        if (isHealthyReportEnabled(severityEvaluation)) {
            int percentage;
            
            int healthyNumber = CppcheckMetricUtil.convert(severityEvaluation.getHealthy());
            int unHealthyNumber = CppcheckMetricUtil.convert(severityEvaluation.getUnHealthy());

            if (nbErrorForSeverity < healthyNumber) {
                percentage = 100;
            } else if (nbErrorForSeverity > unHealthyNumber) {
                percentage = 0;
            } else {
                percentage = 100 - ((nbErrorForSeverity - healthyNumber) * 100
                        / (unHealthyNumber - healthyNumber));
            }

            return percentage;
        }
        return -1;
    }


    private boolean isHealthyReportEnabled(CppcheckConfigSeverityEvaluation severityEvaluation) {
        if (CppcheckMetricUtil.isValid(severityEvaluation.getHealthy())
                && CppcheckMetricUtil.isValid(severityEvaluation.getUnHealthy())) {
            int healthyNumber = CppcheckMetricUtil.convert(severityEvaluation.getHealthy());
            int unHealthyNumber = CppcheckMetricUtil.convert(severityEvaluation.getUnHealthy());
            return unHealthyNumber > healthyNumber;
        }
        return false;
    }
}
