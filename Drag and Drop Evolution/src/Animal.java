import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.Set;

public class Animal extends EnvironmentObject {
	// Properties
	float standardSize;
	int direction;
	int stepsToMove;
	float movementSpeed;
	int id;
	int bouncesTillDeath;
	float maxObjectSize;
	boolean keepTurning = false;
	float coinToss;
	
	// This processor object allows us to access Processor methods outside of the main class
	//PApplet pro;
	
	Animal(PApplet parent, ArrayList <Animal> animals, float maxObjectSize, RectObj env){
		// Add the processing applet into the class
		super(parent, env);

		// Set the attributes
		this.id = animals.size();
		radius = getPixelsFromPercentWidth(5);
		diameter = radius * 2;
		standardSize = radius;
		this.maxObjectSize = maxObjectSize;
		
		// Survival
		bouncesTillDeath = 20;
		setNewRadius();
		int placeAttempts = 20;
		
		do {
			position.x = pro.random(env.x + radius, env.topX - radius);
			position.y = pro.random(env.y + radius, env.topY - radius);
			placeAttempts--;
			if(placeAttempts < 0) {
				bouncesTillDeath--;
				setNewRadius();
				placeAttempts = 20;
			}
		} while (collideWithAnimal(animals, position) && bouncesTillDeath > 0);
		
		
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
		return env.width * (percentOfScreenWidth / 100);
	}
	
	void move(ArrayList <Animal> animals, float deltaTime) {
		PVector aimVector = getAimVector(deltaTime);
		if(collideWithAnimal(animals, aimVector)) {
			shrinkSize();
		} else {
			setAimVectorAsLocation(aimVector);
		}
		isOutOfBounds();
		stepsToMove--;
	}
	
	
	void moveWithHashGrid(HashGrid<EnvironmentObject> hashGrid, float deltaTime, ArrayList <Food> foodArray) {
		PVector aimVector = getAimVector(deltaTime);
		if(collideWithAnimalHashGrid(hashGrid, aimVector, foodArray)) {
			shrinkSize();
		} else {
			setAimVectorAsLocation(aimVector);
		}
		isOutOfBounds();
		stepsToMove--;
	}
	
	private PVector getAimVector(float deltaTime) {		
		//Time based animation
		float movementWithDelta = movementSpeed * deltaTime;
		
		// Frame based animation
		//float movementWithDelta = movementSpeed * 0.1f;
		float aimX = position.x + movementWithDelta * PApplet.sin(direction);
		float aimY = position.y + movementWithDelta * PApplet.cos(direction);
		
		return new PVector(aimX, aimY);
	}
	
	void setAimVectorAsLocation(PVector aimVector) {
		keepTurning = false;
		position.x = aimVector.x;
		position.y = aimVector.y;
	}
	
	
	
	private void setNewRadius() {
		float tempRadius = standardSize * bouncesTillDeath/15;
		if (tempRadius * 2 > env.topY || tempRadius * 2 > env.topX) {
			if(env.width > env.height)
				radius = env.height/2;
			else
				radius = env.width/2;
			bouncesTillDeath = bouncesTillDeath - 3;
		} else {
			radius = standardSize * bouncesTillDeath/15;
		}
		diameter = radius * 2;
		
		// Check if exceeds size limit
		if(diameter > maxObjectSize) {
			diameter = maxObjectSize;
			radius = maxObjectSize / 2;
		}
	}
	
	private void shrinkSize() {
		//bouncesTillDeath--;
		//setNewRadius();
		adjustAngle();
	}
	
	public void adjustAngle() {
		if(!keepTurning)
			coinToss = pro.random(0, 1);
		if(coinToss > 0.5)
			direction += 65;
		else
			direction -= 65;
		keepTurning = true;
		fixDirection();
	}
	
	private void fixDirection() {
		if(direction > 360) {
			direction = direction - 360;
		} else if(direction < 0) {
			direction = 360 - direction;
		}
	}
	
	public boolean checkIfDead(ArrayList <Animal> animals, int i) {
		if(bouncesTillDeath < 0) {
			animals.remove(i);
			return true;
		}
		return false;
	}
	
	public boolean collideWithAnimal(ArrayList <Animal> animals, PVector aimVector) {
		for (int i = 0; i < animals.size(); i++) {
			if(animals.get(i).id != id) {
				// Find the distance between circles
				float dx = animals.get(i).position.x - aimVector.x;
				float dy = animals.get(i).position.y - aimVector.y;
				float distance = PApplet.sqrt(dx * dx + dy * dy);
				float minDist = animals.get(i).radius + radius;
				if(distance < minDist) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean collideWithAnimalHashGrid(HashGrid<EnvironmentObject> hashGrid, PVector collidePos, ArrayList <Food> foodArray) {
		Set<EnvironmentObject> objectsInRange = hashGrid.get(collidePos);
		for(EnvironmentObject obj : objectsInRange) {
			if(obj instanceof Animal) {
				Animal animal = (Animal)obj;				
				if(animal.id != id) {
					// Find the distance between circles
					float dx = animal.position.x - collidePos.x;
					float dy = animal.position.y - collidePos.y;
					float distance = PApplet.sqrt(dx * dx + dy * dy);
					float minDist = animal.radius + radius;
					if(distance < minDist) {
						return true;
					}
				}
			} else if(obj instanceof Food) {
				// Cast object to the food type, for type safety
				Food food = (Food)obj;
				if(checkIfCollideWithFood(food, foodArray)) {
					hashGrid.remove(food);
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
		if(position.x + radius > env.topX) {
			position.x = env.topX - radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
			
		if(position.x - radius < env.x) {
			position.x = env.x + radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
		
		if(position.y + radius > env.topY) {
			position.y = env.topY - radius;
			stepsToMove = stepsToMove - 100;
			needsShrinking = true;
		}
		
		if(position.y - radius < env.y) {
			position.y = env.y + radius;
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
			checkIfCollideWithFood(foodArray.get(i), foodArray);
		}
	}
	
	private boolean checkIfCollideWithFood(Food food, ArrayList <Food> foodArray) {
		float dx = food.position.x - position.x;
		float dy = food.position.y - position.y;
		float distance = PApplet.sqrt(dx * dx + dy * dy);
		float minDist = food.radius + radius;
		if(distance < minDist) {
			foodArray.remove(food);
			bouncesTillDeath = bouncesTillDeath + 3;
			setNewRadius();
			return true;
		}
		return false;
	}
	
	
	
}
