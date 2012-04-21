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

import com.thalesgroup.hudson.plugins.cppcheck.config.CppcheckConfig;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

@Deprecated
public class CppcheckPublisher extends Recorder {

    private transient CppcheckConfig cppcheckConfig;

    @Override
    public CppcheckDescriptor getDescriptor() {
        return DESCRIPTOR;
    }


    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    public static final CppcheckDescriptor DESCRIPTOR = new CppcheckDescriptor();

    /**
     * The Cppcheck Descriptor
     */
    public static final class CppcheckDescriptor extends BuildStepDescriptor<Publisher> {

        @SuppressWarnings("deprecation")
        public CppcheckDescriptor() {
            super(CppcheckPublisher.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Publish Cppcheck results";
        }

    }


    @SuppressWarnings("unused")
    private Object readResolve() {
        org.jenkinsci.plugins.cppcheck.config.CppcheckConfig newConfig = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfig(
                cppcheckConfig.getCppcheckReportPattern(),
                cppcheckConfig.isIgnoreBlankFiles(),
                cppcheckConfig.getConfigSeverityEvaluation().getThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getNewThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getFailureThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getNewFailureThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getHealthy(),
                cppcheckConfig.getConfigSeverityEvaluation().getUnHealthy(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityError(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityPossibleError(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityStyle(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle(),
                true,
                cppcheckConfig.getConfigGraph().getXSize(),
                cppcheckConfig.getConfigGraph().getYSize(),
                cppcheckConfig.getConfigGraph().isDiplayAllError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityPossibleError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityStyle(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityPossibleStyle(),
                true);


        org.jenkinsci.plugins.cppcheck.CppcheckPublisher cppcheckPublisher = new org.jenkinsci.plugins.cppcheck.CppcheckPublisher();
        cppcheckPublisher.setCppcheckConfig(newConfig);
        return cppcheckPublisher;
    }


}
