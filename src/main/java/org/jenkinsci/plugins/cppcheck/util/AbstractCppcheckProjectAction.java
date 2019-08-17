package org.jenkinsci.plugins.cppcheck.util;

import hudson.model.Actionable;
import hudson.model.ProminentProjectAction;
import hudson.model.Job;
import hudson.model.Run;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractCppcheckProjectAction extends Actionable implements ProminentProjectAction {

    protected final transient Job<?, ?> job;

    public AbstractCppcheckProjectAction(@Nonnull Job<?, ?> job) {
        this.job = job;
    }

    @Nonnull public Job<?, ?> getJob() {
        return job;
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-24.png";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    protected abstract Run<?, ?> getLastFinishedBuild();

    protected abstract Run<?, ?> getLastResultBuild();

    public abstract void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException;

    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Integer buildNumber = (getLastResultBuild() != null) ? getLastResultBuild().getNumber() : null;
        if (buildNumber == null) {
            rsp.sendRedirect2("nodata");
        } else {
            rsp.sendRedirect2("../" + buildNumber + "/" + getUrlName());
        }
    }
}
