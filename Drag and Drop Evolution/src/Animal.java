import java.util.ArrayList;

import processing.core.PApplet;

public class Animal {
	// Properties
	float x;
	float y;
	float radius;
	float standardSize;
	float diameter;
	Color colour = new Color();
	int direction;
	int stepsToMove;
	float movementSpeed;
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
		radius = getPixelsFromPercentWidth(5);
		diameter = radius * 2;
		standardSize = radius;
		
		// Survival
		bouncesTillDeath = 20;
		setNewRadius();
		int placeAttempts = 20;
		
		do {
			x = pro.random(0 + radius, pro.width - radius);
			y = pro.random(0 + radius, pro.height - radius);
			placeAttempts--;
			if(placeAttempts < 0) {
				bouncesTillDeath--;
				setNewRadius();
				placeAttempts = 20;
			}
		} while (collideWithAnimal(animals, x, y) && bouncesTillDeath > 0);
		
		// Set random colour
		colour.r = (int) pro.random(0, 255);
		colour.g = (int) pro.random(0, 255);
		colour.b = (int) pro.random(0, 255);
		
		// Movement
		stepsToMove = 0;
		movementSpeed = 130;
		direction = getRandomAngle();
	}
	
	float getPixelsFromPercentWidth(float percentOfScreenWidth) {
		return pro.width * (percentOfScreenWidth / 100);
	}
	
	void show() {
		pro.fill(colour.r, colour.g, colour.b);
		pro.stroke(0);
		pro.circle(x, y, diameter);
	}
	
	void move(ArrayList <Animal> animals, float deltaTime) {
		setDirection();
		
		//Time based animation
		float movementWithDelta = movementSpeed * deltaTime;
		
		// Frame based animation
		//float movementWithDelta = movementSpeed * 0.1f;
		float aimX = x + movementWithDelta * PApplet.sin(direction);
		float aimY = y + movementWithDelta * PApplet.cos(direction);
		
		x = aimX;
		y = aimY;
		
		if(collideWithAnimal(animals, aimX, aimY)) {
			shrinkSize();
		}
		isOutOfBounds();

		stepsToMove--;
	}
	
	private void setNewRadius() {
		float tempRadius = standardSize * bouncesTillDeath/15;
		if (tempRadius * 2 > pro.height) {
			radius = pro.height/2;
			bouncesTillDeath = bouncesTillDeath - 3;
		} else {
			radius = standardSize * bouncesTillDeath/15;
		}
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
		fixDirection();
	}
	
	private void fixDirection() {
		if(direction > 360) {
			direction = direction - 360;
		} else if(direction < 0) {
			direction = 360 - direction;
		}
	}
	
	public void checkIfDead(ArrayList <Animal> animals, int i) {
		if(bouncesTillDeath < 0) {
			animals.remove(i);
		}
	}
	
	public boolean collideWithAnimal(ArrayList <Animal> animals, float aimX, float aimY) {
		for (int i = 0; i < animals.size(); i++) {
			if(animals.get(i).id != id) {
				// Find the distance between circles
				float dx = animals.get(i).x - aimX;
				float dy = animals.get(i).y - aimY;
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
			//changeDirection();
		}
	}
	
	private void isOutOfBounds() {
		boolean needsShrinking = false;
		if(x + radius > pro.width) {
			x = pro.width - (int) radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
			
		if(x - radius < 0) {
			x = radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
		
		if(y + radius > pro.height) {
			y = pro.height - radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
		
		if(y - radius < 0) {
			y = radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
		
		if(needsShrinking)
			shrinkSize();
	}
	
	private void changeDirection() {
		//stepsToMove = (int) pro.random(1, 200);
		stepsToMove = 10000;
		direction = getRandomAngle();
	}
	
	public int getRandomAngle() {
		return (int) pro.random(0, 360);
	}
	
	public void eatFood(ArrayList <Food> foodArray) {
		for (int i = 0; i < foodArray.size(); i++) {
			// Find the distance between circles
			float dx = foodArray.get(i).x - x;
			float dy = foodArray.get(i).y - y;
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
