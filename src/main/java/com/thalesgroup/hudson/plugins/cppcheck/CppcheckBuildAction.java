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
import hudson.model.Action;
import java.io.Serializable;
import org.kohsuke.stapler.StaplerProxy;


public class CppcheckBuildAction implements Action, Serializable, StaplerProxy {

    public static final String URL_NAME = "cppcheckResult";

    private AbstractBuild<?,?> build;
    private CppcheckResult result;

    public CppcheckBuildAction(AbstractBuild<?,?> build, CppcheckResult result){
        this.build = build;
        this.result = result;
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-24.png";
    }

    public String getDisplayName() {
        return "Cppcheck Results";
    }

    public String getUrlName() {
        return URL_NAME;
    }

    public String getSummary(){
        return CppcheckSummary.createReportSummary(result.getReport(), this.getPreviousReport());
    }

    public String getDetails(){
        return CppcheckSummary.createReportSummaryDetails(result.getReport(), this.getPreviousReport());
    }

    public CppcheckResult getResult(){
        return this.result;
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
        CppcheckBuildAction previousAction = this.getPreviousAction();
        CppcheckResult previousResult = null;
        if(previousAction != null){
            previousResult = previousAction.getResult();
        }
        
        return previousResult;
    }

    CppcheckBuildAction getPreviousAction(){
        AbstractBuild<?,?> previousBuild = this.build.getPreviousBuild();
        if(previousBuild != null){
            return previousBuild.getAction(CppcheckBuildAction.class);
        }
        return null;
    }

    AbstractBuild<?,?> getBuild(){
        return this.build;
    }

    public Object getTarget() {
        return this.result;
    }

}
