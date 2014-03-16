package org.jenkinsci.plugins.cppcheck;


import java.io.IOException;

import com.thalesgroup.hudson.plugins.cppcheck.util.AbstractCppcheckBuildAction;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;

import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;
import org.jenkinsci.plugins.cppcheck.util.CppcheckBuildHealthEvaluator;

/**
 * @author Gregory Boissinot
 */
public class CppcheckBuildAction extends AbstractCppcheckBuildAction {

    public static final String URL_NAME = "cppcheckResult";

    private CppcheckResult result;

    /** 
     * The health report percentage.
     * 
     * @since 1.15
     */
    private int healthReportPercentage;

    public CppcheckBuildAction(AbstractBuild<?, ?> owner, CppcheckResult result,
            int healthReportPercentage) {
        super(owner);
        this.result = result;
        this.healthReportPercentage = healthReportPercentage;
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

    AbstractBuild<?, ?> getBuild() {
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
    private transient AbstractBuild<?, ?> build;

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
}
