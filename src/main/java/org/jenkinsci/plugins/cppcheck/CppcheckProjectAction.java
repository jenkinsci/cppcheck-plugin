package org.jenkinsci.plugins.cppcheck;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import org.jenkinsci.plugins.cppcheck.util.AbstractCppcheckProjectAction;

/**
 * @author Gregory Boissinot
 */
public class CppcheckProjectAction extends AbstractCppcheckProjectAction {

    public String getSearchUrl() {
        return getUrlName();
    }

    public CppcheckProjectAction(final AbstractProject<?, ?> project) {
        super(project);
    }

    public AbstractBuild<?, ?> getLastFinishedBuild() {
        AbstractBuild<?, ?> lastBuild = project.getLastBuild();
        while (lastBuild != null && (lastBuild.isBuilding()
                || lastBuild.getAction(CppcheckBuildAction.class) == null)) {
            lastBuild = lastBuild.getPreviousBuild();
        }
        return lastBuild;
    }

    public final boolean isDisplayGraph() {
        //Latest
        AbstractBuild<?, ?> b = getLastFinishedBuild();
        if (b == null) {
            return false;
        }

        //Affect previous
        b = b.getPreviousBuild();
        if (b != null) {

            for (; b != null; b = b.getPreviousBuild()) {
                if (b.getResult().isWorseOrEqualTo(Result.FAILURE)) {
                    continue;
                }
                CppcheckBuildAction action = b.getAction(CppcheckBuildAction.class);
                if (action == null || action.getResult() == null) {
                    continue;
                }
                CppcheckResult result = action.getResult();
                if (result == null)
                    continue;

                return true;
            }
        }
        return false;
    }

    public Integer getLastResultBuild() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuiltBuild()) {
            CppcheckBuildAction r = b.getAction(CppcheckBuildAction.class);
            if (r != null)
                return b.getNumber();
        }
        return null;
    }


    public String getDisplayName() {
        return Messages.cppcheck_CppcheckResults();
    }

    public String getUrlName() {
        return CppcheckBuildAction.URL_NAME;
    }
}
