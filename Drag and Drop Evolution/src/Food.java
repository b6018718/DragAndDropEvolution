import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class Food extends EnvironmentObject {
	// Constructor
	Food(PApplet parent, ArrayList <Animal> animals, RectObj env){
		// Call the parent constructor
		super(parent, env);
		radius = 5;
		diameter = radius * 2;
		
		// Find a random position for the object
		int attempts = 20;
		boolean notFound = true;
		int aimX;
		int aimY;
		do {
			aimX = (int) pro.random(env.x, env.topX - radius);
			aimY = (int) pro.random(env.y, env.topY - radius);
			if(animals.size() < 0 || !(collideWithAnimal(animals, aimX, aimY))) {
				notFound = false;
			}
		} while(notFound || attempts <= 0);
		
		// Set the position
		PVector position = new PVector();
		if(notFound) {
			position.x = -1;
			position.y = -1;
		} else {
			position.x = aimX;
			position.y = aimY;
		}
		
		// Set the colour
		colour.r = 0;
		colour.g = 190;
		colour.b = 0;
	}
	
	private boolean collideWithAnimal(ArrayList <Animal> animals, int aimX, int aimY) {
		for (int i = 0; i < animals.size(); i++) {
			// Find the distance between circles
			float dx = animals.get(i).position.x - aimX;
			float dy = animals.get(i).position.y - aimY;
			float distance = PApplet.sqrt(dx * dx + dy * dy);
			float minDist = animals.get(i).radius + radius;
			if(distance < minDist) {
				return true;
			}
		}
		return false;
	}
}
