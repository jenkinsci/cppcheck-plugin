package org.jenkinsci.plugins.cppcheck;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thalesgroup.hudson.plugins.cppcheck.util.AbstractCppcheckBuildAction;

import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep;
import hudson.model.Action;
import hudson.model.HealthReport;

import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;
import org.jenkinsci.plugins.cppcheck.util.CppcheckBuildHealthEvaluator;

/**
 * @author Gregory Boissinot
 */
public class CppcheckBuildAction extends AbstractCppcheckBuildAction implements SimpleBuildStep.LastBuildAction {

    public static final String URL_NAME = "cppcheckResult";

    private CppcheckResult result;

    private List<CppcheckProjectAction> projectActions;
    /** 
     * The health report percentage.
     * 
     * @since 1.15
     */
    private int healthReportPercentage;

    public CppcheckBuildAction(Run<?, ?> owner, CppcheckResult result, CppcheckConfig config,
            int healthReportPercentage) {
        super(owner);
        this.result = result;
        this.healthReportPercentage = healthReportPercentage;
        
        List<CppcheckProjectAction> projectActions = new ArrayList<CppcheckProjectAction>();
        projectActions.add(new CppcheckProjectAction(owner, config.getConfigGraph()));
        this.projectActions = projectActions;
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-24.png";
    }

    public String getDisplayName() {
        return Messages.cppcheck_CppcheckResults();
    }

    public String getUrlName() {
        return URL_NAME;
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public CppcheckResult getResult() {
        return this.result;
    }

    Run<?, ?> getBuild() {
        return this.owner;
    }

    public Object getTarget() {
        return this.result;
    }

    public HealthReport getBuildHealth() {
        if(healthReportPercentage >= 0 && healthReportPercentage <= 100) {
            return new HealthReport(healthReportPercentage,
                    Messages._cppcheck_BuildStability());
        } else {
            return null;
        }
    }

    public static int computeHealthReportPercentage(CppcheckResult result,
            CppcheckConfigSeverityEvaluation severityEvaluation) {
        try {
            return new CppcheckBuildHealthEvaluator().evaluatBuildHealth(severityEvaluation,
                    result.getNumberErrorsAccordingConfiguration(severityEvaluation,
                            false));
        } catch (IOException e) {
            return -1;
        }
    }

    // Backward compatibility
    @Deprecated
    private transient Run<?, ?> build;

    /** Backward compatibility with version 1.14 and less. */
    @Deprecated
    private transient CppcheckConfig cppcheckConfig;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    private Object readResolve() {
        if (build != null) {
            this.owner = build;
        }

        // Backward compatibility with version 1.14 and less
        if (cppcheckConfig != null) {
            healthReportPercentage = 100;
        }

        return this;
    }
    
    @Override
    public Collection<? extends Action> getProjectActions() {
    	return this.projectActions;
    }
}
