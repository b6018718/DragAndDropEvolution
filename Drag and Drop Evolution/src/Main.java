import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import org.gicentre.utils.FrameTimer;
import org.gicentre.utils.stat.XYChart;

import g4p_controls.GButton;
import g4p_controls.GDropList;
import g4p_controls.GEvent;
import g4p_controls.GImageButton;
import g4p_controls.GToggleControl;

public class Main extends PApplet {
	String versionNumber = "Alpha 1.8";
	
	// Screen dimensions
	int scWidth;
	int scHeight;
	
	FrameTimer timer;
	// Animation frames
	int lastMillis = millis();
	// Is the first frame when a second has passed in time
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
	ImageManager imageManager;

	// Graph features
	int saveDataPerSecond = 5;
	int saveDataPerMs = saveDataPerSecond * 1000;
	float secondCount = -saveDataPerSecond;
	int millisCount = saveDataPerMs - 2500;
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
		imageManager = new ImageManager(this);
		env = new Environment(this, envArea, imageManager);
		// Load images
		userInterface = new UI(this, env, offset, imageManager);
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
		millisCount += lastLoopTime;
		if(millisCount > saveDataPerMs) {
			secondPassedFrame = true;
			millisCount = 0;
			secondCount = secondCount + saveDataPerSecond;
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
		if(userInterface.selectedSpecies != null) {
			String numOfAnimalsString = "Number of " + userInterface.selectedSpecies.name +": " + userInterface.selectedSpecies.animals.size();
			textSize(scWidth/70);
			fill(0,0,0);
			text(numOfAnimalsString, (float) (scWidth * 0.1), (float) (scHeight * 0.15));
		}
		
		// Draw framerate
		showFPS();
		//saveFrame("C:\\Users\\antho\\Desktop\\Evolution_Video\\img####.tif");
		lastMillis = currentMillis;
		secondPassedFrame = false;
	}
	
	public void keyPressed() {
		if(key == 'l' || key == 'L') {
			env.showAnimalLines();
		} else if(key == 'w' || key == 'W') {
			userInterface.beginDrawingWalls();
		} else if(key == 's' || key == 'S') {
			userInterface.beginDrawingSea();
		}
	}
	
	public void mousePressed() {
		if(mouseButton == LEFT) {
			// Loop through the environments looking for creatures
			PVector mouseZoomed = userInterface.zoomer.getMouseCoord();
			PVector mouseUi = new PVector(mouseX, mouseY);
			
			/*if(env.isInsideEnv(mouseUi)) {
				userInterface.zoomer.setMouseMask(0);
			} else {
				userInterface.zoomer.setMouseMask(-1);
			}*/
			
			env.clickOnEnv(mouseZoomed, mouseUi);
			userInterface.checkCollisions(mouseUi);
			
			userInterface.drawWallsPressed();
			userInterface.drawSeaPressed();
		}
		
		if(mouseButton == RIGHT) {
			userInterface.resetWalls();
			userInterface.resetSeaDrawing();
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
			for (Species species : env.speciesArray) {
				ArrayList<Animal> animals = species.animals;
				// FPS Chart
				species.fpsLineChart.addData(secondCount, timer.getFrameRate());
				fps = timer.getFrameRateAsText();
				// Animal population
				species.animalPopulation.addData(secondCount, animals.size());
				species.birthRate.addData(secondCount, calculateAverageBirthRateInSeconds(animals));
				species.sizeChart.addData(secondCount, calculateAverageSize(animals));
				species.speedChart.addData(secondCount, calculateAverageSpeed(animals));
				species.foodChart.addData(secondCount, env.foodArray.size());
				species.hungerChart.addData(secondCount, calculateAverageHunger(animals));
			}
		}
	}
	
	
	public float calculateAverageBirthRateInSeconds(ArrayList<Animal> animals) {
		if(animals.size() == 0) {
			return 0;
		}
			
		float totalLifeSpan = 0;
		for (Animal an : animals) {
			totalLifeSpan += an.gene.lifeSpan;
		}
		float returnVal = totalLifeSpan / (animals.size() * 1000);
		return returnVal;
	}
	
	public float calculateAverageHunger(ArrayList<Animal> animals) {
		if(animals.size() == 0) {
			return 0;
		}
		
		float totalHunger = 0;
		for (Animal an : animals) {
			totalHunger += an.normaliseHunger();
		}
		float returnVal = totalHunger / animals.size();
		return 1 - returnVal;
		
	}
	
	public float calculateAverageSize(ArrayList<Animal> animals) {
		if(animals.size() == 0) {
			return 0;
		}
		
		float totalSize = 0;
		for (Animal an : animals) {
			totalSize += an.width;
		}
		float returnVal = totalSize / animals.size();
		return returnVal;
	}
	
	public float calculateAverageSpeed(ArrayList<Animal> animals) {
		if(animals.size() == 0) {
			return 0;
		}
		
		float totalSpeed = 0;
		for (Animal an : animals) {
			totalSpeed += an.gene.speed;
		}
		float returnVal = totalSpeed / animals.size();
		return returnVal;
	}
	
	public void handleDropListEvents(GDropList list, GEvent event) {
		if(userInterface.selectedSpecies != null)
			userInterface.selectedSpecies.handleDropListEvents(list, event);
	}
	
	public void handleButtonEvents(GButton button, GEvent event) {
		userInterface.handleButtonEvents(button, event);
	}
	
	public void handleToggleControlEvents(GToggleControl checkBox, GEvent event) {
		userInterface.handleToggleControlEvents(checkBox, event);
	}
	
	public void handleButtonEvents(GImageButton button, GEvent event) {
		userInterface.handleButtonEvents(button, event);
	}
	
	public void getAnimalImage() {
		userInterface.getAnimalImage();
	}
	
	public void saveTableThread() {
		userInterface.saveTableThread();
	}
}
