import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Environment {
	// Processing applet
	PApplet pro;
	
	// Environment dimensions
	int x, y, width, height;
	// Starting population animal count
	int numOfAnimals = 10;
	// Max size of animals
	float maxObjectSize = 10f;
	// Amount of food
	int foodPerEvent = 5;
	// Ms per food event
	float msPerFoodEvent = 2000;
	float originalFoodPerEvent = msPerFoodEvent;
	// Counter to track time till next food spawn
	int foodCounter = 0;
	// Use a hash grid
	boolean useHashGrid = true;
	// Hashgrid
	HashGrid<EnvironmentObject> hashGrid;
	// Environment dimensions
	RectObj env;
	// Ui interaction
	UI userInterface;
	
	ArrayList<Animal> animals = new ArrayList<Animal>();
	ArrayList<Food> foodArray = new ArrayList<Food>();
	
	PImage animalImage;
	
	Environment(PApplet processing, RectObj env){
		pro = processing;
		this.env = env;
		
		// Get the max proper max size
		maxObjectSize = calculateObjectSize();
		
		// Create a hash grid
		if(useHashGrid) {
			hashGrid = new HashGrid<EnvironmentObject>(env.topX, env.topY, maxObjectSize * 2 + 1);
		}
		
		animalImage = pro.loadImage("Rabbit.png");
		
		// Create the animals
		for(int i = 0; i < numOfAnimals; i++) {
			addAnimal();
		}
		
	}
	
	void draw(float deltaTime, int lastLoopTime) {
		//pro.stroke(0);
		pro.fill(153, 255, 51, 190);
		pro.noStroke();
		pro.rect(env.x, env.y, env.width, env.height);
		
		for(int i = animals.size()-1; i >= 0; i--) {
			Animal an = animals.get(i);
			an.show();
			an.move(deltaTime);
			an.checkIfDead(lastLoopTime);
		}
		
		// Draw the food onto the environment
		showFood();
		
		// Update counters
		if(msPerFoodEvent != -1)
			foodCounter += lastLoopTime;
		
		if(useHashGrid)
			hashGrid.updateAll();
	}
	
	float calculateObjectSize() {
		return pro.width * 0.01f;
	}
	
	void showSelectedAnimal() {
		for (Animal animal : animals) {
			animal.showSelectedCircle();
		}
	}
	
	
	void showFood() {
		if(foodCounter > msPerFoodEvent && msPerFoodEvent != -1) {
			//System.out.println(timer.getFrameRate());
			// Produce food
			foodCounter = 0;
			for (int i = 0; i < foodPerEvent; i++) {
				foodArray.add(new Food(pro, animals, foodArray, env, hashGrid, null));
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
	
	void addAnimal() {
		animals.add(new Animal(pro, animals, foodArray, maxObjectSize, env, hashGrid, animalImage));
		if(useHashGrid) {
			hashGrid.add(animals.get(animals.size() -1));
		}
	}
	
	void clickOnEnv(PVector mouse) {
		if(withinEnv(mouse)) {
			Animal an = null;
			if(hashGrid != null) {
				an = checkForAnimalCollisions(mouse, null);
			} else {
				an = checkForAnimalCollisions(mouse, animals);
			}
			// Send the selected animal to the user interface
			unselectAllAnimals();
			if(an != null){
				an.isSelected = true;
				userInterface.showAnimalChart(an);
			} else {
				userInterface.barCharts.clear();
			}
		}
	}
	
	private Animal checkForAnimalCollisions(PVector collidePos, ArrayList<? extends EnvironmentObject> arrList) {
		Set<EnvironmentObject> objectsInRange;
		if(hashGrid != null) {
			objectsInRange = hashGrid.get(collidePos);
		} else {
			objectsInRange = new HashSet<EnvironmentObject>(arrList);
		}
		
		for(EnvironmentObject obj : objectsInRange) {
			if(obj instanceof Animal) {
				Animal animal = (Animal)obj;				
				if(collisionAABBWithMouse(animal, collidePos)) {
					return animal;
				}
			}
		}
		return null;
	}
	
	protected boolean collisionAABBWithMouse(EnvironmentObject obj, PVector aimPos) {
		if(obj.position.x <= aimPos.x + obj.width * 2 && obj.position.x + obj.width * 2 >= aimPos.x) { // X Collision
			if(obj.position.y <= aimPos.y + obj.width * 2 && obj.position.y + obj.width * 2 >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
	
	protected boolean isInsideEnv(PVector aimPos) {
		if(env.x <= aimPos.x && env.topX >= aimPos.x) { // X Collision
			if(env.y <= aimPos.y && env.topY >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
	
	public void changeSpeedMultiplierInEnv(double multiplier) {
		if(multiplier == 0) {
			msPerFoodEvent = -1;
		} else {
			msPerFoodEvent = (float) (originalFoodPerEvent / multiplier);
		}
		for (Animal an : animals) {
			an.speedMultiplier = (float) multiplier;
		}
	}
	
	public void setUi(UI ui) {
		userInterface = ui;
	}
	
	public void unselectAllAnimals() {
		for (Animal animal : animals) {
			animal.isSelected = false;
		}
	}
	
	public boolean withinEnv(PVector mouse) {
		return (mouse.x > env.x && mouse.x < env.topX && mouse.y > env.y & mouse.y < env.topY);
	}
}
