import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;

public class Environment {
	// Processing applet
	PApplet pro;
	
	// Environment dimensions
	int x, y, width, height;
	// Starting population animal count
	int numOfAnimals = 100;
	// Max size of animals
	float maxObjectSize = 3f;
	// Amount of food
	int foodPerEvent = 0;
	// How long between food generation
	int foodCounter = 10;
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
		//pro.rect(env.x, env.y, env.width, env.height);
		
		for(int i = animals.size()-1; i >= 0; i--) {
			Animal an = animals.get(i);
			an.show();
			if(useHashGrid) {
				// Moving and eating are both contained in the function below for efficiency
				an.moveWithHashGrid(hashGrid, deltaTime, foodArray);
				if(an.checkIfDead(animals, i)) {
					hashGrid.remove(an);
				}
			} else {
				an.move(animals, deltaTime);
				an.eatFood(foodArray);
				an.checkIfDead(animals, i);
			}
		}
		
		// Draw the food onto the environment
		showFood();
		
		// Update counters
		foodCounter += lastLoopTime;
		
		if(useHashGrid)
			hashGrid.updateAll();
	}
	
	void showFood() {
		if(foodCounter > 2000) {
			//System.out.println(timer.getFrameRate());
			// Produce food
			foodCounter = 0;
			for (int i = 0; i < foodPerEvent; i++) {
				foodArray.add(new Food(pro, animals, env));
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
		animals.add(new Animal(pro, animals, maxObjectSize, env));
		if(useHashGrid) {
			hashGrid.add(animals.get(animals.size() -1));
		}
	}
}
