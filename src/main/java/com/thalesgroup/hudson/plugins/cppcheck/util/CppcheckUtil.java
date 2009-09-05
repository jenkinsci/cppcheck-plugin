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

import com.thalesgroup.hudson.plugins.cppcheck.CppcheckResult;
import com.thalesgroup.hudson.plugins.cppcheck.config.CppcheckConfig;

public class CppcheckUtil {

    /**
     * Gets the number of errors
     *
     * @param cppecheckConfig
     * @param result
     * @param checkNewError
     * @return
     */
    public static int getNumberErrors(CppcheckConfig cppecheckConfig, CppcheckResult result, boolean checkNewError) {

        int nbErrors = 0;
        int nbPreviousError = 0;
        CppcheckResult previousResult = result.getPreviousResult();

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityPossibleError()) {
            nbErrors = result.getReport().getPossibleErrorSeverities().size();
            if (previousResult != null) {
                nbPreviousError = previousResult.getReport().getPossibleErrorSeverities().size();
            }
        }

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityStyle()) {
            nbErrors = nbErrors + result.getReport().getStyleSeverities().size();
            if (previousResult != null) {
                nbPreviousError = nbPreviousError + previousResult.getReport().getStyleSeverities().size();
            }

        }

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle()) {
            nbErrors = nbErrors + result.getReport().getPossibleStyleSeverities().size();
            if (previousResult != null) {
                nbPreviousError = nbPreviousError + previousResult.getReport().getPossibleStyleSeverities().size();
            }
        }

        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityError()) {
            nbErrors = nbErrors + result.getReport().getErrorSeverities().size();
            if (previousResult != null) {
                nbPreviousError = nbPreviousError + previousResult.getReport().getErrorSeverities().size();
            }
        }

        if (checkNewError) {
            if (previousResult != null) {
                return nbErrors - nbPreviousError;
            } else {
                return 0;
            }
        } else
            return nbErrors;
    }
}
