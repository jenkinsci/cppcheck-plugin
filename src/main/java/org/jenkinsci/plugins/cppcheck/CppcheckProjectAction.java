package org.jenkinsci.plugins.cppcheck;

import java.io.IOException;
import java.util.Calendar;
import javax.annotation.Nonnull;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.Result;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;

import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph;
import org.jenkinsci.plugins.cppcheck.util.AbstractCppcheckProjectAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.thalesgroup.hudson.plugins.cppcheck.graph.CppcheckGraph;

/**
 * @author Gregory Boissinot
 */
public class CppcheckProjectAction extends AbstractCppcheckProjectAction {

    public String getSearchUrl() {
        return getUrlName();
    }

    public CppcheckProjectAction(@Nonnull final Job<?, ?> job) {
        super(job);
    }

	@Override
    public Run<?, ?> getLastFinishedBuild() {

    	Run<?, ?> lastBuild = getJob().getLastBuild();
        while (lastBuild != null
                && (lastBuild.isBuilding() || lastBuild.getAction(CppcheckBuildAction.class) == null))
        {
            lastBuild = lastBuild.getPreviousBuild();
        }

        return lastBuild;
    }

	@Override
    public Run<?, ?> getLastResultBuild() {
		for (Run<?, ?> b = getJob().getLastBuild(); b != null; b = b.getPreviousBuiltBuild())
		{
			CppcheckBuildAction r = b.getAction(CppcheckBuildAction.class);

			if (r != null)
				return b;
		}
		return null;
    }

    public final boolean isDisplayGraph() {   	
        //Latest
        Run<?, ?> b = getLastFinishedBuild();
        if (b == null) {
            return false;
        }

		// Affect previous
		for (b = b.getPreviousBuild(); b != null; b = b.getPreviousBuild())
		{
			if (b.getResult() == null || b.getResult().isWorseOrEqualTo(Result.FAILURE))
				continue;

			CppcheckBuildAction action = b.getAction(CppcheckBuildAction.class);
			if (action == null || 
                action.getResult() == null || 
                action.getConfig() == null || 
                action.getConfig().getConfigGraph() == null)
				continue;

			return true;
		}

		return false;
    }

    public String getDisplayName() {
        return Messages.cppcheck_CppcheckResults();
    }

    public String getUrlName() {
        return CppcheckBuildAction.URL_NAME;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

    	Run<?, ?> lastBuild = getLastFinishedBuild();
        Calendar timestamp = lastBuild.getTimestamp();

        if (req.checkIfModified(timestamp, rsp)) {
            return;
        }

    	Run<?, ?> lastBuildWithResult = getLastResultBuild();
        CppcheckBuildAction lastAction = lastBuildWithResult.getAction(CppcheckBuildAction.class);
        CppcheckConfigGraph configGraph = (lastAction != null) ? 
                                            ((lastAction.getConfig() != null) ? 
                                                lastAction.getConfig().getConfigGraph() : null) : null;

        if (configGraph == null) {
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

        Graph g = new CppcheckGraph(lastBuild, getDataSetBuilder(configGraph).build(), Messages.cppcheck_NumberOfErrors(),
                                    configGraph.getXSize(), configGraph.getYSize());
        g.doPng(req, rsp);
    }

    private DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> getDataSetBuilder(CppcheckConfigGraph configGraph) {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb
                = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        if (configGraph == null)
            return dsb;

        Run<?,?> lastBuild = getLastFinishedBuild();
        CppcheckBuildAction lastAction = lastBuild.getAction(CppcheckBuildAction.class);

        int numBuilds = 0;

        // numBuildsInGraph <= 1 means unlimited
        for (CppcheckBuildAction a = lastAction;
             (a != null) && (configGraph.getNumBuildsInGraph() <= 1 || numBuilds < configGraph.getNumBuildsInGraph());
             a = a.getPreviousResult(), ++numBuilds) {

            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.getOwner());
            CppcheckStatistics statistics = a.getResult().getStatistics();

            // error
            if (configGraph.isDisplayErrorSeverity())
                dsb.add(statistics.getNumberErrorSeverity(),
                        Messages.cppcheck_Error(), label);

            //warning
            if (configGraph.isDisplayWarningSeverity())
                dsb.add(statistics.getNumberWarningSeverity(),
                        Messages.cppcheck_Warning(), label);

            //style
            if (configGraph.isDisplayStyleSeverity())
                dsb.add(statistics.getNumberStyleSeverity(),
                        Messages.cppcheck_Style(), label);

            //performance
            if (configGraph.isDisplayPerformanceSeverity())
                dsb.add(statistics.getNumberPerformanceSeverity(),
                        Messages.cppcheck_Performance(), label);

            //information
            if (configGraph.isDisplayInformationSeverity())
                dsb.add(statistics.getNumberInformationSeverity(),
                        Messages.cppcheck_Information(), label);

            //no category
            if (configGraph.isDisplayNoCategorySeverity())
                dsb.add(statistics.getNumberNoCategorySeverity(),
                        Messages.cppcheck_NoCategory(), label);

            //portability
            if (configGraph.isDisplayPortabilitySeverity())
                dsb.add(statistics.getNumberPortabilitySeverity(),
                        Messages.cppcheck_Portability(), label);

            // all errors
            if (configGraph.isDisplayAllErrors())
                dsb.add(statistics.getNumberTotal(),
                        Messages.cppcheck_AllErrors(), label);
        }
        return dsb;
    }
}
