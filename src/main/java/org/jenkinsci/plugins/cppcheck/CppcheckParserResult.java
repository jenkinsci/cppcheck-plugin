package org.jenkinsci.plugins.cppcheck;


import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.jenkinsci.plugins.cppcheck.parser.CppcheckParser;
import org.jenkinsci.plugins.cppcheck.util.CppcheckLogger;

import java.io.File;
import java.io.IOException;

/**
 * @author Gregory Boissinot
 */
public class CppcheckParserResult implements FilePath.FileCallable<CppcheckReport> {

    private static final long serialVersionUID = 1L;

    private final BuildListener listener;

    private final String cppcheckReportPattern;

    private final boolean ignoreBlankFiles;

    public static final String DELAULT_REPORT_MAVEN = "**/cppcheck-result.xml";

    public CppcheckParserResult(final BuildListener listener, String cppcheckReportPattern, boolean ignoreBlankFiles) {

        if (cppcheckReportPattern == null) {
            cppcheckReportPattern = DELAULT_REPORT_MAVEN;
        }

        if (cppcheckReportPattern.trim().length() == 0) {
            cppcheckReportPattern = DELAULT_REPORT_MAVEN;
        }

        this.listener = listener;
        this.cppcheckReportPattern = cppcheckReportPattern;
        this.ignoreBlankFiles = ignoreBlankFiles;
    }

    public CppcheckReport invoke(java.io.File basedir, VirtualChannel channel) throws IOException {

        CppcheckReport cppcheckReportResult = new CppcheckReport();
        try {
            String[] cppcheckReportFiles = findCppcheckReports(basedir);
            if (cppcheckReportFiles.length == 0) {
                String msg = "No cppcheck test report file(s) were found with the pattern '"
                        + cppcheckReportPattern + "' relative to '"
                        + basedir + "'."
                        + "  Did you enter a pattern relative to the correct directory?"
                        + "  Did you generate the XML report(s) for Cppcheck?";
                CppcheckLogger.log(listener, msg);
                throw new IllegalArgumentException(msg);
            }

            CppcheckLogger.log(listener, "Processing " + cppcheckReportFiles.length + " files with the pattern '" + cppcheckReportPattern + "'.");

            for (String cppchecReportkFileName : cppcheckReportFiles) {
                CppcheckReport cppcheckReport = new CppcheckParser().parse(new File(basedir, cppchecReportkFileName), listener);
                mergeReport(cppcheckReportResult, cppcheckReport);
            }
        } catch (Exception e) {
            CppcheckLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
            return null;
        }

        return cppcheckReportResult;
    }


    private static void mergeReport(CppcheckReport cppcheckReportResult, CppcheckReport cppcheckReport) {
        cppcheckReportResult.getErrorSeverityList().addAll(cppcheckReport.getErrorSeverityList());
        cppcheckReportResult.getWarningSeverityList().addAll(cppcheckReport.getWarningSeverityList());
        cppcheckReportResult.getStyleSeverityList().addAll(cppcheckReport.getStyleSeverityList());
        cppcheckReportResult.getPerformanceSeverityList().addAll(cppcheckReport.getPerformanceSeverityList());
        cppcheckReportResult.getInformationSeverityList().addAll(cppcheckReport.getInformationSeverityList());
        cppcheckReportResult.getNoCategorySeverityList().addAll(cppcheckReport.getNoCategorySeverityList());
        cppcheckReportResult.getPortabilitySeverityList().addAll(cppcheckReport.getPortabilitySeverityList());
        cppcheckReportResult.getAllErrors().addAll(cppcheckReport.getAllErrors());
        cppcheckReportResult.getVersions().add(cppcheckReport.getVersion());
    }

    /**
     * Return all cppcheck report files
     *
     * @param parentPath parent
     * @return an array of strings
     */
    private String[] findCppcheckReports(File parentPath) {
        FileSet fs = Util.createFileSet(parentPath, this.cppcheckReportPattern);
        if (this.ignoreBlankFiles) {
            fs.add(new FileSelector() {
                public boolean isSelected(File basedir, String filename, File file) throws BuildException {
                    return file != null && file.length() != 0;
                }
            });
        }
        DirectoryScanner ds = fs.getDirectoryScanner();
        return ds.getIncludedFiles();
    }

    public String getCppcheckReportPattern() {
        return cppcheckReportPattern;
    }
}
