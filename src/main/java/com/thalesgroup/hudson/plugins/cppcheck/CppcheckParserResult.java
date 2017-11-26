/*******************************************************************************
 * Copyright (c) 2009-2011 Thales Corporate Services SAS                        *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package com.thalesgroup.hudson.plugins.cppcheck;

import com.thalesgroup.hudson.plugins.cppcheck.parser.CppcheckParser;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckLogger;
import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import jenkins.security.Roles;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.jenkinsci.remoting.RoleChecker;

import java.io.File;
import java.io.IOException;

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
                throw new IllegalArgumentException(msg);
            }

            CppcheckLogger.log(listener, "Processing " + cppcheckReportFiles.length + " files with the pattern '" + cppcheckReportPattern + "'.");

            for (String cppchecReportkFileName : cppcheckReportFiles) {
                CppcheckReport cppcheckReport = new CppcheckParser().parse(new File(basedir, cppchecReportkFileName));
                mergeReport(cppcheckReportResult, cppcheckReport);
            }
        } catch (Exception e) {
            CppcheckLogger.log(listener, "Parsing throws exceptions. " + e.getMessage());
            return null;
        }

        return cppcheckReportResult;
    }


    private static void mergeReport(CppcheckReport cppcheckReportResult, CppcheckReport cppcheckReport) {
        cppcheckReportResult.getPossibleErrorSeverities().addAll(cppcheckReport.getPossibleErrorSeverities());
        cppcheckReportResult.getPossibleStyleSeverities().addAll(cppcheckReport.getPossibleStyleSeverities());
        cppcheckReportResult.getErrorSeverities().addAll(cppcheckReport.getErrorSeverities());
        cppcheckReportResult.getEverySeverities().addAll(cppcheckReport.getEverySeverities());
        cppcheckReportResult.getNoCategorySeverities().addAll(cppcheckReport.getNoCategorySeverities());
        cppcheckReportResult.getStyleSeverities().addAll(cppcheckReport.getStyleSeverities());
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
    
    @Override
    public void checkRoles(RoleChecker checker) throws SecurityException {
        checker.check(this, Roles.SLAVE);
    }
}
