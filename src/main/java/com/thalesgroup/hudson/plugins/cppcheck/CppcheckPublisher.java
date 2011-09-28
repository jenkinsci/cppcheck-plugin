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
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckSourceContainer;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckBuildResultEvaluator;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckLogger;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixProject;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

@Deprecated
public class CppcheckPublisher extends Recorder {

    private CppcheckConfig cppcheckConfig;

    @Override
    public CppcheckDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new CppcheckProjectAction(project);
    }

    protected boolean canContinue(final Result result) {
        return result != Result.ABORTED && result != Result.FAILURE;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        if (this.canContinue(build.getResult())) {
            CppcheckLogger.log(listener, "Starting the cppcheck analysis.");

            CppcheckParserResult parser = new CppcheckParserResult(listener, cppcheckConfig.getCppcheckReportPattern(), cppcheckConfig.isIgnoreBlankFiles());
            CppcheckReport cppcheckReport;
            try {
                cppcheckReport = build.getWorkspace().act(parser);
            } catch (Exception e) {
                CppcheckLogger.log(listener, "Error on cppcheck analysis: " + e);
                build.setResult(Result.FAILURE);
                return false;
            }

            if (cppcheckReport == null) {
                build.setResult(Result.FAILURE);
                return false;
            }

            CppcheckSourceContainer cppcheckSourceContainer = new CppcheckSourceContainer(listener, build.getWorkspace(), cppcheckReport.getEverySeverities());

            CppcheckResult result = new CppcheckResult(cppcheckReport, cppcheckSourceContainer, build);

            Result buildResult = new CppcheckBuildResultEvaluator().evaluateBuildResult(
                    listener, result.getNumberErrorsAccordingConfiguration(cppcheckConfig, false),
                    result.getNumberErrorsAccordingConfiguration(cppcheckConfig, true),
                    cppcheckConfig);

            if (buildResult != Result.SUCCESS) {
                build.setResult(buildResult);
            }

            CppcheckBuildAction buildAction = new CppcheckBuildAction(build, result, cppcheckConfig);
            build.addAction(buildAction);


            if (build.getWorkspace().isRemote()) {
                copyFilesFromSlaveToMaster(build.getRootDir(), launcher.getChannel(), cppcheckSourceContainer.getInternalMap().values());
            }

            CppcheckLogger.log(listener, "End of the cppcheck analysis.");
        }
        return true;
    }


    /**
     * Copies all the source files from stave to master for a remote build.
     *
     * @param rootDir      directory to store the copied files in
     * @param channel      channel to get the files from
     * @param sourcesFiles the sources files to be copied
     * @throws IOException           if the files could not be written
     * @throws FileNotFoundException if the files could not be written
     * @throws InterruptedException  if the user cancels the processing
     */
    private void copyFilesFromSlaveToMaster(final File rootDir,
                                            final VirtualChannel channel, final Collection<CppcheckWorkspaceFile> sourcesFiles)
            throws IOException, InterruptedException {

        File directory = new File(rootDir, CppcheckWorkspaceFile.WORKSPACE_FILES);
        if (!directory.exists()) {

            if (!directory.delete()) {
                //do nothing
            }

            if (!directory.mkdir()) {
                throw new IOException("Can't create directory for remote source files: " + directory.getAbsolutePath());
            }
        }

        for (CppcheckWorkspaceFile file : sourcesFiles) {
            if (!file.isSourceIgnored()) {
                File masterFile = new File(directory, file.getTempName());
                if (!masterFile.exists()) {
                    FileOutputStream outputStream = new FileOutputStream(masterFile);
                    new FilePath(channel, file.getFileName()).copyTo(outputStream);
                }
            }
        }
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
            boolean isIvyProject = false;
            if (Hudson.getInstance().getPlugin("ivy") != null) {
                isIvyProject = hudson.ivy.AbstractIvyProject.class.isAssignableFrom(jobType);
            }

            return FreeStyleProject.class.isAssignableFrom(jobType)
                    || MatrixProject.class.isAssignableFrom(jobType)
                    || isIvyProject;
        }

        @Override
        public String getDisplayName() {
            return "Publish Cppcheck results";
        }

        @Override
        public final String getHelpFile() {
            return getPluginRoot() + "help.html";
        }

        public String getPluginRoot() {
            return "/plugin/cppcheck/";
        }

        @SuppressWarnings("unused")
        public CppcheckConfig getConfig() {
            return new CppcheckConfig();
        }

        @Override
        public Publisher newInstance(StaplerRequest req, JSONObject formData)
                throws hudson.model.Descriptor.FormException {
            @SuppressWarnings("deprecation")
            CppcheckPublisher pub = new CppcheckPublisher();
            CppcheckConfig cppcheckConfig = req.bindJSON(CppcheckConfig.class, formData);
            pub.setCppcheckConfig(cppcheckConfig);
            return pub;
        }
    }


    @SuppressWarnings("unused")
    public CppcheckConfig getCppcheckConfig() {
        return cppcheckConfig;
    }

    public void setCppcheckConfig(CppcheckConfig cppcheckConfig) {
        this.cppcheckConfig = cppcheckConfig;
    }

    @SuppressWarnings("unused")
    private Object readResolve() {
        org.jenkinsci.plugins.cppcheck.config.CppcheckConfig newConfig = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfig(
                cppcheckConfig.getCppcheckReportPattern(),
                true,
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
