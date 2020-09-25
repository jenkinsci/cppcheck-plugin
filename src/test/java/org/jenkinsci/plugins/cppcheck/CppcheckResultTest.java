package org.jenkinsci.plugins.cppcheck;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import hudson.model.AbstractBuild;

public class CppcheckResultTest {
	
	private AbstractBuild owner;
	private CppcheckResult result1;
	private CppcheckResult result2;

	@Before
	public void setUp() throws Exception {
		owner = mock(AbstractBuild.class);
		result1 = new CppcheckResult(new CppcheckStatistics(3, 3, 3, 3, 3, 3, 3, null), owner);
		result2 = new CppcheckResult(new CppcheckStatistics(1, 1, 1, 1, 1, 1, 1, null), owner);
	}

	@Test
	public void testCorrectNumberOfDiffsCalculatedWithoutBaseline() {
		CppcheckStatistics diff1 = result1.getDiff();
		
		assertEquals(0, diff1.getNumberErrorSeverity());
		assertEquals(0, diff1.getNumberWarningSeverity());
		assertEquals(0, diff1.getNumberStyleSeverity());
		assertEquals(0, diff1.getNumberPerformanceSeverity());
		assertEquals(0, diff1.getNumberInformationSeverity());
		assertEquals(0, diff1.getNumberNoCategorySeverity());
		assertEquals(0, diff1.getNumberPortabilitySeverity());
		assertEquals(0, diff1.getNumberTotal());
	}
	
	@Test
	public void testCorrectNumberOfDiffsCalculatedWithBaseline() {
		result1.setBaselineResult(result2);
		CppcheckStatistics diff1 = result1.getDiff();
		
		assertEquals(2, diff1.getNumberErrorSeverity());
		assertEquals(2, diff1.getNumberWarningSeverity());
		assertEquals(2, diff1.getNumberStyleSeverity());
		assertEquals(2, diff1.getNumberPerformanceSeverity());
		assertEquals(2, diff1.getNumberInformationSeverity());
		assertEquals(2, diff1.getNumberNoCategorySeverity());
		assertEquals(2, diff1.getNumberPortabilitySeverity());
		assertEquals(14, diff1.getNumberTotal());
	}

}
