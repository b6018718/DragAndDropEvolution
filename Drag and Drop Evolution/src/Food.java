import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PVector;

public class Food extends EnvironmentObject {
	// Constructor
	Food(PApplet parent, ArrayList <Animal> animals, ArrayList <Food> foodArray, RectObj env, HashGrid<EnvironmentObject> hashGrid, PVector startingLoc){
		// Call the parent constructor
		super(parent, env, animals, foodArray, hashGrid, null);
		width = pro.width * 0.005f;
		
		// Find a random position for the object
		if(startingLoc == null) {
			findAStartingLocation();
		} else {
			position = startingLoc;
		}
		
		// Set the colour
		colour.r = 0;
		colour.g = 190;
		colour.b = 0;
	}
	
	private void findAStartingLocation() {
		int attempts = 20;
		boolean notFound = true;
		PVector aimPos = new PVector();
		do {
			aimPos.x = (int) pro.random(env.x, env.topX - width);
			aimPos.y = (int) pro.random(env.y, env.topY - width);
			if(animals.size() < 0 || !(collideWithAnimal(aimPos))) {
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
		for (int i = 0; i < animals.size(); i++) {
			// Check if squares collide using AABB collision method
			Animal an = animals.get(i);
			if(collisionAABB(an, aimPos)) {
				return true;
			}
		}
		return false;
	}
}
