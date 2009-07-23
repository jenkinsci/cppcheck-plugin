package com.thalesgroup.hudson.plugins.cppcheck;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.junit.Test;

public class CppcheckSummaryTest {

    /**
     * Parameterized test case to check the message text for the specified
     * number of erors
     *
     * @param numberOfErrors
     *            the number of errors
     * @param expectedMessage
     *            the expected message
     */
    private void checkSummaryText(final int numberOfErrors, final String expectedMessage) {
    	
    	ICheckstyleReport result = createMock(ICheckstyleReport.class);
        expect(result.getNumberErrors()).andReturn(numberOfErrors).anyTimes();

        replay(result);

        Assert.assertEquals("Wrong summary message created.", expectedMessage, CppcheckSummary.createReportSummary(result));

        verify(result);
    }
    
    /**
     * Checks the text for 0 error.
     */
    @Test
    public void test0Errors() {
        checkSummaryText(0,  "Cppcheck: no error.");
    }

    /**
     * Checks the text for one error.
     */
    @Test
    public void test1Errors() {
        checkSummaryText(1, "Cppcheck: <a href=\"cppcheckResult\">1 error</a>.");
    }

    /**
     * Checks the text for 5 errors.
     */
    @Test
    public void test5Errors() {
        checkSummaryText( 5, "Cppcheck: <a href=\"cppcheckResult\">5 errors</a>.");
    }


	
}
