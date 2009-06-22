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



public class CppcheckSummary {

    private CppcheckSummary(){
        super();
    }

    public static String createReportSummary(CppcheckReport report, CppcheckReport previous){
        StringBuilder builder = new StringBuilder();
        builder.append("<a href=\"" + CppcheckBuildAction.URL_NAME + "\">Cppcheck Results</a>");
        builder.append("\n");
        return builder.toString();
    }

    public static String createReportSummaryDetails(CppcheckReport report, CppcheckReport previousReport){

    	StringBuilder builder = new StringBuilder();
  
    
        builder.append("<li>");               
        builder.append("Number of errors :");
        builder.append(report.getErrors().size());
        if(previousReport != null){
            printDifference(report.getErrors().size(), previousReport.getErrors().size(), builder);
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
