import java.util.ArrayList;

import org.gicentre.utils.stat.XYChart;

import processing.core.PApplet;
import processing.core.PVector;

public class UiLineChart {
	PApplet pro;
	XYChart graph;
	Environment env;
	ArrayList<PVector> dataPoints = new ArrayList<PVector>();
	RectObj sizeRect;
	boolean display = true;
	boolean resize = true;
	float maximumY;
	
	public UiLineChart(PApplet pro, Environment env, RectObj sizeRect, float startingY, boolean resize){
		this.graph = new XYChart(pro);
		this.pro = pro;
		this.env = env;
		this.sizeRect = sizeRect;
		this.maximumY = startingY;
		this.resize = resize;
		
		graph.setData(dataPoints);
		graph.showYAxis(true);
		graph.showXAxis(true);
		graph.setAxisValuesColour(pro.color(0,0,0,0));
		graph.setAxisColour(pro.color(0,0,0,0));
		graph.setPointColour(pro.color(0,0,0,0));
		graph.setPointSize(0);
		graph.setLineColour(pro.color(255, 0, 0));
		graph.setMaxY(startingY);
		graph.setMinY(0);
		graph.setLineWidth(2);
	}

	void addData(float secondCount, float data){
		// Resize the chart if the data is too large
		if(data > maximumY && resize) {
			graph.setMaxY(data);
			maximumY = data;
		}
		// Add the data into the chart
		PVector dataNode = new PVector(secondCount, data);
		dataPoints.add(dataNode);
		graph.setData(dataPoints);
	}
	
	void show() {
		if(display) {
			pro.textSize(pro.width/90);
			graph.draw(sizeRect.x, sizeRect.y, sizeRect.width, sizeRect.height);
		}
	}	
	
}
