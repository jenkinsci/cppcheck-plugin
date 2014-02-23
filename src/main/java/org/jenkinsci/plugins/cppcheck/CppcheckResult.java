package org.jenkinsci.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.CppcheckSource;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;

import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.model.Item;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfig;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gregory Boissinot
 */
public class CppcheckResult implements Serializable {
    private static final long serialVersionUID = 2L;

    /**
     * The Cppcheck report. Backward compatibility with version 1.14 and less.
     */
    private transient CppcheckReport report;

    /**
     * The Cppcheck container with all source files. Backward compatibility
     * with version 1.14 and less.
     */
    private transient CppcheckSourceContainer cppcheckSourceContainer;

    /**
     * The build owner.
     */
    private AbstractBuild<?, ?> owner;

    /**
     * The Cppcheck report statistics.
     * 
     * @since 1.15
     */
    private CppcheckStatistics statistics;

    /**
     * Constructor.
     * 
     * @param statistics
     *            the Cppcheck report statistics
     * @param owner
     *            the build owner
     * 
     * @since 1.15
     */
    public CppcheckResult(CppcheckStatistics statistics, AbstractBuild<?, ?> owner) {
        this.statistics = statistics;
        this.owner = owner;
    }
    
    /**
     * Constructor. Only for backward compatibility with previous versions.
     * 
     * @param report
     * @param cppcheckSourceContainer
     * @param owner
     * 
     * @deprecated Use a different constructor instead.
     */
    public CppcheckResult(CppcheckReport report, CppcheckSourceContainer cppcheckSourceContainer, AbstractBuild<?, ?> owner) {
        this.report = report;
        this.cppcheckSourceContainer = cppcheckSourceContainer;
        this.owner = owner;
        this.statistics = report.getStatistics();
    }

    /**
     * Gets the remote API for the build result.
     *
     * @return the remote API
     */
    public Api getApi() {
        return new Api(getStatistics());
    }

    @Exported
    public CppcheckStatistics getReport() {
        return getStatistics();
    }

    /**
     * Get the statistics.
     * 
     * @return the statistics, always non-null value should be returned
     */
    @Exported
    public CppcheckStatistics getStatistics() {
        return statistics;
    }

    public AbstractBuild<?, ?> getOwner() {
        return owner;
    }

    public CppcheckSourceContainer getCppcheckSourceContainer() {
        return lazyLoadSourceContainer();
    }

