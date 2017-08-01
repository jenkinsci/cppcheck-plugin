package org.jenkinsci.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.CppcheckSource;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckWorkspaceFile;

import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.model.Item;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author Gregory Boissinot
 */
public class CppcheckResult implements Serializable {
    private static final long serialVersionUID = 2L;

    /**
     * The Cppcheck report.
     * 
     * @deprecated Only for backward compatibility with version 1.14 and less.
     */
    @Deprecated
    private transient CppcheckReport report;

    /**
     * The Cppcheck container with all source files.
     * 
     * @deprecated Only for backward compatibility with version 1.14 and less.
     * @see #lazyLoadSourceContainer()
     */
    @Deprecated
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
     * @param report CPPCheck report
     * @param cppcheckSourceContainer The Cppcheck container with all source files.
     * @param owner the build owner
     * 
     * @deprecated Use a different constructor instead.
     */
    public CppcheckResult(CppcheckReport report,
            CppcheckSourceContainer cppcheckSourceContainer, AbstractBuild<?, ?> owner) {
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
    public Object getDynamic(final String link, final StaplerRequest request,
            final StaplerResponse response) throws IOException {
        if (link.equals("source.all")) {
            if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
                response.sendRedirect2("nosourcepermission");
                return null;
            }

            Set<CppcheckDiffState> filter = parseStatesFilter(request.getParameter("states"));
            Collection<CppcheckWorkspaceFile> files = diffCurrentAndPrevious(filter);
            int before = parseIntWithDefault(request.getParameter("before"), 5);
            int after = parseIntWithDefault(request.getParameter("after"), 5);

            return new CppcheckSourceAll(owner, files, before, after);
        } else if (link.startsWith("source.")) {
            if (!owner.getProject().getACL().hasPermission(Item.WORKSPACE)) {
                response.sendRedirect2("nosourcepermission");
                return null;
            }

            Map<Integer, CppcheckWorkspaceFile> agregateMap
                    = getCppcheckSourceContainer().getInternalMap();

            if (agregateMap != null) {
                CppcheckWorkspaceFile vCppcheckWorkspaceFile = agregateMap.get(
                        Integer.parseInt(StringUtils.substringAfter(link, "source.")));

                if (vCppcheckWorkspaceFile == null) {
                    throw new IllegalArgumentException("Error for retrieving the source file with link:" + link);
                }

                return new CppcheckSource(owner, vCppcheckWorkspaceFile);
            }
        }
        return null;
    }

    /**
     * Parse list of states.
     *
     * @param states
     *            comma separated list of states (will be transformed to uppercase)
     * @return the parsed value or null if input is null
     */
    private Set<CppcheckDiffState> parseStatesFilter(String states) {
        if (states == null) {
            return null;
        }

        Set<CppcheckDiffState> result = new HashSet<CppcheckDiffState>();

        for (String state: states.toUpperCase().split(",")) {
            try {
                result.add(CppcheckDiffState.valueOf(state));
            } catch (IllegalArgumentException e) {
                // Ignore, input was broken
            }
        }

        return result;
    }

