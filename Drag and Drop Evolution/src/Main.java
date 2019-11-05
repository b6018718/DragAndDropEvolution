import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import org.gicentre.utils.FrameTimer;
import org.gicentre.utils.stat.XYChart;

public class Main extends PApplet {
	String versionNumber = "Alpha 0.4";
	
	// Screen dimensions
	int scWidth;
	int scHeight;
	
	FrameTimer timer;
	// Animation frames
	int lastMillis = millis();
	int millisCount = 0;
	boolean secondPassedFrame = false;
	String fps = "";
	// Delta upper and lower limits
	float LOW_LIMIT = 0.0167f; 	// 10 fps
	float HIGH_LIMIT = 0.1f; 	// 60 fps
	// Boolean variables
	boolean gameFullScreen = false;
	// Environment
	Environment env;
	// Graph
	XYChart lineChart;
	ArrayList <PVector> fpsArray = new ArrayList<PVector>();
	float secondCount = 0;
	RectObj chartRect;

	public static void main(String[] args) {
		PApplet.main( new String[] { "Main" } );
	}
	
	public void settings() {
		if(gameFullScreen) {
			fullScreen();
			scWidth = displayWidth;
			scHeight = displayHeight;
		} else {
			scWidth = 640;
			scHeight = 480;
			size(scWidth, scHeight);
		}
		
		// 0.2 is 20% offset
		double offset = 0.2;
		
		int envX = (int) (scWidth * 0);
		int envY = (int) (scHeight * offset);
		int envHeight = (int) (scHeight * (1 - offset));
		int envWidth = (int) (scWidth * (1 - offset));
		
		RectObj envArea = new RectObj(envX, envY, envWidth, envHeight);
		env = new Environment(this, envArea);
	}
	
	public void setup() {
		// Set the window name
		surface.setTitle(versionNumber);
		// Create the frame rate timer, reports every 60 frames
		timer = new FrameTimer(0, 1);
		// Create the chart
		lineChart = new XYChart(this);
		lineChart.showYAxis(true);
		lineChart.setAxisValuesColour(color(0,0,0,0));
		lineChart.setAxisColour(color(0,0,0,0));
		lineChart.setPointColour(color(0,0,0,0));
		lineChart.setPointSize(0);
		lineChart.setLineColour(color(255, 0, 0));
		lineChart.setMaxY(60);
		lineChart.setMinY(0);
		lineChart.setLineWidth(2);
		//lineChart
		float chartX = (float) (scWidth * 0.8);
		float chartY = (float) (scHeight * 0.2);
		float chartWidth = (float) (scWidth * 0.2);
		float chartHeight = (float) (scHeight * 0.7);
		chartRect = new RectObj(chartX, chartY, chartWidth, chartHeight);
		
		
		frameRate(60);
		drawBackground();
	}
	
	public void drawBackground() {
		setGradient(0, 0, width, height, color(160, 160, 160), color(10, 50, 255), Y_AXIS);
	}
	
	public float getDeltaTime(int lastLoopTime) {
		float deltaTime = (lastLoopTime) / 1000f;
		if(deltaTime < LOW_LIMIT)
			deltaTime = LOW_LIMIT;
		else if(deltaTime > HIGH_LIMIT)
			deltaTime = HIGH_LIMIT;
		return deltaTime;
	}
	
	
	public void draw() {
		int currentMillis = millis();
		timer.update();
		int lastLoopTime = currentMillis - lastMillis;
		float deltaTime = getDeltaTime(lastLoopTime);
		millisCount += lastLoopTime;
		if(millisCount > 1000) {
			secondPassedFrame = true;
			millisCount = 0;
			secondCount++;
		}
		
		// Drawing functions
		drawBackground();
		env.draw(deltaTime, lastLoopTime);
		
		// Draw Text
		String numOfAnimalsString = "Number of objects: " + env.animals.size();
		textSize(scWidth/30);
		fill(0,0,0);
		text(numOfAnimalsString, (float) (scWidth * 0.05), (float) (scHeight * 0.08));
		
		// Draw framerate
		showFPS();
		
		// Show charts
		showCharts();
		
		lastMillis = currentMillis;
		secondPassedFrame = false;
	}
	
	public void showCharts() {
		fill(255);
		rect(chartRect.x, chartRect.y, chartRect.width, chartRect.height + 10);
		textSize(10);
		lineChart.draw(chartRect.x, chartRect.y, chartRect.width, chartRect.height);
	}
	
	public void showFPS() {
		fill(0, 160);
		if (fps.length()>0){
			textSize(scWidth/30);
			fill(0, 0, 0);
		    text(fps+" fps", (float) (scWidth * 0.8), (float) (scHeight * 0.08));
		}
		// Add to the chart array
		if(secondPassedFrame) {
			PVector graphNode = new PVector(secondCount, timer.getFrameRate());
			fpsArray.add(graphNode);
			lineChart.setData(fpsArray);
			env.addAnimal();
			fps = timer.getFrameRateAsText();
		}
	}
	
	// Constants
	int Y_AXIS = 1;
	int X_AXIS = 2;
	void setGradient(int x, int y, float w, float h, int c1, int c2, int axis ) {
		  noFill();
		  if (axis == Y_AXIS) {  // Top to bottom gradient
		    for (int i = y; i <= y+h; i++) {
		      float inter = map(i, y, y+h, 0, 1);
		      int c = lerpColor(c1, c2, inter);
		      stroke(c);
		      line(x, i, x+w, i);
		    }
		  }  
		  else if (axis == X_AXIS) {  // Left to right gradient
		    for (int i = x; i <= x+w; i++) {
		      float inter = map(i, x, x+w, 0, 1);
		      int c = lerpColor(c1, c2, inter);
		      stroke(c);
		      line(i, y, i, y+h);
		      } 
		  }
	}

}
