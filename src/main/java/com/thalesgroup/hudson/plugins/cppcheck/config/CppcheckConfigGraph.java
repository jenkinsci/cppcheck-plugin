package com.thalesgroup.hudson.plugins.cppcheck.config;

import java.io.Serializable;

import com.thalesgroup.hudson.plugins.cppcheck.graph.CppcheckGraph;

public class CppcheckConfigGraph implements Serializable{

	private static final long serialVersionUID = 1L;

	private int xSize = CppcheckGraph.DEFAULT_CHART_WIDTH;
	
	private int ySize = CppcheckGraph.DEFAULT_CHART_HEIGHT;
	
	private boolean diplayAllError = true;
	
	private boolean displaySeverityError = true;
	
	private boolean displaySeverityPossibleError = true;
	
	private boolean displaySeverityStyle = true;
	
	private boolean displaySeverityPossibleStyle = true;

	
	public CppcheckConfigGraph(){}
	
	public CppcheckConfigGraph(int xSize, int ySize, boolean diplayAllError,
			boolean displaySeverityError, boolean displaySeverityPossibleError,
			boolean displaySeverityStyle, boolean displaySeverityPossibleStyle) {
		super();
		this.xSize = xSize;
		this.ySize = ySize;
		this.diplayAllError = diplayAllError;
		this.displaySeverityError = displaySeverityError;
		this.displaySeverityPossibleError = displaySeverityPossibleError;
		this.displaySeverityStyle = displaySeverityStyle;
		this.displaySeverityPossibleStyle = displaySeverityPossibleStyle;
	}

	public int getXSize() {
		return xSize;
	}

	public void setXSize(int size) {
		xSize = size;
	}

	public int getYSize() {
		return ySize;
	}

	public void setYSize(int size) {
		ySize = size;
	}

	public boolean isDiplayAllError() {
		return diplayAllError;
	}

	public void setDiplayAllError(boolean diplayAllError) {
		this.diplayAllError = diplayAllError;
	}

	public boolean isDisplaySeverityError() {
		return displaySeverityError;
	}

	public void setDisplaySeverityError(boolean displaySeverityError) {
		this.displaySeverityError = displaySeverityError;
	}

	public boolean isDisplaySeverityPossibleError() {
		return displaySeverityPossibleError;
	}

	public void setDisplaySeverityPossibleError(boolean displaySeverityPossibleError) {
		this.displaySeverityPossibleError = displaySeverityPossibleError;
	}

	public boolean isDisplaySeverityStyle() {
		return displaySeverityStyle;
	}

	public void setDisplaySeverityStyle(boolean displaySeverityStyle) {
		this.displaySeverityStyle = displaySeverityStyle;
	}

	public boolean isDisplaySeverityPossibleStyle() {
		return displaySeverityPossibleStyle;
	}

	public void setDisplaySeverityPossibleStyle(boolean displaySeverityPossibleStyle) {
		this.displaySeverityPossibleStyle = displaySeverityPossibleStyle;
	}		
}
