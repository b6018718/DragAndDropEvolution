import processing.core.PApplet;
import java.util.*;
import org.gicentre.utils.geom.*;
import org.gicentre.utils.FrameTimer;

public class Main extends PApplet {
	String versionNumber = "Alpha 0.3";
	
	FrameTimer timer;
	// Animation frames
	int lastMillis = millis();
	// Food timer starting point (ms)
	int secondCounter = 2000;
	// Starting population animal count
	int numOfAnimals = 250;
	// Delta upper and lower limits
	float LOW_LIMIT = 0.0167f; 	// 10 fps
	float HIGH_LIMIT = 0.1f; 	// 60 fps
	// Boolean variables
	boolean gameFullScreen = false;
	boolean useHashGrid = false;
	// Hashgrid
	HashGrid<EnvironmentObject> hashGrid;
	float maxObjectSize = 15f;
	int foodPerEvent = 0;

	public static void main(String[] args) {
		PApplet.main( new String[] { "Main" } );
	}
	
	ArrayList<Animal> animals = new ArrayList<Animal>();
	ArrayList<Food> foodArray = new ArrayList<Food>();
	
	public void settings() {
		int scWidth;
		int scHeight;
		if(gameFullScreen) {
			fullScreen();
			scWidth = displayWidth;
			scHeight = displayHeight;
		} else {
			scWidth = 640;
			scHeight = 480;
			size(scWidth, scHeight);
		}

		width = scWidth;
		height = scHeight;
	}
	
	public void setup() {
		// Create a hash grid
		if(useHashGrid) {
			hashGrid = new HashGrid<EnvironmentObject>(width, height, maxObjectSize * 2);
		}
		// Set the window name
		surface.setTitle(versionNumber);
		// Create the frame rate timer, reports every 60 frames
		timer = new FrameTimer(0, 1);
		// Create the animals
		for(int i = 0; i < numOfAnimals; i++) {
			animals.add(new Animal(this, i, animals, maxObjectSize));
			if(useHashGrid) {
				hashGrid.add(animals.get(animals.size() -1));
			}
		}
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
		
		// Drawing functions
		drawBackground();
		
		for(int i = animals.size()-1; i >= 0; i--) {
			Animal an = animals.get(i);
			an.show();
			if(useHashGrid) {
				an.moveWithHashGrid(hashGrid, deltaTime);
				an.eatFoodWithHashGrid(hashGrid, foodArray);
				if(an.checkIfDead(animals, i)) {
					hashGrid.remove(an);
				}
			} else {
				an.move(animals, deltaTime);
				an.eatFood(foodArray);
				an.checkIfDead(animals, i);
			}
		}
		
		showFood();
		showFPS();
		
		// Update counters
		secondCounter += lastLoopTime;
		lastMillis = currentMillis;
		if(useHashGrid)
			hashGrid.updateAll();
	}
	
	public void showFPS() {
		fill(0, 160);
		String fps = timer.getFrameRateAsText();
		if (fps.length()>0){
			textSize(18);
		    text(fps+" fps", (float) (width * 0.8), (float) (height * 0.05));
		}
	}
	
	void showFood() {
		if(secondCounter > 2000) {
			//System.out.println(timer.getFrameRate());
			// Produce food
			secondCounter = 0;
			for (int i = 0; i < foodPerEvent; i++) {
				foodArray.add(new Food(this, animals));
				if(useHashGrid) {
					hashGrid.add(foodArray.get(foodArray.size() - 1));
				}
			}
		}
		
		for (int i = 0; i < foodArray.size(); i++) {
			Food food = foodArray.get(i);
			food.show();
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
