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

import com.thalesgroup.hudson.plugins.cppcheck.util.Messages;

public class CppcheckSummary {

	private CppcheckSummary(){}

    public static String createReportSummary(CppcheckResult result){
        
        StringBuilder summary = new StringBuilder();
        int nbErrors = result.getReport().getNumberTotal();

        summary.append(Messages.getMessage("cppcheck.Errors_ProjectAction_Name"));
        summary.append(": ");
        if (nbErrors == 0){
        	summary.append(Messages.getMessage("cppcheck.ResultAction.NoError"));
        }
        else {
            summary.append("<a href=\""+CppcheckBuildAction.URL_NAME+"\">");
        
            if (nbErrors == 1) {
            	summary.append(Messages.getMessage("cppcheck.ResultAction.OneError"));
            }
            else {
            	summary.append(Messages.getMessage("cppcheck.ResultAction.MultipleErrors", nbErrors));
            }
            summary.append("</a>");
        }
        summary.append(".");
        
        return summary.toString();
    }

    
 
    public static String createReportSummaryDetails(CppcheckResult result){

    	StringBuilder builder = new StringBuilder();
    	int nbNewErrors = result.getNewNumberErrors();
    	
    	builder.append("<li>");
    	
    	if (nbNewErrors==0){
    		builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_NoNewError"));
    	}
    	else if (nbNewErrors==1){
    		builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_NewOneError"));
    	}
    	else{
			builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_NewMultipleErrors"));
	        builder.append(": ");
	        builder.append(nbNewErrors);
    	}
	    builder.append("</li>"); 
    	
        return builder.toString();
    }
}
