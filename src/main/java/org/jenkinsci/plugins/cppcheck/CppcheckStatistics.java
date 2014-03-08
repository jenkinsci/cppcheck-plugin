package org.jenkinsci.plugins.cppcheck;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Statistics for a report to be stored in build.xml file. Details are lazy
 * loaded from external file after they are needed.
 * 
 * @author Michal Turek
 * @since 1.15
 */
@ExportedBean
public class CppcheckStatistics implements Serializable {
	/** Serial version UID. */
	private static final long serialVersionUID = 1L;

	/** Count of error issues. */
	private final int errorCount;

	/** Count of warning issues. */
	private final int warningCount;

	/** Count of style issues. */
	private final int styleCount;

	/** Count of performance issues. */
	private final int performanceCount;

	/** Count of information issues. */
	private final int informationCount;

	/** Count of issues with no category. */
	private final int noCategoryCount;

	/** Count of portability issues. */
	private final int portabilityCount;

	/** Cppcheck versions used for generating of the report. */
	private final Set<String> versions;

	/**
	 * Constructor, create an empty object.
	 */
	public CppcheckStatistics() {
		this(0, 0, 0, 0, 0, 0, 0, Collections.<String> emptySet());
	}

	/**
	 * @param errorCount
	 *            count of error issues
	 * @param warningCount
	 *            count of warning issues
	 * @param styleCount
	 *            count of style issues
	 * @param performanceCount
	 *            count of performance issues
	 * @param informationCount
	 *            count of information issues
	 * @param noCategoryCount
	 *            count of issues with no category
	 * @param portabilityCount
	 *            count of portability issues
	 * @param versions
	 *            Cppcheck versions used for generating of the report
	 */
	public CppcheckStatistics(int errorCount, int warningCount, int styleCount,
			int performanceCount, int informationCount, int noCategoryCount,
			int portabilityCount, Set<String> versions) {
		this.errorCount = errorCount;
		this.warningCount = warningCount;
		this.styleCount = styleCount;
		this.performanceCount = performanceCount;
		this.informationCount = informationCount;
		this.noCategoryCount = noCategoryCount;
		this.portabilityCount = portabilityCount;
		this.versions = (versions != null) ? new HashSet<String>(versions)
				: null;
	}

	/**
	 * Get total count of all issues.
	 * 
	 * @return the sum of issues of all severities
	 */
	@Exported
	public int getNumberTotal() {
		return errorCount + warningCount + styleCount + performanceCount
				+ informationCount + noCategoryCount + portabilityCount;
	}

	@Exported
	public int getNumberErrorSeverity() {
		return errorCount;
	}

	@Exported
	public int getNumberWarningSeverity() {
		return warningCount;
	}

	@Exported
	public int getNumberStyleSeverity() {
		return styleCount;
	}

	@Exported
	public int getNumberPerformanceSeverity() {
		return performanceCount;
	}

	@Exported
	public int getNumberInformationSeverity() {
		return informationCount;
	}

	@Exported
	public int getNumberNoCategorySeverity() {
		return noCategoryCount;
	}

	@Exported
	public int getNumberPortabilitySeverity() {
		return portabilityCount;
	}

	public Set<String> getVersions() {
		return (versions != null) ? Collections.unmodifiableSet(versions)
				: null;
	}

	public String formatDiff(int value) {
		if (value == 0) {
			return "";
		}

		return String.format("%+d", value);
	}
}
