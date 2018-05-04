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
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class CppcheckParserV1Test {


    CppcheckParser cppcheckParser;

    @Before
    public void setUp() throws Exception {
        cppcheckParser = new CppcheckParser();
    }


    @Test
    public void nullFile() throws Exception {
        try {
            cppcheckParser.parse(null);
            Assert.fail("null parameter is not allowed.");
        } catch (IllegalArgumentException iea) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void nonExistFile() throws Exception {
        try {
            cppcheckParser.parse(new File("nonExistFile"));
            Assert.fail("A valid file is mandatory.");
        } catch (IllegalArgumentException iea) {
            Assert.assertTrue(true);
        }
    }


    @Test
    public void testcppcheck1() throws Exception {
        processCheckstyle("version1/testcppcheck1.xml", 12, 2, 0, 2, 8, 0);
    }

    @Test
    public void testcppcheck2() throws Exception {
        processCheckstyle("version1/testcppcheck2.xml", 18, 4, 0, 0, 14, 0);
    }

    @Test
    public void testcppcheckPart1() throws Exception {
        processCheckstyle("version1/testcppcheck-part1.xml", 3, 0, 0, 1, 2, 0);
    }

    @Test
    public void testcppcheckPart2() throws Exception {
        processCheckstyle("version1/testcppcheck-part2.xml", 4, 2, 0, 1, 1, 0);

    }

    private void processCheckstyle(String filename,
                                   int nbErrors,
                                   int nbSeveritiesPossibleError,
                                   int nbSeveritiesPossibleStyle,
                                   int nbStyleErrors,
                                   int nbSeveritiesError,
                                   int nbSeveritiesNoCategory) throws Exception {

        CppcheckReport cppcheckReport = cppcheckParser.parse(new File(CppcheckParserTest.class.getResource(filename).toURI()));

        List<CppcheckFile> everyErrors = cppcheckReport.getEverySeverities();
        List<CppcheckFile> possibileErrorSeverities = cppcheckReport.getPossibleErrorSeverities();
        List<CppcheckFile> styleErrors = cppcheckReport.getStyleSeverities();
        List<CppcheckFile> possibleStyleSeverities = cppcheckReport.getPossibleStyleSeverities();
        List<CppcheckFile> errorSeverities = cppcheckReport.getErrorSeverities();
        List<CppcheckFile> noCategorySeverities = cppcheckReport.getNoCategorySeverities();

        assert possibileErrorSeverities != null;
        assert possibleStyleSeverities != null;
        assert errorSeverities != null;
        assert everyErrors != null;
        assert styleErrors != null;
        assert noCategorySeverities != null;

        Assert.assertEquals("Wrong computing of list of errors", everyErrors.size(),
                noCategorySeverities.size() + possibleStyleSeverities.size() + errorSeverities.size() + possibileErrorSeverities.size() + styleErrors.size());

        Assert.assertEquals("Wrong total number of errors", nbErrors, everyErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'possible error'", nbSeveritiesPossibleError, possibileErrorSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'possible style'", nbSeveritiesPossibleStyle, possibleStyleSeverities.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'style'", nbStyleErrors, styleErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'error'", nbSeveritiesError, errorSeverities.size());
        Assert.assertEquals("Wrong total number of errors with no category", nbSeveritiesNoCategory, noCategorySeverities.size());
    }

}
