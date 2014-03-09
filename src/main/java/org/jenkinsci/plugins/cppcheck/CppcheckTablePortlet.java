package org.jenkinsci.plugins.cppcheck;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.view.dashboard.DashboardPortlet;

/**
 * Dashboard portlet that shows a sortable table with jobs and Cppcheck
 * statistics per severity type.
 * 
 * @author Michal Turek
 */
public class CppcheckTablePortlet extends DashboardPortlet {
    /**
     * Constructor.
     * 
     * @param name
     *            the name of the portlet
     */
    @DataBoundConstructor
    public CppcheckTablePortlet(String name) {
        super(name);
    }

    /**
     * Get latest available Cppcheck statistics of a job.
     * 
     * @param job
     *            the job
     * @return the statistics, always non-null value
     */
    public CppcheckStatistics getStatistics(Job<?, ?> job) {
        Run<?, ?> build = job.getLastBuild();

        while(build != null){
            CppcheckBuildAction action = build.getAction(CppcheckBuildAction.class);

            if (action != null) {
                CppcheckResult result = action.getResult();

                if(result != null) {
                    return result.getStatistics();
                }
            }

            build = build.getPreviousBuild();
        }

        return new CppcheckStatistics();
    }

    /**
     * Extension point registration.
     * 
     * @author Michal Turek
     */
    @Extension(optional = true)
    public static class CppcheckTableDescriptor extends Descriptor<DashboardPortlet> {
        @Override
        public String getDisplayName() {
            return Messages.cppcheck_PortletName();
        }
    }
}
