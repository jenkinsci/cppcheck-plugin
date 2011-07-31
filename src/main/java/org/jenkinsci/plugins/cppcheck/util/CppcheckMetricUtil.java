package org.jenkinsci.plugins.cppcheck.util;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;

/**
 * @author Gregory Boissinot
 */
public class CppcheckMetricUtil {

    public static int convert(String threshold) {
        if (isValid(threshold)) {
            if (StringUtils.isNotBlank(threshold)) {
                try {
                    return Integer.valueOf(threshold);
                } catch (NumberFormatException exception) {
                    // not valid
                }
            }
        }
        throw new IllegalArgumentException("Not a parsable integer value >= 0: " + threshold);
    }

    public static boolean isValid(final String threshold) {
        if (StringUtils.isNotBlank(threshold)) {
            try {
                return Integer.valueOf(threshold) >= 0;
            } catch (NumberFormatException exception) {
                // not valid
            }
        }
        return false;
    }


    private static boolean isAllSeverities(CppcheckConfig cppcheckConfig) {
        return cppcheckConfig.getConfigSeverityEvaluation().isSeverityError()
                && cppcheckConfig.getConfigSeverityEvaluation().isSeverityWarning()
                && cppcheckConfig.getConfigSeverityEvaluation().isSeverityStyle()
                && cppcheckConfig.getConfigSeverityEvaluation().isSeverityPerformance()
                && cppcheckConfig.getConfigSeverityEvaluation().isSeverityInformation();
    }


    public static String getMessageSelectedSeverties(CppcheckConfig cppcheckConfig) {
        StringBuffer sb = new StringBuffer();

        if (isAllSeverities(cppcheckConfig)) {
            sb.append("with all severities");
            return sb.toString();
        }

        if (cppcheckConfig.getConfigSeverityEvaluation().isSeverityError()) {
            sb.append(" and ");
            sb.append("severity 'error'");
        }

        if (cppcheckConfig.getConfigSeverityEvaluation().isSeverityWarning()) {
            sb.append(" and ");
            sb.append("severity 'warning'");
        }

        if (cppcheckConfig.getConfigSeverityEvaluation().isSeverityStyle()) {
            sb.append(" and ");
            sb.append("severity 'style'");
        }

        if (cppcheckConfig.getConfigSeverityEvaluation().isSeverityPerformance()) {
            sb.append(" and ");
            sb.append("severity 'performance'");
        }


        if (cppcheckConfig.getConfigSeverityEvaluation().isSeverityInformation()) {
            sb.append(" and ");
            sb.append("severity 'information'");
        }

        if (sb.length() != 0)
            sb.delete(0, 5);

        return sb.toString();
    }

}
