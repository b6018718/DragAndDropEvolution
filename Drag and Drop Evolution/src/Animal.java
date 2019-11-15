import java.util.ArrayList;
import java.util.HashSet;

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
	float startingMovementSpeed;
	int id;
	float maxObjectSize;
	boolean keepTurning = false;
	float coinToss;
	float startingLifeSpan = 60000;
	float startingTimeTillStarve;
	float timeTillStarve;
	float lifeSpanInMs = startingLifeSpan;
	float startDyingPercentageOfLife = 0.2f;
	float whenGetHungryMs = 1000;
	boolean isDead = false;
	float speedMultiplier = 1;
	
	// This processor object allows us to access Processor methods outside of the main class
	//PApplet pro;
	
	Animal(PApplet parent, ArrayList <Animal> animals, ArrayList <Food> foodArray, float maxObjectSize, RectObj env, HashGrid<EnvironmentObject> hashGrid){
		// Add the processing applet into the class
		super(parent, env, animals, foodArray, hashGrid);

		// Set the attributes
		this.id = animals.size();
		//width = getPixelsFromPercentWidth(5);
		width = maxObjectSize;
		this.maxObjectSize = maxObjectSize;
		
		// Survival
		int placeAttempts = 20;
		
		do {
			position.x = pro.random(env.x, env.topX - width);
			position.y = pro.random(env.y, env.topY - width);
			placeAttempts--;
			if(placeAttempts < 0) {
				placeAttempts = 20;
				width--;
			}
		} while (collide(position) && width > 0);
		
		
		// Set random colour
		colour.r = (int) pro.random(0, 255);
		colour.g = (int) pro.random(0, 255);
		colour.b = (int) pro.random(0, 255);
		
		// Movement
		stepsToMove = 0;
		movementSpeed = 30;
		startingMovementSpeed = movementSpeed;
		direction = getRandomAngle();
		startingTimeTillStarve = 200000 * (1/width);
		timeTillStarve = startingTimeTillStarve;
	}
	
	float getPixelsFromPercentWidth(float percentOfScreenWidth) {
		return env.width * (percentOfScreenWidth / 100);
	}
	
	void move(float deltaTime) {
		PVector aimVector = getAimVector(deltaTime);
		if(collide(aimVector)) {
			adjustAngle();
		} else {
			setAimVectorAsLocation(aimVector);
		}
		isOutOfBounds();
		stepsToMove--;
	}
	
	private PVector getAimVector(float deltaTime) {		
		//Time based animation
		float movementWithDelta = movementSpeed * deltaTime * speedMultiplier;
		
		// Frame based animation
		float aimX = position.x + movementWithDelta * PApplet.sin(direction);
		float aimY = position.y + movementWithDelta * PApplet.cos(direction);
		
		return new PVector(aimX, aimY);
	}
	
	void setAimVectorAsLocation(PVector aimVector) {
		keepTurning = false;
		position.x = aimVector.x;
		position.y = aimVector.y;
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
	
	public boolean checkIfDead(float lastLoopTime) {
		float elderlySpeed = movementSpeed;
		float hungerSpeed = movementSpeed;
		
		lastLoopTime = lastLoopTime * speedMultiplier;

		lifeSpanInMs = lifeSpanInMs - lastLoopTime;
		timeTillStarve = timeTillStarve - lastLoopTime;
		
		float lifeRemaining = lifeSpanInMs / startingLifeSpan;
		// Elderly check
		if(lifeRemaining < startDyingPercentageOfLife && lifeSpanInMs > 0) {
			// Creature starts dying so calculate the speed reduction
			float movementMultiplier = lifeRemaining / startDyingPercentageOfLife;
			elderlySpeed = startingMovementSpeed * movementMultiplier;
		}
		
		// Hungry check
		if(timeTillStarve < whenGetHungryMs) {
			hungerSpeed = startingMovementSpeed * (timeTillStarve / whenGetHungryMs);
		}
		
		// Check which movement decrease is largest
		if(hungerSpeed < elderlySpeed) {
			movementSpeed = hungerSpeed;
		} else {
			movementSpeed = elderlySpeed;
		}
		
		// Compost check
		if(lifeSpanInMs < -2000) {
			removeFromEnvironment();
		}
		
		// Starve check
		if(timeTillStarve < 0 && lifeSpanInMs > 0) {
			lifeSpanInMs = 0;
			die();
			return true;
		}
			
		// Old age death check
		if(lifeSpanInMs < 0) {
			// remove from hash grid and the array list if you want to get rid of
			die();
			return true;
		}
		
		//Animal is still alive
		return false;
	}
	
	public void die() {
		movementSpeed = 0;
		timeTillStarve = 0;
		isDead = true;
	}
	
	public void removeFromEnvironment() {
		removeAnimalFromArray();
		addFoodAtDeathLocation();
	}
	
	private void removeAnimalFromArray() {
		if(hashGrid != null)
			hashGrid.remove(this);
		animals.remove(this);
	}
	
	private void addFoodAtDeathLocation() {
		foodArray.add(new Food(pro, animals, foodArray, env, hashGrid, position));
		if(hashGrid != null) {
			hashGrid.add(foodArray.get(foodArray.size() - 1));
		}
	}
	
	private boolean collide(PVector collidePos) {
		if(hashGrid != null) {
			return checkForCollisions(collidePos, null);
		} else {
			if(checkForCollisions(collidePos, animals))
				return true;
			checkForCollisions(collidePos, foodArray);
		}
		return false;
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
				if(animal.id != id) {
					if(collisionAABB(animal, collidePos)) {
						return true;
					}
				}
			} else if(obj instanceof Food) {
				// Cast object to the food type, for type safety
				Food food = (Food) obj;
				if(collisionAABB(food, collidePos)) {
					eat(food, hashGrid);
				}
			}
		}
		return false;
	}
	
	public void eat(Food food, HashGrid<EnvironmentObject> hashGrid) {
		timeTillStarve += food.width * 2000;
		if(timeTillStarve > startingTimeTillStarve) {
			timeTillStarve = startingTimeTillStarve;
			movementSpeed = startingMovementSpeed;
		}
		foodArray.remove(food);
		if(hashGrid != null) {
			hashGrid.remove(food);
		}
	}
	
	private void isOutOfBounds() {
		boolean hitSide = false;
		
		if(position.x + width > env.topX) {
			position.x = env.topX - width;
			stepsToMove = stepsToMove - 100;
			hitSide = true;
		}
			
		if(position.x < env.x) {
			position.x = env.x;
			stepsToMove = stepsToMove - 100;
			hitSide = true;
		}
		
		if(position.y + width > env.topY) {
			position.y = env.topY - width;
			stepsToMove = stepsToMove - 100;
			hitSide = true;
		}
		
		if(position.y < env.y) {
			position.y = env.y;
			stepsToMove = stepsToMove - 100;
			hitSide = true;
		}
		
		if(hitSide)
			adjustAngle();
		
	}
	
	public int getRandomAngle() {
		return (int) pro.random(0, 360);
	}
	
	public float normaliseLifeSpan() {
		return lifeSpanInMs / startingLifeSpan;
	}
	
	public float normaliseHunger() {
		return timeTillStarve / startingTimeTillStarve;
	}
	
	public float normaliseSpeed() {
		return movementSpeed / startingMovementSpeed;
	}
	
}
