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

import hudson.FilePath;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import com.thalesgroup.hudson.plugins.cppcheck.parser.CppcheckParser;
import com.thalesgroup.hudson.plugins.cppcheck.util.Messages;

public class CppcheckParserResult implements FilePath.FileCallable<CppcheckReport> {

	private static final long serialVersionUID = 1L;

    private final BuildListener listener;
    
	private final String cppcheckReportPattern;

	public static final String DELAULT_REPORT_MAVEN  ="**/cppcheck-result.xml";
	

	public CppcheckParserResult(final BuildListener listener,  String cppcheckReportPattern) {

		if (cppcheckReportPattern==null){
			cppcheckReportPattern=DELAULT_REPORT_MAVEN;
		}
		
		if (cppcheckReportPattern.trim().length()==0){
			cppcheckReportPattern=DELAULT_REPORT_MAVEN;
		}
		
		this.listener=listener;
		this.cppcheckReportPattern = cppcheckReportPattern;
	}

	public CppcheckReport invoke(java.io.File moduleRoot, VirtualChannel channel) throws IOException {
     	
		CppcheckReport cppcheckReportResult = new CppcheckReport();
		try {
			String[] cppcheckFiles = findCppcheckReports(moduleRoot);
			if (cppcheckFiles.length==0){
	            String msg = "No cppcheck test report file(s) were found with the pattern '"
	                + cppcheckReportPattern + "' relative to '"
	                + moduleRoot + "'."
	                + "  Did you enter a pattern relative to the correct directory?"
	                + "  Did you generate the XML report(s) for Cppcheck?";				
				throw  new IllegalArgumentException(msg);
			}
			
			Messages.log(listener,"Processing "+cppcheckFiles.length+ " files with the pattern '" + cppcheckReportPattern + "'.");
			
			for (String cppcheckFile : cppcheckFiles){
				CppcheckReport cppcheckReport= new CppcheckParser().parse(new File(moduleRoot,cppcheckFile));
				mergeReport(cppcheckReportResult,cppcheckReport);
			}
        }
        catch (Exception e) {
        	Messages.log(listener,"Parsing has been canceled. " + e.getMessage());
        	return null;
        }
        
        return cppcheckReportResult;
	}
	

    private static void mergeReport(CppcheckReport cppcheckReportResult, CppcheckReport cppcheckReport) {		
    	cppcheckReportResult.getPossibleErrorSeverities().addAll(cppcheckReport.getPossibleErrorSeverities());
    	cppcheckReportResult.getPossibleStyleSeverities().addAll(cppcheckReport.getPossibleStyleSeverities());
    	cppcheckReportResult.getErrorSeverities().addAll(cppcheckReport.getErrorSeverities());
    	cppcheckReportResult.getEverySeverities().addAll(cppcheckReport.getEverySeverities());
    	cppcheckReportResult.getInternalMap().putAll(cppcheckReport.getInternalMap());
    	cppcheckReportResult.getNoCategorySeverities().addAll(cppcheckReport.getNoCategorySeverities());
    	cppcheckReportResult.getStyleSeverities().addAll(cppcheckReport.getStyleSeverities());		
	}	
	
	/**
     * Return all cppechk report files
     * 
     * @param parentPath parent
     * @return an array of strings
     */
    private String[] findCppcheckReports(File parentPath)  {
        FileSet fs = Util.createFileSet(parentPath, this.cppcheckReportPattern);
        DirectoryScanner ds = fs.getDirectoryScanner();
        String[] cppcheckFiles = ds.getIncludedFiles();
        return cppcheckFiles;
    }

	public String getCppcheckReportPattern() {
		return cppcheckReportPattern;
	}

}
