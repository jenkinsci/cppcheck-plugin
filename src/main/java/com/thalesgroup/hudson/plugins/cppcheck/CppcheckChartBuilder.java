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

import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;



public class CppcheckChartBuilder {

    private CppcheckChartBuilder(){
        super();
    }

    public static JFreeChart buildChart(CppcheckBuildAction action){
        JFreeChart chart = ChartFactory.createStackedAreaChart(null, null, "Number of errors", buildDataset(action), PlotOrientation.VERTICAL, true, false, true);

        chart.setBackgroundPaint(Color.white);


        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

        
        CategoryItemRenderer firstRender= new DefaultCategoryItemRenderer();
        CppcheckAreaRenderer renderer = new CppcheckAreaRenderer(action.getUrlName());
        plot.setRenderer(firstRender); 
        

        return chart;
    }

    private static CategoryDataset buildDataset(CppcheckBuildAction lastAction){
        DataSetBuilder<String, NumberOnlyBuildLabel> builder = new DataSetBuilder<String, NumberOnlyBuildLabel>();

        CppcheckBuildAction action = lastAction;
        do{
            CppcheckResult result = action.getResult();
            if(result != null){
                CppcheckReport report = result.getReport();
                NumberOnlyBuildLabel buildLabel = new NumberOnlyBuildLabel(action.getBuild());
                if (report.getErrors() ==null)
                	builder.add(0, "Number of module", buildLabel);
                else
                	builder.add(report.getErrors().size(), "Number of errors", buildLabel);
            }
            action = action.getPreviousAction();
        }while(action != null);

        return builder.build();
    }
    

    
}
