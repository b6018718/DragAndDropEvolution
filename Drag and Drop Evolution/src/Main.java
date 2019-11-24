import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import org.gicentre.utils.FrameTimer;
import org.gicentre.utils.stat.XYChart;

public class Main extends PApplet {
	String versionNumber = "Alpha 1.2";
	
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
	boolean gameFullScreen = true;
	boolean showFPSGraph = false;
	// Environment
	Environment env;
	UI userInterface;

	// Graph
	XYChart fpsChart;
	ArrayList <PVector> fpsArray = new ArrayList<PVector>();
	float secondCount = 0;
	RectObj fpsRect;

	public static void main(String[] args) {
		PApplet.main( new String[] { "Main" } );
	}
	
	public void settings() {
		if(gameFullScreen) {
			fullScreen(JAVA2D);
			scWidth = displayWidth;
			scHeight = displayHeight;
		} else {
			scWidth = 640;
			scHeight = 480;
			size(scWidth, scHeight, JAVA2D);
		}
		
		width = scWidth;
		height = scHeight;
		
		// 0.2 is 20% offset
		double offset = 0.2;
		
		int envX = (int) (scWidth * 0);
		int envY = (int) (scHeight * offset);
		int envHeight = (int) (scHeight * (1 - offset));
		int envWidth = (int) (scWidth * (1 - offset));
		
		RectObj envArea = new RectObj(envX, envY, envWidth, envHeight);
		env = new Environment(this, envArea);
		
		// Load images
		userInterface = new UI(this, env, offset);
		env.setUi(userInterface);
		
	}
	
	public void setup() {
		// Set the window name
		surface.setTitle(versionNumber);
		// Create the frame rate timer, reports every 60 frames
		timer = new FrameTimer(0, 1);
		// Create the chart
		fpsChart = new XYChart(this);
		fpsChart.showYAxis(true);
		fpsChart.showXAxis(true);
		fpsChart.setAxisValuesColour(color(0,0,0,0));
		fpsChart.setAxisColour(color(0,0,0,0));
		fpsChart.setPointColour(color(0,0,0,0));
		fpsChart.setPointSize(0);
		fpsChart.setLineColour(color(255, 0, 0));
		fpsChart.setMaxY(60);
		fpsChart.setMinY(0);
		fpsChart.setLineWidth(2);
		//lineChart
		float chartX = (float) (scWidth * 0.8);
		float chartY = (float) (scHeight * 0.2);
		float chartWidth = (float) (scWidth * 0.2);
		float chartHeight = (float) (scHeight * 0.7);
		fpsRect = new RectObj(chartX, chartY, chartWidth, chartHeight);
		
		frameRate(60);
		drawBackground();
	}
	
	public void drawBackground() {
		//setGradient(0, 0, width, height, color(160, 160, 160), color(10, 50, 255), Y_AXIS);
		background(0, 117, 199);
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
		
		// Sets the boundaries for the panning so that it doesn't allow the screen to go outside the play area
		userInterface.correctPanOffset();
		
		// Drawing functions
		drawBackground();
		userInterface.zoomStart();
		env.draw(deltaTime, lastLoopTime);
		env.showSelectedAnimal();
		userInterface.zoomEnd();
		
		userInterface.display();
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
	
	public void keyPressed() {
		if (key == 'f' || key == 'F')
			showFPSGraph = !showFPSGraph;
	}
	
	public void mousePressed() {
		if(mouseButton == LEFT) {
			// Loop through the environments looking for creatures
			PVector mouseZoomed = userInterface.zoomer.getMouseCoord();
			PVector mouseUi = new PVector(mouseX, mouseY);
			
			if(env.isInsideEnv(mouseUi)) {
				userInterface.zoomer.setMouseMask(0);
			} else {
				userInterface.zoomer.setMouseMask(-1);
			}
			
			env.clickOnEnv(mouseZoomed);
			userInterface.checkCollisions(mouseUi);
		}
	}
	
	public void showCharts() {
		//noStroke
		//fill(255, 255, 255, 50);
		//rect(chartRect.x, chartRect.y, chartRect.width, chartRect.height + 10);
		if(showFPSGraph) {
			textSize(10);
			fpsChart.draw(fpsRect.x, fpsRect.y, fpsRect.width, fpsRect.height);
		}
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
			fpsChart.setData(fpsArray);
			//env.addAnimal();
			fps = timer.getFrameRateAsText();
		}
	}
}
