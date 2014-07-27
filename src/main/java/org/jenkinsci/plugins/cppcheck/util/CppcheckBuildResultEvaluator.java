package org.jenkinsci.plugins.cppcheck.util;


import hudson.model.BuildListener;
import hudson.model.Result;

import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;

/**
 * @author Gregory Boissinot
 */
public class CppcheckBuildResultEvaluator {
    public Result evaluateBuildResult(
            final BuildListener listener,
            int errorsCount,
            int newErrorsCount,
            CppcheckConfigSeverityEvaluation severityEvaluation) {

        if (isErrorCountExceeded(errorsCount, severityEvaluation.getFailureThreshold())) {
            CppcheckLogger.log(listener,
                    "Setting build status to FAILURE since total number of issues '"
                            + errorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(newErrorsCount, severityEvaluation.getNewFailureThreshold())) {
            CppcheckLogger.log(listener,
                    "Setting build status to FAILURE since number of new issues '"
                            + newErrorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getNewFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(errorsCount, severityEvaluation.getThreshold())) {
            CppcheckLogger.log(listener,
                    "Setting build status to UNSTABLE since total number of issues '"
                            + errorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getThreshold() + "'.");
            return Result.UNSTABLE;
        }
        if (isErrorCountExceeded(newErrorsCount, severityEvaluation.getNewThreshold())) {
            CppcheckLogger.log(listener,
                    "Setting build status to UNSTABLE since number of new issues '"
                            + newErrorsCount + "' exceeds the threshold value '"
                            + severityEvaluation.getNewThreshold() + "'.");
            return Result.UNSTABLE;
        }

        CppcheckLogger.log(listener,
                "Not changing build status, since no threshold has been exceeded.");
        return Result.SUCCESS;
    }

    private boolean isErrorCountExceeded(final int errorCount, final String errorThreshold) {
        if (errorCount > 0 && CppcheckMetricUtil.isValid(errorThreshold)) {
            return errorCount >= CppcheckMetricUtil.convert(errorThreshold);
        }
        return false;
    }
}
