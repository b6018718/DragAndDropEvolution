import java.util.ArrayList;

import processing.core.PApplet;

public class Animal {
	// Properties
	int x;
	int y;
	float radius;
	float standardSize;
	float diameter;
	Color colour = new Color();
	int direction;
	int stepsToMove;
	int movementSpeed;
	int id;
	int bouncesTillDeath;
	
	class Color{
		int r;
		int g;
		int b;
	}
	
	// This processor object allows us to access Processor methods outside of the main class
	PApplet pro;
	
	Animal(PApplet parent, int id, ArrayList <Animal> animals){
		// Add the processing applet into the class
		pro = parent;
		this.id = id;
		// Set the attributes
		radius = (int) 24;
		diameter = radius * 2;
		standardSize = radius;
		
		
		do {
			x = (int) pro.random(0 + radius, pro.width - radius);
			y = (int) pro.random(0 + radius, pro.height - radius);
		} while (collideWithAnimal(animals, x, y));
		
		// Set random colour
		colour.r = (int) pro.random(0, 255);
		colour.g = (int) pro.random(0, 255);
		colour.b = (int) pro.random(0, 255);
		
		// Movement
		stepsToMove = 0;
		movementSpeed = 8;
		
		// Survival
		bouncesTillDeath = (int) pro.random(1, 20);
		setNewRadius();
	}
	
	void show() {
		pro.fill(colour.r, colour.g, colour.b);
		pro.stroke(0);
		pro.circle(x, y, diameter);
	}
	
	void move(ArrayList <Animal> animals) {		
		setDirection();
		int aimX = (int) (x + movementSpeed * PApplet.sin(direction));
		int aimY = (int) (y + movementSpeed * PApplet.cos(direction));
		
		if(!(collideWithAnimal(animals, aimX, aimY))) {
			x = aimX;
			y = aimY;
		} else {
			shrinkSize();
		}
		isOutOfBounds();

		stepsToMove--;
	}
	
	private void setNewRadius() {
		radius = standardSize * bouncesTillDeath/10;
		diameter = radius * 2;
	}
	
	private void shrinkSize() {
		bouncesTillDeath--;
		setNewRadius();
		adjustAngle();
	}
	
	public void adjustAngle() {
		float coinToss = pro.random(0, 1);
		if(coinToss > 0.5)
			direction += 10;
		else
			direction -= 10;
		if(direction > 360) {
			direction = direction - 360;
		}
	}
	
	public void checkIfDead(ArrayList <Animal> animals, int i) {
		if(bouncesTillDeath < 0) {
			animals.remove(i);
		}
	}
	
	public boolean collideWithAnimal(ArrayList <Animal> animals, int aimX, int aimY) {
		for (int i = 0; i < animals.size(); i++) {
			if(animals.get(i).id != id) {
				// Find the distance between circles
				int dx = animals.get(i).x - aimX;
				int dy = animals.get(i).y - aimY;
				float distance = PApplet.sqrt(dx * dx + dy * dy);
				float minDist = animals.get(i).radius + radius;
				if(distance < minDist) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void setDirection() {
		if(stepsToMove < 1) {
			changeDirection();
		}
	}
	
	private void isOutOfBounds() {
		if(x + radius > pro.width) {
			x = pro.width - (int) radius;
			stepsToMove = stepsToMove - 100;
			shrinkSize();
		}
			
		if(x - radius < 0) {
			x = (int) radius;
			stepsToMove = stepsToMove - 100;
			shrinkSize();
		}
		
		if(y + radius > pro.height) {
			y = pro.height - (int) radius;
			stepsToMove = stepsToMove - 100;
			shrinkSize();
		}
		
		if(y - radius < 0) {
			y = (int) radius;
			stepsToMove = stepsToMove - 100;
			shrinkSize();
		}
	}
	
	private void changeDirection() {
		stepsToMove = (int) pro.random(1, 200);
		direction = getRandomAngle();
	}
	
	public int getRandomAngle() {
		return (int) pro.random(0, 360);
	}
	
	public void eatFood(ArrayList <Food> foodArray) {
		for (int i = 0; i < foodArray.size(); i++) {
			// Find the distance between circles
			int dx = foodArray.get(i).x - x;
			int dy = foodArray.get(i).y - y;
			float distance = PApplet.sqrt(dx * dx + dy * dy);
			float minDist = foodArray.get(i).radius + radius;
			if(distance < minDist) {
				foodArray.remove(i);
				bouncesTillDeath = bouncesTillDeath + 3;
				setNewRadius();
			}
		}
	}
	
}
