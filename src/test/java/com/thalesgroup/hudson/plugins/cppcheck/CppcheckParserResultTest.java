package com.thalesgroup.hudson.plugins.cppcheck;

import static org.mockito.Mockito.mock;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CppcheckParserResultTest extends AbstractWorkspaceTest{
	
	private PrintStream logger;
	private VirtualChannel channel;
	
	@Before
	public void setUp() throws Exception{
		logger=mock(PrintStream.class);
		channel=mock(VirtualChannel.class);
		super.createWorkspace();
	}
	
	@Test
	public void testNullPattern(){
		CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(logger, null);
		Assert.assertEquals("With none pattern, the default pattern must be " +CppcheckParserResult.DELAULT_REPORT_MAVEN , CppcheckParserResult.DELAULT_REPORT_MAVEN, cppcheckParserResult.getCppcheckReportPattern());
	}

	@Test
	public void testEmptyPattern(){
		CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(logger, null);
		Assert.assertEquals("With empty pattern, the default pattern must be " +CppcheckParserResult.DELAULT_REPORT_MAVEN , CppcheckParserResult.DELAULT_REPORT_MAVEN, cppcheckParserResult.getCppcheckReportPattern());
	}
	
	@Test
	public void testNoMatch() throws Exception{
		CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(logger, "*.xml");
		try{
			cppcheckParserResult.invoke(new File(workspace.toURI()), channel);
			Assert.fail("A pattern with no match files is not allowed.");
		}
		catch (Exception iae){
			Assert.assertTrue(true);				
		}
	}
	
}
