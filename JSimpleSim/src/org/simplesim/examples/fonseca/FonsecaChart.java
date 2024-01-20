/*
 * SimNation is a multi-agent model to simulate economic systems. It is scalable
 * and used JSimpleSim as technical backbone for concurrent discrete event simulation.
 * 
 * This software is published as open source and licensed under GNU GPLv3.
 * 
 * Contributors:
 * 	- Rene Kuhlemann - development and initial implementation
 * 
 */
package org.simplesim.examples.fonseca;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * Class to display chart, pareto front and xy-series of algorithm
 */
public class FonsecaChart {

	private final static int VISIBLE_DOTS=20;
	
	private final XYChart chart;
	private final Deque<Double> f1=new ArrayDeque<>();
	private final Deque<Double> f2=new ArrayDeque<>();

	private final SwingWrapper<XYChart> sw;

	public FonsecaChart(String title) {
		chart=new XYChartBuilder().width(800).height(600).title(title).theme(ChartTheme.Matlab).build();

		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
		chart.getStyler().setChartTitleVisible(false);
		chart.getStyler().setLegendVisible(false);
		chart.getStyler().setAxisTitlesVisible(true);
		chart.getStyler().setXAxisDecimalPattern("0.0");
		chart.setXAxisTitle("f1(x)");
		chart.setYAxisTitle("f2(x)");

		XYSeries series=chart.addSeries("Algorithm",new double[1],new double[1]);
		series.setMarkerColor(Color.BLUE);
		series.setMarker(SeriesMarkers.CIRCLE);

		sw=new SwingWrapper<>(chart);
		sw.displayChart();
	}
	
	public void addParetoFront(double[] f1pareto, double[] f2pareto) {
		// draw the pareto-optimal front
		final XYSeries series=chart.addSeries("Pareto front",f1pareto,f2pareto);
		series.setMarkerColor(Color.RED);
		series.setMarker(SeriesMarkers.DIAMOND);
	}

	public void update(double x1, double x2) {
		f1.addFirst(x1);
		while (f1.size()>VISIBLE_DOTS) f1.removeLast();
		f2.addFirst(x2);
		while (f2.size()>VISIBLE_DOTS) f2.removeLast();
		chart.updateXYSeries("Algorithm",new ArrayList<>(f1),new ArrayList<>(f2),null);
		sw.repaintChart();
	}

}