    /**
     * Parse integer.
     * 
     * @param str
     *            the input string
     * @param defaultValue
     *            the default value returned on error
     * @return the parsed value or default value on error
     * @see Integer#parseInt(String)
     */
    private int parseIntWithDefault(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return defaultValue;
        }
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
            return new CppcheckStatistics();
        }

        CppcheckStatistics previous = previousResult.getStatistics();

        return new CppcheckStatistics(
                current.getNumberErrorSeverity() - previous.getNumberErrorSeverity(),
                current.getNumberWarningSeverity() - previous.getNumberWarningSeverity(),
                current.getNumberStyleSeverity() - previous.getNumberStyleSeverity(),
                current.getNumberPerformanceSeverity() - previous.getNumberPerformanceSeverity(),
                current.getNumberInformationSeverity() - previous.getNumberInformationSeverity(),
                current.getNumberNoCategorySeverity() - previous.getNumberNoCategorySeverity(),
                current.getNumberPortabilitySeverity() - previous.getNumberPortabilitySeverity(),
                current.getVersions());
    }

    /**
     * Gets the number of errors according the selected severities form the configuration user object.
     *
     * @param severityEvaluation the severity evaluation configuration object
     * @param checkNewError   true, if the request is for the number of new errors
     * @return the number of errors or new errors (if checkNewEroor is set to true) for the current configuration object
     * @throws java.io.IOException if an error occurs
     */
    public int getNumberErrorsAccordingConfiguration(
            CppcheckConfigSeverityEvaluation severityEvaluation,
            boolean checkNewError) throws IOException {

        if (severityEvaluation == null) {
            throw new IOException("[ERROR] - The cppcheck configuration file is missing. Could you save again your job configuration.");
        }

        int nbErrors = 0;
        int nbPreviousError = 0;
        CppcheckResult previousResult = this.getPreviousResult();

        CppcheckStatistics st = getStatistics();
        CppcheckStatistics prev = (previousResult != null)
                ? previousResult.getStatistics() : null;

        //Error
        if (severityEvaluation.isSeverityError()) {
            nbErrors += st.getNumberErrorSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberErrorSeverity();
            }
        }

        //Warnings
        if (severityEvaluation.isSeverityWarning()) {
            nbErrors += st.getNumberWarningSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberWarningSeverity();
            }
        }

        //Style
        if (severityEvaluation.isSeverityStyle()) {
            nbErrors += st.getNumberStyleSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberStyleSeverity();
            }
        }

        //Performance
        if (severityEvaluation.isSeverityPerformance()) {
            nbErrors += st.getNumberPerformanceSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberPerformanceSeverity();
            }
        }

        //Information
        if (severityEvaluation.isSeverityInformation()) {
            nbErrors += st.getNumberInformationSeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberInformationSeverity();
            }
        }

        //NoCategory
        if (severityEvaluation.isSeverityNoCategory()) {
            nbErrors += st.getNumberNoCategorySeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberNoCategorySeverity();
            }
        }

        //Portability
        if (severityEvaluation.isSeverityPortability()) {
            nbErrors += st.getNumberPortabilitySeverity();
            if (previousResult != null) {
                nbPreviousError += prev.getNumberPortabilitySeverity();
            }
        }

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
     * Compare current and previous source containers. The code first tries to
     * find all exact matches (file, line, message) and then all approximate
     * matches (file, message). It is possible the line number will change if
     * a developer updates the source code somewhere above the issue. Move of
     * the code to a different file e.g. during refactoring is not considered
     * and one solved and one new issue will be highlighted in such case.
     *
     * @param filter
     *            put only issues of these types to the output, null for all
     * @return the result of the comparison
     */
    public Collection<CppcheckWorkspaceFile> diffCurrentAndPrevious(
            Set<CppcheckDiffState> filter) {
        CppcheckSourceContainer cur = getCppcheckSourceContainer();
        CppcheckResult prevResult = getPreviousResult();
        List<CppcheckWorkspaceFile> curValues
                = new ArrayList<CppcheckWorkspaceFile>(cur.getInternalMap().values());

        if(prevResult == null) {
            for(CppcheckWorkspaceFile file : curValues) {
                file.setDiffState(CppcheckDiffState.UNCHANGED);
            }

            return filterDiffOutput(curValues, filter);
        }

        CppcheckSourceContainer prev = prevResult.getCppcheckSourceContainer();
        Collection<CppcheckWorkspaceFile> prevValues = prev.getInternalMap().values();

        // Exact match first
        for(CppcheckWorkspaceFile curFile : curValues) {
            CppcheckFile curCppFile = curFile.getCppcheckFile();

            for(CppcheckWorkspaceFile prevFile : prevValues) {
                CppcheckFile prevCppFile = prevFile.getCppcheckFile();

                if (curCppFile.getLineNumber() == prevCppFile.getLineNumber()
                        && curCppFile.getFileNameNotNull().equals(prevCppFile.getFileNameNotNull())
                        && curCppFile.getMessage().equals(prevCppFile.getMessage())) {
                    curFile.setDiffState(CppcheckDiffState.UNCHANGED);
                    prevFile.setDiffState(CppcheckDiffState.UNCHANGED);
                    break;
                }
            }
        }

        // Approximate match of the rest (ignore line numbers)
        for(CppcheckWorkspaceFile curFile : curValues) {
            if(curFile.getDiffState() != null) {
                continue;
            }

            CppcheckFile curCppFile = curFile.getCppcheckFile();

            for(CppcheckWorkspaceFile prevFile : prevValues) {
                if(prevFile.getDiffState() != null) {
                    continue;
                }

                CppcheckFile prevCppFile = prevFile.getCppcheckFile();

                if (curCppFile.getFileNameNotNull().equals(prevCppFile.getFileNameNotNull())
                        && curCppFile.getMessage().equals(prevCppFile.getMessage())) {
                    curFile.setDiffState(CppcheckDiffState.UNCHANGED);
                    prevFile.setDiffState(CppcheckDiffState.UNCHANGED);
                    break;
                }
            }
        }

        // Label all new
        for(CppcheckWorkspaceFile curFile : curValues) {
            if(curFile.getDiffState() != null) {
                continue;
            }

            curFile.setDiffState(CppcheckDiffState.NEW);
        }
        
        // Add and label all solved
        for(CppcheckWorkspaceFile prevFile : prevValues) {
            if(prevFile.getDiffState() != null) {
                continue;
            }

            prevFile.setDiffState(CppcheckDiffState.SOLVED);
            prevFile.setSourceIgnored(true);
            curValues.add(prevFile);
        }

        // Sort according to the compare flag
        Collections.sort(curValues, new Comparator<CppcheckWorkspaceFile>() {
            public int compare(CppcheckWorkspaceFile a, CppcheckWorkspaceFile b) {
                return a.getDiffState().ordinal() - b.getDiffState().ordinal();
            }
        });

        return filterDiffOutput(curValues, filter);
    }

    /**
     * Filter result of comparison.
     *
     * @param files
     *            input issues
     * @param filter
     *            put only issues of these types to the output, null for all
     * @return filtered input
     */
    private Collection<CppcheckWorkspaceFile> filterDiffOutput(List<CppcheckWorkspaceFile> files,
                                                               Set<CppcheckDiffState> filter) {
        if (filter == null) {
            return files;
        }

        Collection<CppcheckWorkspaceFile> result = new ArrayList<CppcheckWorkspaceFile>();

        for (CppcheckWorkspaceFile file: files) {
            if (filter.contains(file.getDiffState())) {
                result.add(file);
            }
        }

        return result;
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
            statistics = new CppcheckStatistics();
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
