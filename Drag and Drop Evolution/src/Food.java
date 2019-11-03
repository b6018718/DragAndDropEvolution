import java.util.ArrayList;

import processing.core.PApplet;

public class Food {
	int x;
	int y;
	int radius;
	int diameter;
	PApplet pro;
	
	Food(PApplet parent, ArrayList <Animal> animals){
		radius = 5;
		diameter = radius * 2;
		pro = parent;
		
		int attempts = 20;
		boolean notFound = true;
		int aimX;
		int aimY;
		do {
			aimX = (int) pro.random(0, pro.width - radius);
			aimY = (int) pro.random(0, pro.height - radius);
			if(animals.size() < 0 || !(collideWithAnimal(animals, aimX, aimY))) {
				notFound = false;
			}
		} while(notFound || attempts <= 0);
		
		if(notFound) {
			x = -1;
			y = -1;
		} else {
			x = aimX;
			y = aimY;
		}
		
	}
	
	private boolean collideWithAnimal(ArrayList <Animal> animals, int aimX, int aimY) {
		for (int i = 0; i < animals.size(); i++) {
			// Find the distance between circles
			float dx = animals.get(i).x - aimX;
			float dy = animals.get(i).y - aimY;
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
		pro.circle(x, y, diameter);
	}
}
