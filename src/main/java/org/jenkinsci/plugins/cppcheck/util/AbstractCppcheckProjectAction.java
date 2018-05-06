package org.jenkinsci.plugins.cppcheck.util;

import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.Action;
import hudson.model.Run;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractCppcheckProjectAction extends Actionable implements Action {

    protected final Job<?, ?> job;

    public AbstractCppcheckProjectAction(Job<?, ?> job) {
        this.job = job;
    }

    public Run<?, ?> getRun() {
        return getLastFinishedBuild();
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-24.png";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    protected abstract Run<?, ?> getLastFinishedBuild();

    protected abstract Integer getLastResultBuild();

    public abstract void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException;

    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Integer buildNumber = getLastResultBuild();
        if (buildNumber == null) {
            rsp.sendRedirect2("nodata");
        } else {
            rsp.sendRedirect2("../" + buildNumber + "/" + getUrlName());
        }
    }
}
