package org.jenkinsci.plugins.cppcheck.util;

import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckLogger;
import hudson.model.BuildListener;
import hudson.model.Result;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;

/**
 * @author Gregory Boissinot
 */
public class CppcheckBuildResultEvaluator {


    public Result evaluateBuildResult(
            final BuildListener listener,
            int errorsCount,
            int newErrorsCount,
            CppcheckConfig cppcheckConfig) {

        if (isErrorCountExceeded(errorsCount, cppcheckConfig.getConfigSeverityEvaluation().getFailureThreshold())) {
            CppcheckLogger.log(listener, "Setting build status to FAILURE since total number of errors ("
                    + CppcheckMetricUtil.getMessageSelectedSeverties(cppcheckConfig)
                    + ") exceeds the threshold value '" + cppcheckConfig.getConfigSeverityEvaluation().getFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(newErrorsCount, cppcheckConfig.getConfigSeverityEvaluation().getNewFailureThreshold())) {
            CppcheckLogger.log(listener, "Setting build status to FAILURE since total number of new errors ("
                    + CppcheckMetricUtil.getMessageSelectedSeverties(cppcheckConfig)
                    + ") exceeds the threshold value '" + cppcheckConfig.getConfigSeverityEvaluation().getNewFailureThreshold() + "'.");
            return Result.FAILURE;
        }
        if (isErrorCountExceeded(errorsCount, cppcheckConfig.getConfigSeverityEvaluation().getThreshold())) {
            CppcheckLogger.log(listener, "Setting build status to UNSTABLE since total number of errors ("
                    + CppcheckMetricUtil.getMessageSelectedSeverties(cppcheckConfig)
                    + ") exceeds the threshold value '" + cppcheckConfig.getConfigSeverityEvaluation().getThreshold() + "'.");
            return Result.UNSTABLE;
        }
        if (isErrorCountExceeded(newErrorsCount, cppcheckConfig.getConfigSeverityEvaluation().getNewThreshold())) {
            CppcheckLogger.log(listener, "Setting build status to UNSTABLE since total number of new errors ("
                    + CppcheckMetricUtil.getMessageSelectedSeverties(cppcheckConfig)
                    + ") exceeds the threshold value '" + cppcheckConfig.getConfigSeverityEvaluation().getNewThreshold() + "'.");
            return Result.UNSTABLE;
        }

        CppcheckLogger.log(listener, "Not changing build status, since no threshold has been exceeded");
        return Result.SUCCESS;
    }

    private boolean isErrorCountExceeded(final int errorCount, final String errorThreshold) {
        if (errorCount > 0 && CppcheckMetricUtil.isValid(errorThreshold)) {
            return errorCount > CppcheckMetricUtil.convert(errorThreshold);
        }
        return false;
    }
}
