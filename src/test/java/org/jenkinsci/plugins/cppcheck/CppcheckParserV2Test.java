package org.jenkinsci.plugins.cppcheck;

import org.jenkinsci.plugins.cppcheck.CppcheckReport;
import org.jenkinsci.plugins.cppcheck.model.CppcheckFile;
import org.jenkinsci.plugins.cppcheck.parser.CppcheckParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author Gregory Boissinot
 */
public class CppcheckParserV2Test {


    CppcheckParser cppcheckParser;

    @Before
    public void setUp() throws Exception {
        cppcheckParser = new CppcheckParser();
    }

    @Test
    public void testcppcheck1Version2() throws Exception {
        processCppcheck("version2/testCppcheck.xml", 16, 1, 13, 0, 2, 0);
    }

    private void processCppcheck(String filename,
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
