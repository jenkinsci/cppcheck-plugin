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
package com.thalesgroup.hudson.plugins.cppcheck.graph;

import hudson.model.Run;
import hudson.util.ColorPalette;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CppcheckGraph extends Graph {
    private final String yLabel;

    private final CategoryDataset categoryDataset;

    public static final int DEFAULT_CHART_WIDTH = 500;
    public static final int DEFAULT_CHART_HEIGHT = 200;

    private static final Color RED = new Color(0xCC0000);
    private static final Color BLUE = new Color(0x3465A4);
    private static final Color GREEN = new Color(0x73D216);
    private static final Color YELLOW = new Color(0xEDD400);
    private static final Color BROWN = new Color(0xB87700);
    private static final Color GRAY = new Color(0x4D4D4D);
    private static final Color VIOLET = new Color(0xA020F0);
    private static final Color PINK = new Color(0xFF9780);

    /** Color palette for the lines in the graph. */
    private static final List<Color> colors = Collections.unmodifiableList(
            Arrays.asList(RED, BLUE, GREEN, YELLOW, BROWN, GRAY, VIOLET, PINK));

    public CppcheckGraph(Run<?, ?> owner, CategoryDataset categoryDataset,
            String yLabel, int chartWidth, int chartHeight) {
        super(owner.getTimestamp(), chartWidth, chartHeight);
        this.yLabel = yLabel;
        this.categoryDataset = categoryDataset;
    }

    /**
     * Creates a Cppcheck trend graph
     *
     * @return the JFreeChart graph object
     */
    protected JFreeChart createGraph() {

        final JFreeChart chart = ChartFactory.createLineChart(
                null,                     // chart title
                null,                     // unused
                yLabel,                   // range axis label
                categoryDataset,          // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setLowerBound(0);
        rangeAxis.setAutoRange(true);

        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseStroke(new BasicStroke(2.0f));
        applyColorPalette(renderer);

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));

        return chart;
    }

    /**
     * Apply color palette to the lines in the graph.
     * 
     * @param renderer the graph renderer
     * @see ColorPalette#apply(LineAndShapeRenderer)
     */
    private void applyColorPalette(LineAndShapeRenderer renderer) {
        int n = 0;

        for (Color c : colors) {
            renderer.setSeriesPaint(n++, c);
        }
    }
}
