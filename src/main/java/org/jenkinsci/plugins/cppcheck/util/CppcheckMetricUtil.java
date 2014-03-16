package org.jenkinsci.plugins.cppcheck.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author Gregory Boissinot
 */
public class CppcheckMetricUtil {
    public static int convert(String threshold) {
        if (isValid(threshold)) {
            if (StringUtils.isNotBlank(threshold)) {
                try {
                    return Integer.parseInt(threshold);
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
                return Integer.parseInt(threshold) >= 0;
            } catch (NumberFormatException exception) {
                // not valid
            }
        }
        return false;
    }
}
