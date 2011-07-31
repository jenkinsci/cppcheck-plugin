package org.jenkinsci.plugins.cppcheck.util;

import com.thalesgroup.hudson.plugins.cppcheck.util.Messages;
import hudson.model.HealthReport;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;

/**
 * @author Gregory Boissinot
 */
public class CppcheckBuildHealthEvaluator {

    public HealthReport evaluatBuildHealth(CppcheckConfig cppcheckConfig, int nbErrorForSeverity) {

        if (cppcheckConfig == null) {
            // no thresholds => no report
            return null;
        }

        if (isHealthyReportEnabled(cppcheckConfig)) {
            int percentage;

            if (nbErrorForSeverity < CppcheckMetricUtil.convert(cppcheckConfig.getConfigSeverityEvaluation().getHealthy())) {
                percentage = 100;
            } else if (nbErrorForSeverity > CppcheckMetricUtil.convert(cppcheckConfig.getConfigSeverityEvaluation().getUnHealthy())) {
                percentage = 0;
            } else {
                percentage = 100 - ((nbErrorForSeverity - CppcheckMetricUtil.convert(cppcheckConfig.getConfigSeverityEvaluation().getHealthy())) * 100
                        / (CppcheckMetricUtil.convert(cppcheckConfig.getConfigSeverityEvaluation().getUnHealthy()) - CppcheckMetricUtil.convert(cppcheckConfig.getConfigSeverityEvaluation().getHealthy())));
            }

            return new HealthReport(percentage, Messages._CppcheckBuildHealthEvaluator_Description(CppcheckMetricUtil.getMessageSelectedSeverties(cppcheckConfig)));
        }
        return null;
    }


    private boolean isHealthyReportEnabled(CppcheckConfig cppcheckconfig) {
        if (CppcheckMetricUtil.isValid(cppcheckconfig.getConfigSeverityEvaluation().getHealthy()) && CppcheckMetricUtil.isValid(cppcheckconfig.getConfigSeverityEvaluation().getUnHealthy())) {
            int healthyNumber = CppcheckMetricUtil.convert(cppcheckconfig.getConfigSeverityEvaluation().getHealthy());
            int unHealthyNumber = CppcheckMetricUtil.convert(cppcheckconfig.getConfigSeverityEvaluation().getUnHealthy());
            return unHealthyNumber > healthyNumber;
        }
        return false;
    }
}
