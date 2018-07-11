/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
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

package org.jenkinsci.plugins.cppcheck;


import org.jenkinsci.plugins.cppcheck.model.CppcheckFile;
import org.jenkinsci.plugins.cppcheck.parser.CppcheckParser;
import hudson.model.BuildListener;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CppcheckParserV1Test {

    hudson.model.TaskListener listener = mock(hudson.model.TaskListener.class);
    CppcheckParser cppcheckParser;

    @Before
    public void setUp() throws Exception {
        //initialize the logger
        listener = mock(BuildListener.class);
        when(listener.getLogger()).thenReturn(new PrintStream(new ByteArrayOutputStream()));

        cppcheckParser = new CppcheckParser();
    }


    @Test
    public void nullFile() throws Exception {
        try {
            cppcheckParser.parse(null, listener);
            Assert.fail("null parameter is not allowed.");
        } catch (IllegalArgumentException iea) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void nonExistFile() throws Exception {
        try {
            cppcheckParser.parse(new File("nonExistFile"), listener);
            Assert.fail("A valid file is mandatory.");
        } catch (IllegalArgumentException iea) {
            Assert.assertTrue(true);
        }
    }


    @Test
    public void testcppcheck1() throws Exception {
        processCheckstyle("version1/testcppcheck1.xml", 12, 8, 2, 2, 0, 0, 0);
    }

    @Test
    public void testcppcheck2() throws Exception {
        processCheckstyle("version1/testcppcheck2.xml", 18, 14, 4, 0, 0, 0, 0);
    }

    @Test
    public void testcppcheckPart1() throws Exception {
        processCheckstyle("version1/testcppcheck-part1.xml", 3, 2, 0, 1, 0, 0, 0);
    }

    @Test
    public void testcppcheckPart2() throws Exception {
        processCheckstyle("version1/testcppcheck-part2.xml", 4, 1, 2, 1, 0, 0, 0);

    }

    private void processCheckstyle(String filename,
                                 int nbAllErrors,
                                 int nbError,
                                 int nbWarning,
                                 int nbStyle,
                                 int nbPerformance,
                                 int nbInformation,
                                 int nbNoCategory) throws Exception {

        CppcheckReport cppcheckReport = cppcheckParser.parse(new File(CppcheckParserV1Test.class.getResource(filename).toURI()), listener);

        List<CppcheckFile> everyErrors = cppcheckReport.getAllErrors();
        List<CppcheckFile> errorSeverities = cppcheckReport.getErrorSeverityList();
        List<CppcheckFile> warningSeverities = cppcheckReport.getWarningSeverityList();
        List<CppcheckFile> styleSeverities = cppcheckReport.getStyleSeverityList();
        List<CppcheckFile> performanceSeverities = cppcheckReport.getPerformanceSeverityList();
        List<CppcheckFile> informationSeverities = cppcheckReport.getInformationSeverityList();
        List<CppcheckFile> noCategorySeverities = cppcheckReport.getNoCategorySeverityList();
        List<CppcheckFile> portabilitySeverities = cppcheckReport.getPortabilitySeverityList();

        assert everyErrors != null;
        assert errorSeverities != null;
        assert warningSeverities != null;
        assert styleSeverities != null;
        assert performanceSeverities != null;
        assert noCategorySeverities != null;
        assert portabilitySeverities != null;

        Assert.assertEquals("Wrong computing of list of errors", everyErrors.size(),
                errorSeverities.size() + warningSeverities.size() + styleSeverities.size() + performanceSeverities.size() + informationSeverities.size() + noCategorySeverities.size() + portabilitySeverities.size());

        Assert.assertEquals("Wrong total number of errors", nbAllErrors, everyErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'error'", nbError, errorSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'warning'", nbWarning, warningSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'style'", nbStyle, styleSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'performance'", nbPerformance, performanceSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'information'", nbInformation, informationSeverities.size());
        Assert.assertEquals("Wrong total number of errors with no category", nbNoCategory, noCategorySeverities.size());
    }

}
