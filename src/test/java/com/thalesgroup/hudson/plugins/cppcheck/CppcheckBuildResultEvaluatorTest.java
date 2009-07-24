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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;

import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckBuildResultEvaluator;

public class CppcheckBuildResultEvaluatorTest {

	private CppcheckBuildResultEvaluator cppcheckBuildResultEvaluator;
	CppcheckHealthReportThresholds cppcheckHealthReportThresholds;
	PrintStream logger=mock(PrintStream.class);
	
	@Before
	public void initialize(){				
		cppcheckHealthReportThresholds=mock(CppcheckHealthReportThresholds.class);		
		cppcheckBuildResultEvaluator=new CppcheckBuildResultEvaluator();		
	}
	
	private Result processFailurThreshold(int failureThreshold, int errorsCount, int newErrors){
		when(cppcheckHealthReportThresholds.getFailureThreshold()).thenReturn(String.valueOf(failureThreshold));
		return cppcheckBuildResultEvaluator.evaluateBuildResult(logger, errorsCount, newErrors, cppcheckHealthReportThresholds);	
	}	
	private Result processNewFailurThreshold(int newfailureThreshold, int errorsCount, int newErrors){
		when(cppcheckHealthReportThresholds.getNewFailureThreshold()).thenReturn(String.valueOf(newfailureThreshold));
		return cppcheckBuildResultEvaluator.evaluateBuildResult(logger, errorsCount, newErrors, cppcheckHealthReportThresholds);	
	}
	private Result processNewThreshold(int newThreshold, int errorsCount, int newErrors){
		when(cppcheckHealthReportThresholds.getNewThreshold()).thenReturn(String.valueOf(newThreshold));
		return cppcheckBuildResultEvaluator.evaluateBuildResult(logger, errorsCount, newErrors, cppcheckHealthReportThresholds);	
	}	
	private Result processThreshold(int threshold, int errorsCount, int newErrors){
		when(cppcheckHealthReportThresholds.getThreshold()).thenReturn(String.valueOf(threshold));
		return cppcheckBuildResultEvaluator.evaluateBuildResult(logger, errorsCount, newErrors, cppcheckHealthReportThresholds);	
	}
	
	
	@Test
	public void testFailurExceedThreshold(){	
		
		//Serie 1 with fixed number to new error of 5
		Assert.assertEquals("Wrong result for failure threshold",Result.FAILURE,processFailurThreshold(5,6,5));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,5,5));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,3,5));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,1,5));
		
		//Serie 2 with fixed number to new error of 3
		Assert.assertEquals("Wrong result for failure threshold",Result.FAILURE,processFailurThreshold(5,6,3));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,5,3));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,3,3));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,1,3));
		
		//Serie 3 with fixed number to new error of 0
		Assert.assertEquals("Wrong result for failure threshold",Result.FAILURE,processFailurThreshold(5,6,0));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,5,0));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,3,0));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processFailurThreshold(5,1,0));
	}

	@Test
	public void testFailurNewThreshold(){	
		
		//Serie 1 with fixed number to total error of 5
		Assert.assertEquals("Wrong result for new failure threshold",Result.FAILURE,processNewFailurThreshold(5,5,6));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,5,4));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,5,3));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,5,1));
		
		//Serie 2 with fixed number to total error of 3
		Assert.assertEquals("Wrong result for new failure threshold",Result.FAILURE,processNewFailurThreshold(5,3,6));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,3,4));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,3,3));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,3,1));		

		//Serie 3 with fixed number to total error of 0
		Assert.assertEquals("Wrong result for new failure threshold",Result.FAILURE,processNewFailurThreshold(5,0,6));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,0,4));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,0,3));
		Assert.assertEquals("Wrong result for new failure threshold",Result.SUCCESS,processNewFailurThreshold(5,0,1));	
	}

	@Test
	public void testUnstableThreshold(){	
		
		//Serie 1 with fixed number to new error of 5
		Assert.assertEquals("Wrong result for failure threshold",Result.UNSTABLE,processThreshold(5,6,5));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,5,5));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,3,5));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,1,5));
		
		//Serie 2 with fixed number to new error of 3
		Assert.assertEquals("Wrong result for failure threshold",Result.UNSTABLE,processThreshold(5,6,3));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,5,3));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,3,3));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,1,3));
		
		//Serie 3 with fixed number to new error of 0
		Assert.assertEquals("Wrong result for failure threshold",Result.UNSTABLE,processThreshold(5,6,0));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,5,0));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,3,0));
		Assert.assertEquals("Wrong result for failure threshold",Result.SUCCESS,processThreshold(5,1,0));
	}	
	
	@Test
	public void testUnstableNewThreshold(){	
		
		//Serie 1 with fixed number to total error of 5
		Assert.assertEquals("Wrong result for new threshold",Result.UNSTABLE,processNewThreshold(5,5,6));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,5,4));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,5,3));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,5,1));
		
		//Serie 2 with fixed number to total error of 3
		Assert.assertEquals("Wrong result for new threshold",Result.UNSTABLE,processNewThreshold(5,3,6));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,3,4));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,3,3));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,3,1));		

		//Serie 3 with fixed number to total error of 0
		Assert.assertEquals("Wrong result for new threshold",Result.UNSTABLE,processNewThreshold(5,0,6));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,0,4));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,0,3));
		Assert.assertEquals("Wrong result for new threshold",Result.SUCCESS,processNewThreshold(5,0,1));
	}
	
	private Result processTestCaseLimit1(int newFailureThreshold, int newThreshold, int errorsCount, int newErrors){
		when(cppcheckHealthReportThresholds.getNewFailureThreshold()).thenReturn(String.valueOf(newFailureThreshold));
		when(cppcheckHealthReportThresholds.getNewThreshold()).thenReturn(String.valueOf(newThreshold));
		return cppcheckBuildResultEvaluator.evaluateBuildResult(logger, errorsCount, newErrors, cppcheckHealthReportThresholds);	
	}
	private Result processTestCaseLimit2(int failureThreshold, int threshold, int errorsCount, int newErrors){
		when(cppcheckHealthReportThresholds.getFailureThreshold()).thenReturn(String.valueOf(failureThreshold));
		when(cppcheckHealthReportThresholds.getThreshold()).thenReturn(String.valueOf(threshold));
		return cppcheckBuildResultEvaluator.evaluateBuildResult(logger, errorsCount, newErrors, cppcheckHealthReportThresholds);	
	}

	
	
	@Test
	public void testCaseLimit1(){

		//new new failure error wins over new threshold - Serie 1 for 0 errors
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,0,4));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,0,5));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.FAILURE,processTestCaseLimit1(5,5,0,6));

		//new new failure error wins over new threshold - Serie 2 for 3 errors
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,3,4));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,3,5));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.FAILURE,processTestCaseLimit1(5,5,3,6));
		
		//new new failure error wins over new threshold - Serie 3 for 5 errors
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,5,4));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,5,5));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.FAILURE,processTestCaseLimit1(5,5,5,6));
		
		//new new failure error wins over new threshold - Serie 4 for 6 errors
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,6,4));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.SUCCESS,processTestCaseLimit1(5,5,6,5));
		Assert.assertEquals("Wrong result for new failure error over new threshold",Result.FAILURE,processTestCaseLimit1(5,5,6,6));		
	}
	
	@Test
	public void testCaseLimit2(){

		//new failure error wins over threshold - Serie 1 for 0 new errors
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,4,0));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,5,0));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.FAILURE,processTestCaseLimit2(5,5,6,0));

		//new failure error wins over threshold - Serie 2 for 3 new errors
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,4,3));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,5,3));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.FAILURE,processTestCaseLimit2(5,5,6,3));
		
		//new failure error wins over threshold - Serie 3 for 5 new errors
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,4,5));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,5,5));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.FAILURE,processTestCaseLimit2(5,5,6,5));
		
		//new failure error wins over threshold - Serie 4 for 6 new errors
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,4,6));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.SUCCESS,processTestCaseLimit2(5,5,5,6));
		Assert.assertEquals("Wrong result for failure error over threshold",Result.FAILURE,processTestCaseLimit2(5,5,6,6));		
	}	
	
}
