import processing.core.PApplet;
import processing.core.PVector;

import org.gicentre.utils.FrameTimer;
import org.gicentre.utils.stat.XYChart;

public class Main extends PApplet {
	String versionNumber = "Alpha 1.3";
	
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

	// Graph features
	float secondCount = 0;
	XYChart lineChart;

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
	}
	
	public void setup() {
		// Set the window name
		surface.setTitle(versionNumber);
		// Create the frame rate timer, reports every 60 frames
		timer = new FrameTimer(0, 1);
		
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
		millisCount += lastLoopTime * env.speedMultiplier;
		if(millisCount > 2000) {
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
		
		addDataToCharts();
		userInterface.display();
		// Draw Text
		String numOfAnimalsString = "Number of animals: " + env.animals.size();
		textSize(scWidth/30);
		fill(0,0,0);
		text(numOfAnimalsString, (float) (scWidth * 0.05), (float) (scHeight * 0.08));
		
		// Draw framerate
		showFPS();
		
		lastMillis = currentMillis;
		secondPassedFrame = false;
	}
	
	public void keyPressed() {
		if (key == 'f' || key == 'F')
			userInterface.fpsLineChart.display = !userInterface.fpsLineChart.display;
		else if (key == 'c' || key == 'C')
			env.reset();
		else if(key == 'b' || key == 'B') {
			userInterface.animalPopulation.display = !userInterface.animalPopulation.display;
			userInterface.birthRate.display = !userInterface.birthRate.display;
		}
			
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
			
			env.clickOnEnv(mouseZoomed, mouseUi);
			userInterface.checkCollisions(mouseUi);
		}
	}
	
	public void showFPS() {
		fill(0, 160);
		if (fps.length()>0){
			textSize(scWidth/30);
			fill(0, 0, 0);
		    text(fps+" fps", (float) (scWidth * 0.8), (float) (scHeight * 0.08));
		}
	}
	
	public void addDataToCharts() {
		// Add to the chart array
		if(secondPassedFrame) {
			// FPS Chart
			userInterface.fpsLineChart.addData(secondCount, timer.getFrameRate());
			fps = timer.getFrameRateAsText();
			// Animal population
			userInterface.animalPopulation.addData(secondCount, env.animals.size());
			userInterface.birthRate.addData(secondCount, calculateAverageBirthRateInSeconds());
		}
	}
	
	public float calculateAverageBirthRateInSeconds() {
		float totalLifeSpan = 0;
		for (Animal an : env.animals) {
			totalLifeSpan += an.startingLifeSpan;
		}
		float returnVal = totalLifeSpan / (env.animals.size() * 1000);
		return returnVal;
	}
}
