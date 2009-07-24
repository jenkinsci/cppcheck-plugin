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

package com.thalesgroup.hudson.plugins.cppcheck;

import static org.mockito.Mockito.*;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CppcheckSummaryTest {

	/**
	 * Initializes the locale to English.
	 */
	@Before
	public void initializeLocale() {
		Locale.setDefault(Locale.ENGLISH);
	}


	/**
	 * Parameterized test case to check the message text for the specified
	 * number of erors
	 * 
	 * @param numberOfErrors
	 *            the number of errors
	 * @param expectedMessage
	 *            the expected message
	 */
	private void checkSummaryText(final int numberOfErrors,
			final String expectedMessage) {
		
		CppcheckReport report = mock(CppcheckReport.class);
		CppcheckResult result = mock(CppcheckResult.class);
		
		when(result.getReport()).thenReturn(report);		
		when(result.getReport().getNumberTotal()).thenReturn(numberOfErrors);
		
		Assert.assertEquals("Wrong summary detail message created.",expectedMessage, CppcheckSummary.createReportSummary(result));		
	}

	/**
	 * Checks the text for 0 error.
	 */
	@Test
	public void test0Errors() {
		checkSummaryText(0, "Cppcheck: no error.");
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
		checkSummaryText(5,"Cppcheck: <a href=\"cppcheckResult\">5 errors</a>.");
	}
			
	

	/**
	 * Parameterized test case to check the detail message text for the
	 * specified number of erors
	 * 
	 * @param numberOfErrors
	 *            the number of errors
	 * @param expectedMessage
	 *            the expected message
	 */
	private void checkSummaryDetailsText(final int numberOfErrors,
			final String expectedMessage) {

		CppcheckResult result = mock(CppcheckResult.class);
		
		when(result.getNewNumberErrors()).thenReturn(numberOfErrors);

		Assert.assertEquals("Wrong summary detail message created.",expectedMessage, CppcheckSummary.createReportSummaryDetails(result));

	}

	/**
	 * Checks the text for no new errors.
	 */
	@Test
	public void test0NewErrors() {
		checkSummaryDetailsText(0, "<li>No new error</li>");
	}

	/**
	 * Checks the text for one new error.
	 */
	@Test
	public void test1NewErrors() {
		checkSummaryDetailsText(1, "<li>One new error</li>");
	}

	/**
	 * Checks the text for one fives errors.
	 */
	@Test
	public void test5NewErrors() {
		checkSummaryDetailsText(5, "<li>New Errors: 5</li>");
	}

}
