import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
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
	float msPerFoodEvent = 4000;
	// Counter to track time till next food spawn
	int foodCounter = 0;
	// Use a hash grid
	boolean useHashGrid = true;
	// Hashgrid
	HashGrid<EnvironmentObject> hashGrid;
	// Environment dimensions
	RectObj env;
	
	ArrayList<Animal> animals = new ArrayList<Animal>();
	ArrayList<Food> foodArray = new ArrayList<Food>();
	
	Environment(PApplet processing, RectObj env){
		pro = processing;
		this.env = env;
		
		// Create a hash grid
		if(useHashGrid) {
			hashGrid = new HashGrid<EnvironmentObject>(env.topX, env.topY, maxObjectSize * 2 + 1);
		}
		
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
		foodCounter += lastLoopTime;
		
		if(useHashGrid)
			hashGrid.updateAll();
	}
	
	void showFood() {
		if(foodCounter > msPerFoodEvent) {
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
		animals.add(new Animal(pro, animals, foodArray, maxObjectSize, env, hashGrid));
		if(useHashGrid) {
			hashGrid.add(animals.get(animals.size() -1));
		}
	}
	
	void clickOnEnv(PVector mouse) {
		if(hashGrid != null) {
			System.out.println(checkForCollisions(mouse, null));
		} else {
			System.out.println(checkForCollisions(mouse, animals));
		}
	}
	
	private boolean checkForCollisions(PVector collidePos, ArrayList<? extends EnvironmentObject> arrList) {
		Set<EnvironmentObject> objectsInRange;
		if(hashGrid != null) {
			objectsInRange = hashGrid.get(collidePos);
		} else {
			objectsInRange = new HashSet<EnvironmentObject>(arrList);
		}
		
		for(EnvironmentObject obj : objectsInRange) {
			if(obj instanceof Animal) {
				Animal animal = (Animal)obj;				
				if(collisionAABB(animal, collidePos)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean collisionAABB(EnvironmentObject obj, PVector aimPos) {
		if(obj.position.x <= aimPos.x + width && obj.position.x + obj.width >= aimPos.x) { // X Collision
			if(obj.position.y <= aimPos.y + width && obj.position.y + obj.width >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
	
	public void speedUpDouble() {
		msPerFoodEvent = msPerFoodEvent / 2;
		for (Animal an : animals) {
			an.speedMultiplier = 2;
		}
	}
}
