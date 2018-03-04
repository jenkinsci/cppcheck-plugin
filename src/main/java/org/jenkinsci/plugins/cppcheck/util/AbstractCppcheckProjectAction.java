package org.jenkinsci.plugins.cppcheck.util;

import hudson.model.Actionable;
import hudson.model.Run;
import hudson.model.Action;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public abstract class AbstractCppcheckProjectAction extends Actionable implements Action {

    protected final Run<?, ?> run;

    public AbstractCppcheckProjectAction(Run<?, ?> run) {
        this.run = run;
    }

    public Run<?, ?> getRun() {
        return run;
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
