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
	String graphName;
	
	public UiLineChart(PApplet pro, Environment env, RectObj sizeRect, float startingY, boolean resize, String graphName){
		this.graph = new XYChart(pro);
		this.pro = pro;
		this.env = env;
		this.sizeRect = sizeRect;
		this.maximumY = startingY;
		this.resize = resize;
		this.graphName = graphName;
		
		//graph.setData(dataPoints);
		
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
			//if(graph.getLeftSpacing() > 50)
				//graph.draw(sizeRect.x - (graph.getLeftSpacing() * 0.8f), sizeRect.y + (graph.getBottomSpacing() * 0.9f),
					//sizeRect.width, sizeRect.height);
			//else
				//graph.setXAxisLabel(null);
				//graph.setYAxisLabel(null);
				//System.out.println(graph.getLeftSpacing());
				//graph.calcDataSpacing();
				graph.updateLayout();

				graph.draw(sizeRect.x, sizeRect.y,
						sizeRect.width, sizeRect.height);
		}
	}	
	
}
