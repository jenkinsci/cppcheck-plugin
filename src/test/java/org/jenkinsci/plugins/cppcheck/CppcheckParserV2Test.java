package org.jenkinsci.plugins.cppcheck;

import org.jenkinsci.plugins.cppcheck.CppcheckReport;
import org.jenkinsci.plugins.cppcheck.model.CppcheckFile;
import org.jenkinsci.plugins.cppcheck.parser.CppcheckParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * @author Gregory Boissinot
 */
public class CppcheckParserV2Test {

    hudson.model.TaskListener listener = mock(hudson.model.TaskListener.class);
    CppcheckParser cppcheckParser;

    @Before
    public void setUp() throws Exception {
        cppcheckParser = new CppcheckParser();
    }

    @Test
    public void testcppcheck1Version2() throws Exception {
        processCppcheck("version2/testCppcheck.xml", 16, 2, 1, 0, 0, 13, 0);
    }

    private void processCppcheck(String filename,
                                 int nbAllErrors,
                                 int nbError,
                                 int nbWarning,
                                 int nbStyle,
                                 int nbPerformance,
                                 int nbInformation,
                                 int nbNoCategory) throws Exception {

        CppcheckReport cppcheckReport = cppcheckParser.parse(new File(CppcheckParserV2Test.class.getResource(filename).toURI()), listener);

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
