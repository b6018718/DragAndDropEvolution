import java.util.ArrayList;

import processing.core.PApplet;

public class Food extends EnvironmentObject {
	int radius;
	int diameter;
	PApplet pro;
	RectObj env;
	
	Food(PApplet parent, ArrayList <Animal> animals, RectObj env){
		radius = 5;
		diameter = radius * 2;
		pro = parent;
		this.env = env;
		
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
		
		if(notFound) {
			super.position.x = -1;
			super.position.y = -1;
		} else {
			super.position.x = aimX;
			super.position.y = aimY;
		}
		
		
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
	
	public void show() {
		pro.fill(0, 190, 10);
		pro.stroke(0);
		pro.circle(super.position.x, super.position.y, diameter);
	}
}
