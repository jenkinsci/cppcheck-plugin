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

    private CppcheckSummary(){
        super();
    }

    public static String createReportSummary(ICheckstyleReport report){
        
        StringBuilder summary = new StringBuilder();
        int nbErrors = report.getNumberErrors();

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

    public static String createReportSummaryDetails(CppcheckReport report, CppcheckReport previousReport){

    	StringBuilder builder = new StringBuilder();

        builder.append("<li>");               
        builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_TotalErrors"));
        builder.append(": ");
        builder.append(report.getEveryErrors().size());
        if(previousReport != null){
            printDifference(report.getEveryErrors().size(), previousReport.getEveryErrors().size(), builder);
        }
        builder.append("</li>"); 

        builder.append("<li>");
        builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_ErrorsSeverityAll"));
        builder.append(": ");
        builder.append(report.getAllErrors().size());
        if(previousReport != null){
            printDifference(report.getAllErrors().size(), previousReport.getAllErrors().size(), builder);
        }
        builder.append("</li>");

        builder.append("<li>");
        builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_ErrorsSeverityStyle"));
        builder.append(": ");
        builder.append(report.getStyleErrors().size());
        if(previousReport != null){
            printDifference(report.getStyleErrors().size(), previousReport.getStyleErrors().size(), builder);
        }
        builder.append("</li>");            

        builder.append("<li>");
        builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_ErrorsSeverityAllStyle"));
        builder.append(": ");
        builder.append(report.getAllStyleErrors().size());
        if(previousReport != null){
            printDifference(report.getAllStyleErrors().size(), previousReport.getAllStyleErrors().size(), builder);
        }
        builder.append("</li>");

        builder.append("<li>");
        builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_ErrorsSeverityError"));
        builder.append(": ");
        builder.append(report.getErrorErrors().size());
        if(previousReport != null){
            printDifference(report.getErrorErrors().size(), previousReport.getErrorErrors().size(), builder);
        }
        builder.append("</li>");
        
        
        builder.append("<li>");
        builder.append(Messages.getMessage("cppcheck.ResultAction_Detail_ErrorsSeverityNoCategory"));
        builder.append(": ");
        builder.append(report.getNoCategoryErrors().size());
        if(previousReport != null){
            printDifference(report.getNoCategoryErrors().size(), previousReport.getNoCategoryErrors().size(), builder);
        }
        builder.append("</li>");        

        return builder.toString();
    }

    private static void printDifference(int current, int previous, StringBuilder builder){
        float difference = current - previous;
        builder.append(" (");

        if(difference >= 0){
            builder.append('+');
        }
        builder.append(difference);
        builder.append(")");
    }
}
