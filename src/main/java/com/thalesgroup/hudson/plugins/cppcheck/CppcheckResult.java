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

import hudson.model.AbstractBuild;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;


public class CppcheckResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private CppcheckReport report;
	private AbstractBuild<?,?> owner;

    public CppcheckResult(CppcheckReport report,  AbstractBuild<?,?> owner){
        this.report = report;
        this.owner = owner;
    }

    public CppcheckReport getReport(){
        return report;
    }

    public AbstractBuild<?,?> getOwner(){
        return owner;
    }
    
    
    /**
     * Returns the dynamic result of the selection element.
     *
     * @param link
     *            the link to identify the sub page to show
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     * @return the dynamic result of the analysis (detail page).
     */
     public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) {
    	 
    	 if (link.startsWith("source.")) { 
    		 Map<Integer, CppcheckFile> agregateMap = report.getInternalMap();
    		 if (agregateMap!=null){
    			 CppcheckFile vCppcheckFile = agregateMap.get(Integer.parseInt(StringUtils.substringAfter(link,"source.")));  
    			 if (vCppcheckFile==null){
    				 throw new IllegalArgumentException("Error for retrieving the source file with link:"+link);
    			 }
    			 return new CppcheckSource(owner, vCppcheckFile);
    		 }
    	 }
    	 return null;    
     }
    
     public String getSummary(){
         return CppcheckSummary.createReportSummary(report);
     }

     public String getDetails(){
         return CppcheckSummary.createReportSummaryDetails(report,getPreviousReport());
     }
     
     private CppcheckReport getPreviousReport(){
         CppcheckResult previous = this.getPreviousResult();
         if(previous == null){
             return null;
         }else{
            return previous.getReport();
         }
     }

     CppcheckResult getPreviousResult(){
         CppcheckBuildAction previousAction = getPreviousAction();
         CppcheckResult previousResult = null;
         if(previousAction != null){
             previousResult = previousAction.getResult();
         }
         
         return previousResult;
     }

     CppcheckBuildAction getPreviousAction(){
         AbstractBuild<?,?> previousBuild = owner.getPreviousBuild();
         if(previousBuild != null){
             return previousBuild.getAction(CppcheckBuildAction.class);
         }
         return null;
     }
     
     public int getNewNumberErrors(){
    	 CppcheckResult previousCppcheckResult = getPreviousResult();
    	 if (previousCppcheckResult==null){
    		 return 0;
    	 }
    	 else {
    		 int diff = report.getNumberErrors()-previousCppcheckResult.getReport().getNumberErrors();
    		 return (diff>0)?diff:0;
    	 }
     }

}
