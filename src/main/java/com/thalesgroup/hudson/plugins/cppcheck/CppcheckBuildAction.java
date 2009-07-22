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

import hudson.model.*;

import java.io.Serializable;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import static com.thalesgroup.hudson.plugins.cppcheck.CppcheckHealthReportThresholds.*;


public class CppcheckBuildAction implements Action, Serializable, StaplerProxy, HealthReportingAction {

    public static final String URL_NAME = "cppcheckResult";

    private AbstractBuild<?,?> build;
    private CppcheckResult result;
    private CppcheckHealthReportThresholds cppcheckHealthReportThresholds;

    public CppcheckBuildAction(AbstractBuild<?,?> build, CppcheckResult result, CppcheckHealthReportThresholds cppcheckHealthReportThresholds){
        this.build = build;
        this.result = result;
        this.cppcheckHealthReportThresholds=cppcheckHealthReportThresholds;
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
    
    public  boolean isHealthyReportEnabled(CppcheckHealthReportThresholds cppcheckHealthReportThresholds) {
        if (isValid(cppcheckHealthReportThresholds.getHealthy()) && isValid(cppcheckHealthReportThresholds.getUnHealthy())) {
            int healthyNumber = convert(cppcheckHealthReportThresholds.getHealthy());
            int unHealthyNumber = convert(cppcheckHealthReportThresholds.getUnHealthy());
            return unHealthyNumber > healthyNumber;
        }
        return false;
    }


    public HealthReport getBuildHealth() {
        if (cppcheckHealthReportThresholds == null) {
            // no thresholds => no report
            return null;
        }

        if (isHealthyReportEnabled(cppcheckHealthReportThresholds)) {
            int percentage;
            int counter =  getNumberErrors(cppcheckHealthReportThresholds.getThresholdLimit(),false);
            
            if (counter < convert(cppcheckHealthReportThresholds.getHealthy())) {
                percentage = 100;
            }
            else if (counter > convert(cppcheckHealthReportThresholds.getUnHealthy())) {
                percentage = 0;
            }
            else {
                percentage = 100 - ((counter - convert(cppcheckHealthReportThresholds.getHealthy())) * 100
                        / (convert(cppcheckHealthReportThresholds.getUnHealthy()) - convert(cppcheckHealthReportThresholds.getHealthy())));
            }
            return new HealthReport(percentage, "Build stability for " + cppcheckHealthReportThresholds.getThresholdLimit() + " severity.");
        }
        return null;
    }


    public int getNumberErrors(String thresholdLimit, boolean newError){

        int nbErrors= 0;
        int nbPreviousError=0;
        CppcheckResult previousResult=getPreviousResult();

        if ("all".equals(thresholdLimit)){
            nbErrors= getResult().getReport().getAllErrors().size();
            if (previousResult!=null){
                  previousResult.getReport().getAllErrors().size();
            }
        }
        else if ("style".equals(thresholdLimit)){
            nbErrors= getResult().getReport().getStyleErrors().size();
            if (previousResult!=null){
                  previousResult.getReport().getStyleErrors().size();
            }

        }
        else if ("all style".equals(thresholdLimit)){
            nbErrors= getResult().getReport().getAllStyleErrors().size();
            if (previousResult!=null){
                  previousResult.getReport().getAllStyleErrors().size();
            }

        }
        else if ("error".equals(thresholdLimit)){
            nbErrors= getResult().getReport().getErrorErrors().size();
            if (previousResult!=null){
                  previousResult.getReport().getErrorErrors().size();
            }

        }
        else{
            nbErrors= getResult().getReport().getEveryErrors().size();
            if (previousResult!=null){
                  previousResult.getReport().getEveryErrors().size();
            }
        }

        if (newError)   {
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


    static boolean isErrorCountExceeded(final int errorCount, final String errorThreshold) {
        if (errorCount > 0 && isValid(errorThreshold)) {
            return errorCount > convert(errorThreshold);
        }
        return false;
    }

}