    /**
     * Gets the dynamic result of the selection element.
     *
     * @param link     the link to identify the sub page to show
     * @param request  Stapler request
     * @param response Stapler response
     * @return the dynamic result of the analysis (detail page).
     * @throws java.io.IOException if an error occurs
     */
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) throws IOException {

        if (link.startsWith("source.")) {

            if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
                response.sendRedirect2("nosourcepermission");
                return null;
            }

            Map<Integer, CppcheckWorkspaceFile> agregateMap = getCppcheckSourceContainer().getInternalMap();
            if (agregateMap != null) {
                CppcheckWorkspaceFile vCppcheckWorkspaceFile = agregateMap.get(Integer.parseInt(StringUtils.substringAfter(link, "source.")));
                if (vCppcheckWorkspaceFile == null) {
                    throw new IllegalArgumentException("Error for retrieving the source file with link:" + link);
                }
                return new CppcheckSource(owner, vCppcheckWorkspaceFile);
            }
        }
        return null;
    }

    /**
     * Gets the previous Cppcheck result for the build result.
     *
     * @return the previous Cppcheck result or null
     */
    public CppcheckResult getPreviousResult() {
        CppcheckBuildAction previousAction = getPreviousAction();
        CppcheckResult previousResult = null;
        if (previousAction != null) {
            previousResult = previousAction.getResult();
        }

        return previousResult;
    }

    /**
     * Gets the previous Action for the build result.
     *
     * @return the previous Cppcheck Build Action
     */
    private CppcheckBuildAction getPreviousAction() {
        AbstractBuild<?, ?> previousBuild = owner.getPreviousBuild();
        if (previousBuild != null) {
            return previousBuild.getAction(CppcheckBuildAction.class);
        }
        return null;
    }

    /**
     * Get differences between current and previous statistics.
     * 
     * @return the differences
     */
    public CppcheckStatistics getDiff(){
        CppcheckStatistics current = getStatistics();
        CppcheckResult previousResult = getPreviousResult();

        if(previousResult == null) {
            return new CppcheckStatistics(0, 0, 0, 0, 0, 0, current.getVersions());
        }

        CppcheckStatistics previous = previousResult.getStatistics();

        return new CppcheckStatistics(
                current.getNumberErrorSeverity() - previous.getNumberErrorSeverity(),
                current.getNumberWarningSeverity() - previous.getNumberWarningSeverity(),
                current.getNumberStyleSeverity() - previous.getNumberStyleSeverity(),
                current.getNumberPerformanceSeverity() - previous.getNumberPerformanceSeverity(),
                current.getNumberInformationSeverity() - previous.getNumberInformationSeverity(),
                current.getNumberNoCategorySeverity() - previous.getNumberNoCategorySeverity(),
                current.getVersions());
    }

    /**
     * Gets the number of errors according the selected severities form the configuration user object.
     *
     * @param cppecheckConfig the Cppcheck configuration object
     * @param checkNewError   true, if the request is for the number of new errors
     * @return the number of errors or new errors (if checkNewEroor is set to true) for the current configuration object
     * @throws java.io.IOException if an error occurs
     */
    public int getNumberErrorsAccordingConfiguration(CppcheckConfig cppecheckConfig, boolean checkNewError) throws IOException {

        if (cppecheckConfig == null) {
            throw new IOException("[ERROR] - The cppcheck configuration file is missing. Could you save again your job configuration.");
        }

        int nbErrors = 0;
        int nbPreviousError = 0;
        CppcheckResult previousResult = this.getPreviousResult();

        CppcheckStatistics st = getStatistics();
        CppcheckStatistics prev = (previousResult != null)
                ? previousResult.getStatistics() : null;

        //Error
        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityError()) {
            nbErrors += st.getNumberErrorSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberErrorSeverity();
            }
        }

        //Warnings
        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityWarning()) {
            nbErrors += st.getNumberWarningSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberWarningSeverity();
            }
        }

        //Style
        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityStyle()) {
            nbErrors += st.getNumberStyleSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberStyleSeverity();
            }
        }

        //Performance
        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityPerformance()) {
            nbErrors += st.getNumberPerformanceSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberPerformanceSeverity();
            }
        }

        //Information
        if (cppecheckConfig.getConfigSeverityEvaluation().isSeverityInformation()) {
            nbErrors += st.getNumberInformationSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberInformationSeverity();
            }
        }

        // TODO: st.getNumberNoCategorySeverity()

        if (checkNewError) {
            if (previousResult != null) {
                return nbErrors - nbPreviousError;
            } else {
                return 0;
            }
        } else {
            return nbErrors;
        }
    }

    /**
     * Convert legacy data in format of cppcheck plugin version 1.14
     * to the new one that uses statistics.
     * 
     * @return this with optionally updated data
     */
    private Object readResolve() {
        if (report != null && statistics == null) {
            statistics = report.getStatistics();
        }

        // Just for sure
        if (statistics == null) {
            statistics = new CppcheckStatistics(0, 0, 0, 0, 0, 0,
                    Collections.<String>emptySet());
        }

        return this;
    }

    /**
     * Lazy load source container data if they are not already loaded.
     * 
     * @return the loaded and parsed data or empty object on error
     */
    private CppcheckSourceContainer lazyLoadSourceContainer() {
        // Backward compatibility with version 1.14 and less
        if(cppcheckSourceContainer != null) {
            return cppcheckSourceContainer;
        }

        XmlFile xmlSourceContainer = new XmlFile(new File(owner.getRootDir(),
                CppcheckPublisher.XML_FILE_DETAILS));
        try {
            return (CppcheckSourceContainer) xmlSourceContainer.read();
        } catch (IOException e) {
            return new CppcheckSourceContainer(new HashMap<Integer,
                    CppcheckWorkspaceFile>());
        }
    }
}
