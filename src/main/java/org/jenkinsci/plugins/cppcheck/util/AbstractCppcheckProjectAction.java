package org.jenkinsci.plugins.cppcheck.util;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import org.jenkinsci.plugins.cppcheck.CppcheckBuildAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractCppcheckProjectAction extends Actionable implements ProminentProjectAction {

    protected final AbstractProject<?, ?> project;

    public AbstractCppcheckProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    @SuppressWarnings("unused")
    public AbstractProject<?, ?> getProject() {
        return project;
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-48.png";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    protected abstract AbstractBuild<?, ?> getLastFinishedBuild();

    protected abstract Integer getLastResultBuild();

    @SuppressWarnings("unused")
    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        AbstractBuild<?, ?> lastBuild = getLastFinishedBuild();
        CppcheckBuildAction cppcheckBuildAction = lastBuild.getAction(CppcheckBuildAction.class);
        if (cppcheckBuildAction != null) {
            cppcheckBuildAction.doGraph(req, rsp);
        }
    }

    @SuppressWarnings("unused")
    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Integer buildNumber = getLastResultBuild();
        if (buildNumber == null) {
            rsp.sendRedirect2("nodata");
        } else {
            rsp.sendRedirect2("../" + buildNumber + "/" + getUrlName());
        }
    }

}
