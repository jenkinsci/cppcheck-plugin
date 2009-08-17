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

import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckBuildHealthEvaluator;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import org.kohsuke.stapler.StaplerProxy;

import java.io.Serializable;


public class CppcheckBuildAction implements Action, Serializable, StaplerProxy, HealthReportingAction {

    public static final String URL_NAME = "cppcheckResult";

    private AbstractBuild<?,?> build;
    private CppcheckResult result;
    private CppcheckConfig cppcheckConfig;

    public CppcheckBuildAction(AbstractBuild<?,?> build, CppcheckResult result, CppcheckConfig cppcheckConfig){
        this.build = build;
        this.result = result;
        this.cppcheckConfig=cppcheckConfig;
    }

    public String getIconFileName() {
        return "/plugin/cppcheck/icons/cppcheck-24.png";
    }

    public String getDisplayName() {
        return "Cppcheck Result";
    }

    public String getUrlName() {
        return URL_NAME;
    }



    public CppcheckResult getResult(){
        return this.result;
    }


    AbstractBuild<?,?> getBuild(){
        return this.build;
    }

    public Object getTarget() {
        return this.result;
    }
    

    public HealthReport getBuildHealth() {
        return  new CppcheckBuildHealthEvaluator().evaluatBuildHealth(cppcheckConfig, getNumberErrors(cppcheckConfig,false));
    }


    public int getNumberErrors(CppcheckConfig cppecheckConfig, boolean checkNewError){

        int nbErrors= 0;
        int nbPreviousError=0;
        CppcheckResult previousResult=result.getPreviousResult();

        if (cppecheckConfig.isSeverityPossibleError()){
            nbErrors= getResult().getReport().getPossibleErrorSeverities().size();
            if (previousResult!=null){
            	nbPreviousError= previousResult.getReport().getPossibleErrorSeverities().size();
            }
        }
        
        if (cppecheckConfig.isSeverityStyle()){
            nbErrors= nbErrors+ getResult().getReport().getStyleSeverities().size();
            if (previousResult!=null){
            	nbPreviousError=  nbPreviousError + previousResult.getReport().getStyleSeverities().size();
            }

        }
        
        if (cppecheckConfig.isSeverityPossibleStyle()){
            nbErrors= nbErrors+ getResult().getReport().getPossibleStyleSeverities().size();
            if (previousResult!=null){
            	nbPreviousError= nbPreviousError + previousResult.getReport().getPossibleStyleSeverities().size();
            }
        }
        
        if (cppecheckConfig.isSeverityError()){
            nbErrors= nbErrors + getResult().getReport().getErrorSeverities().size();
            if (previousResult!=null){
            	nbPreviousError= nbPreviousError + previousResult.getReport().getErrorSeverities().size();
            }
        }
        
        if (checkNewError)   {
            if (previousResult!=null){
                return nbErrors-nbPreviousError;
            }
            else {
                return 0;
            }
        }
        else
            return  nbErrors;
    }

}
