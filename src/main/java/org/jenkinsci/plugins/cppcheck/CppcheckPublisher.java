package org.jenkinsci.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckLogger;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixProject;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.jenkinsci.CppcheckSourceContainer;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;
import org.jenkinsci.plugins.cppcheck.util.CppcheckBuildResultEvaluator;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Gregory Boissinot
 */
public class CppcheckPublisher extends Publisher {

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

            CppcheckParserResult parser = new CppcheckParserResult(listener, cppcheckConfig.getPattern(), cppcheckConfig.isIgnoreBlankFiles());
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

            CppcheckSourceContainer cppcheckSourceContainer = new CppcheckSourceContainer(listener, build.getWorkspace(), build.getModuleRoot(), cppcheckReport.getAllErrors());

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

            CppcheckLogger.log(listener, "Ending the cppcheck analysis.");
        }
        return true;
    }


    /**
     * Copies all the source files from stave to master for a remote build.
     *
     * @param rootDir      directory to store the copied files in
     * @param channel      channel to get the files from
     * @param sourcesFiles the sources files to be copied
     * @throws IOException                   if the files could not be written
     * @throws java.io.FileNotFoundException if the files could not be written
     * @throws InterruptedException          if the user cancels the processing
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

    @Extension
    public static final CppcheckDescriptor DESCRIPTOR = new CppcheckDescriptor();

    /**
     * The Cppcheck Descriptor
     */
    public static final class CppcheckDescriptor extends BuildStepDescriptor<Publisher> {

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


}
