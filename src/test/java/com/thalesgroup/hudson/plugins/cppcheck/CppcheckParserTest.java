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


import hudson.AbortException;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;

public class CppcheckParserTest extends AbstractWorkspaceTest{


    private VirtualChannel virtualChannel;
    
    @Before
    public void setUp() throws Exception {
        super.createWorkspace();
        virtualChannel = mock(VirtualChannel.class);
    }

    
    @Test
    public void nonFile() throws Exception{
    	    	
    	try{
    		CppcheckParser cppcheckParser = new CppcheckParser(null);
    		cppcheckParser.invoke(new File(workspace.toURI()), virtualChannel);
    		Assert.fail();
    	}
    	catch (IllegalArgumentException iea){
    		Assert.assertTrue(true);
    	}
    }	
    
    @Test
    public void fileNotFound() throws Exception{
    	    	
    	try{
    		CppcheckParser cppcheckParser = new CppcheckParser(new FilePath(new File("notFound")));
    		cppcheckParser.invoke(new File(workspace.toURI()), virtualChannel);
    		Assert.fail();
    	}
    	catch (AbortException ae){
    		Assert.assertTrue(true);
    	}
    }	
    	
    /**
     * Tests parsing of a simple cppcheck file.
     *
     * @throws InvocationTargetException Signals that an I/O exception has occurred
     */
    @Test
    public void analyseCheckStyleFile() throws Exception {
        URL url = this.getClass().getResource("testcppcheck1.xml");
        
		CppcheckParser cppcheckParser = new CppcheckParser(new FilePath(new File(url.toURI())));
		CppcheckReport cppcheckReport = cppcheckParser.invoke(new File(workspace.toURI()), virtualChannel);       
        
        List<CppcheckFile> everyErrors = cppcheckReport.getEveryErrors();
        
        List<CppcheckFile> allErrors = cppcheckReport.getAllErrors();
        List<CppcheckFile> styleErrors = cppcheckReport.getStyleErrors();
        List<CppcheckFile> allStyleErrors = cppcheckReport.getAllStyleErrors();
        List<CppcheckFile> errosErrors = cppcheckReport.getErrorErrors();
        List<CppcheckFile> noCategoryErrors = cppcheckReport.getNoCategoryErrors();
                
        assert allErrors!=null;
        assert allStyleErrors!=null;
        assert errosErrors!=null;
        assert everyErrors!=null;
        assert styleErrors!=null;
        assert noCategoryErrors!=null;
        
        Assert.assertEquals("Wrong computing of list of errors", everyErrors.size(), 
        		noCategoryErrors.size()+ allStyleErrors.size() +  errosErrors.size()+ allErrors.size() + styleErrors.size());
        
        Assert.assertEquals("Wrong total number of errors", 13, everyErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'all'", 2, allErrors.size());             
        Assert.assertEquals("Wrong total number of errors for the severity 'allStyle'", 0, allStyleErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'style'", 1, styleErrors.size());
        Assert.assertEquals("Wrong total number of errors for the severity 'errors'", 8, errosErrors.size());
        Assert.assertEquals("Wrong total number of errors with no category", 2, noCategoryErrors.size());
    }
	
}
