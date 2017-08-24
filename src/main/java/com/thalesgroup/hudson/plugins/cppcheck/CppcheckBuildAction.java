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

import com.thalesgroup.hudson.plugins.cppcheck.config.CppcheckConfig;
import com.thalesgroup.hudson.plugins.cppcheck.config.CppcheckConfigGraph;
import com.thalesgroup.hudson.plugins.cppcheck.graph.CppcheckGraph;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckFile;
import com.thalesgroup.hudson.plugins.cppcheck.model.CppcheckSourceContainer;
import com.thalesgroup.hudson.plugins.cppcheck.util.AbstractCppcheckBuildAction;
import com.thalesgroup.hudson.plugins.cppcheck.util.CppcheckBuildHealthEvaluator;
import hudson.model.Run;
import hudson.model.HealthReport;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import org.jenkinsci.plugins.cppcheck.config.CppcheckConfigSeverityEvaluation;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


public class CppcheckBuildAction extends AbstractCppcheckBuildAction {

    public static final String URL_NAME = "cppcheckResult";

    private CppcheckResult result;
    private CppcheckConfig cppcheckConfig;

    public CppcheckBuildAction(Run<?, ?> owner, CppcheckResult result, CppcheckConfig cppcheckConfig) {
        super(owner);
        this.result = result;
        this.cppcheckConfig = cppcheckConfig;
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

    public String getSearchUrl() {
        return getUrlName();
    }

    public CppcheckResult getResult() {
        return this.result;
    }

    Run<?, ?> getBuild() {
        return this.owner;
    }

    public Object getTarget() {
        return this.result;
    }

    public HealthReport getBuildHealth() {
        try {
            return new CppcheckBuildHealthEvaluator().evaluatBuildHealth(cppcheckConfig, result.getNumberErrorsAccordingConfiguration(cppcheckConfig, false));
        } catch (IOException ioe) {
            return new HealthReport();
        }
    }

    private DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (CppcheckBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);

            //a.getResult().getOwner().getResult()

            CppcheckReport report = a.getResult().getReport();

            CppcheckConfigGraph configGraph = cppcheckConfig.getConfigGraph();

            if (configGraph.isDisplaySeverityStyle())
                dsb.add(report.getNumberSeverityStyle(), "Severity 'style'", label);
            if (configGraph.isDisplaySeverityPossibleStyle())
                dsb.add(report.getNumberSeverityPossibleStyle(), "Severity 'possibe style'", label);
            if (configGraph.isDisplaySeverityPossibleError())
                dsb.add(report.getNumberSeverityPossibleError(), "Severity 'possible error'", label);
            if (configGraph.isDisplaySeverityError())
                dsb.add(report.getNumberSeverityError(), "Severity 'error'", label);
            if (configGraph.isDiplayAllError())
                dsb.add(report.getNumberTotal(), "All errors", label);

        }
        return dsb;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if (ChartUtil.awtProblemCause != null) {
            rsp.sendRedirect2(req.getContextPath() + "/images/headless.png");
            return;
        }

        Calendar timestamp = getBuild().getTimestamp();

        if (req.checkIfModified(timestamp, rsp)) return;

        Graph g = new CppcheckGraph(getOwner(), getDataSetBuilder().build(),
                "Number of error", cppcheckConfig.getConfigGraph().getXSize(), cppcheckConfig.getConfigGraph().getYSize());
        g.doPng(req, rsp);
    }

    // Backward compatibility. Do not remove.
    // CPPCHECK:OFF
    @Deprecated
    private transient Run<?, ?> build;

    /**
     * Initializes members that were not present in previous versions of this plug-in.
     *
     * @return the created object
     */
    @SuppressWarnings("deprecation")
    private Object readResolve() {
        if (build != null) {
            this.owner = build;
        }

        //Report
        CppcheckReport report = result.getReport();
        org.jenkinsci.plugins.cppcheck.CppcheckReport newReport = new org.jenkinsci.plugins.cppcheck.CppcheckReport();
        if (report != null) {
            newReport.setAllErrors(report.getEverySeverities());
            newReport.setErrorSeverityList(report.getErrorSeverities());
            newReport.setWarningSeverityList(report.getPossibleErrorSeverities());
            newReport.setStyleSeverityList(report.getStyleSeverities());
            newReport.setPerformanceSeverityList(report.getPossibleStyleSeverities());
            newReport.setInformationSeverityList(report.getNoCategorySeverities());
            newReport.setNoCategorySeverityList(new ArrayList<CppcheckFile>());
            newReport.setPortabilitySeverityList(new ArrayList<CppcheckFile>());
        }

        //Result
        CppcheckSourceContainer sourceContainer = result.getCppcheckSourceContainer();
        org.jenkinsci.plugins.cppcheck.CppcheckSourceContainer newSourceContainer = new org.jenkinsci.plugins.cppcheck.CppcheckSourceContainer(sourceContainer.getInternalMap());
        org.jenkinsci.plugins.cppcheck.CppcheckResult newResult = new org.jenkinsci.plugins.cppcheck.CppcheckResult(newReport, newSourceContainer, getOwner());

        //Config
        org.jenkinsci.plugins.cppcheck.config.CppcheckConfig newConfig = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfig();

        newConfig.setPattern(cppcheckConfig.getCppcheckReportPattern());
        newConfig.setIgnoreBlankFiles(cppcheckConfig.isIgnoreBlankFiles());
        CppcheckConfigSeverityEvaluation configSeverityEvaluation = new CppcheckConfigSeverityEvaluation(
                cppcheckConfig.getConfigSeverityEvaluation().getThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getNewThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getFailureThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getNewFailureThreshold(),
                cppcheckConfig.getConfigSeverityEvaluation().getHealthy(),
                cppcheckConfig.getConfigSeverityEvaluation().getUnHealthy(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityError(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityPossibleError(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityStyle(),
                cppcheckConfig.getConfigSeverityEvaluation().isSeverityPossibleStyle(),
                true, true, true);
        newConfig.setConfigSeverityEvaluation(configSeverityEvaluation);
        org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph configGraph = new org.jenkinsci.plugins.cppcheck.config.CppcheckConfigGraph(
                cppcheckConfig.getConfigGraph().getXSize(),
                cppcheckConfig.getConfigGraph().getYSize(),
                0,
                cppcheckConfig.getConfigGraph().isDiplayAllError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityPossibleError(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityStyle(),
                cppcheckConfig.getConfigGraph().isDisplaySeverityPossibleStyle(),
                true, true, true);
        newConfig.setConfigGraph(configGraph);


        return new org.jenkinsci.plugins.cppcheck.CppcheckBuildAction(owner,
                newResult, newConfig, 100);
    }
}
