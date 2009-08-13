package com.thalesgroup.hudson.plugins.cppcheck;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CppcheckParserResultTest extends AbstractWorkspaceTest{
	
	private BuildListener listener;
	private VirtualChannel channel;
	
	@Before
	public void setUp() throws Exception{
		listener=mock(BuildListener.class);
        when(listener.getLogger()).thenReturn(new PrintStream(new ByteArrayOutputStream())); 
		channel=mock(VirtualChannel.class);
		super.createWorkspace();
	}
	
	@Test
	public void testNullPattern(){
		CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(listener, null);
		Assert.assertEquals("With none pattern, the default pattern must be " +CppcheckParserResult.DELAULT_REPORT_MAVEN , CppcheckParserResult.DELAULT_REPORT_MAVEN, cppcheckParserResult.getCppcheckReportPattern());
	}

	@Test
	public void testEmptyPattern(){
		CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(listener, null);
		Assert.assertEquals("With empty pattern, the default pattern must be " +CppcheckParserResult.DELAULT_REPORT_MAVEN , CppcheckParserResult.DELAULT_REPORT_MAVEN, cppcheckParserResult.getCppcheckReportPattern());
	}
	
	@Test
	public void testNoMatch() throws Exception{
		CppcheckParserResult cppcheckParserResult = new CppcheckParserResult(listener, "*.xml");
		CppcheckReport report = cppcheckParserResult.invoke(new File(workspace.toURI()), channel);
		Assert.assertEquals("A pattern with no match files is not allowed.", null, report);				
	}
	
}
