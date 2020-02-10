import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PVector;

public class Food extends EnvironmentObject {
	// Constructor
	Food(PApplet parent, ArrayList <Species> species, ArrayList <Food> foodArray, Environment env, HashGrid<EnvironmentObject> hashGrid, PVector startingLoc){
		// Call the parent constructor
		super(parent, env, species, foodArray, hashGrid, null);
		width = pro.width * 0.005f;
		
		// Find a random position for the object
		if(startingLoc == null) {
			findAStartingLocation();
		} else {
			position = startingLoc;
		}
		setHashPos();

		
		// Set the colour
		colour.r = 0;
		colour.g = 190;
		colour.b = 0;
	}
	
	private void findAStartingLocation() {
		int attempts = 20;
		boolean notFound = true;
		PVector aimPos = new PVector();
		
		int numberOfAnimals = 0;
		for (int i = 0; i < species.size(); i++) {
			numberOfAnimals += species.get(i).animals.size();
		}
		
		do {
			aimPos.x = (int) pro.random(env.env.x, env.env.topX - width);
			aimPos.y = (int) pro.random(env.env.y, env.env.topY - width);
			if( numberOfAnimals < 0 || !(collideWithAnimal(aimPos))) {
				notFound = false;
			}
		} while(notFound || attempts <= 0);
		
		// Set the position
		if(notFound) {
			position.x = -1;
			position.y = -1;
		} else {
			position.x = aimPos.x;
			position.y = aimPos.y;
		}
	}
	
	private boolean collideWithAnimal(PVector aimPos) {
		for (int j = 0; j < species.size(); j++) {
			ArrayList<Animal> animals = species.get(j).animals;
			for (int i = 0; i < animals.size(); i++) {
				// Check if squares collide using AABB collision method
				Animal an = animals.get(i);
				if(collisionAABB(an, aimPos)) {
					return true;
				}
			}
		}
		return false;
	}
}
