import java.util.ArrayList;
import java.util.HashSet;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import java.util.Set;

public class Animal extends EnvironmentObject {
	// Properties
	float standardSize;
	int direction;
	float movementSpeed;
	int id;
	boolean keepTurning = false;
	boolean isDead = false;
	float coinToss;
	
	// Life span
	float startingLifeSpan;
	float lifeSpanInMs;
	float startDyingPercentageOfLife = 0.2f;

	// Food
	float timeTillStarve;
	float startingTimeTillStarve;
	float whenGetHungryMs = 1000;
	boolean didStarve = false;
	boolean eatenOnce = false;
	
	// Reproduction
	float startingTimeTillLayEgg;
	float timeTillLayEgg;
	int numberOfChildren = 0;
	
	Gene gene;
	
	
	// This processor object allows us to access Processor methods outside of the main class
	//PApplet pro;
	
	Animal(PApplet parent, ArrayList <Animal> animals, ArrayList <Food> foodArray, RectObj env, HashGrid<EnvironmentObject> hashGrid, ImageManager imageManager, Gene gene, PVector eggPos){
		// Add the processing applet into the class
		super(parent, env, animals, foodArray, hashGrid, imageManager);
		this.gene = gene;
		this.width = (float) gene.size;
		this.startingLifeSpan = (float) gene.lifeSpan;
		this.lifeSpanInMs = startingLifeSpan;

		// Set the attributes
		// This is technically a bug, can get situations where certain animals have the same id
		this.id = animals.size();
		
		// Survival
		int placeAttempts = 20;
		
		if(eggPos == null) {
			do {
				position.x = pro.random(env.x, env.topX - width);
				position.y = pro.random(env.y, env.topY - width);
				placeAttempts--;
				if(placeAttempts < 0) {
					placeAttempts = 20;
					width--;
				}
			} while (collide(position) && width > 0);
		} else {
			position.x = eggPos.x;
			position.y = eggPos.y;
		}
		setHashPos();
		
		// Set random colour
		if(gene.colour == null) {
			colour.r = (int) pro.random(0, 255);
			colour.g = (int) pro.random(0, 255);
			colour.b = (int) pro.random(0, 255);
			gene.colour = colour;
		} else {
			colour = gene.colour;
		}
		
		// Movement
		movementSpeed = (float) gene.speed;
		direction = getRandomAngle();
		
		// Time till starve
		startingTimeTillStarve = calculateStarveTime();
		timeTillStarve = startingTimeTillStarve;
		
		// Reproduction
		startingTimeTillLayEgg = calculateBirthRate();
		timeTillLayEgg = startingTimeTillLayEgg;
	}
	
	float calculateBirthRate() {
		return startingLifeSpan/3;
	}
	
	float calculateStarveTime() {
		// y = x/12 and y = x/50, graphs for the starve time
		float smallBonus = 12000 - 1000 * this.width;
		float slowBonus = 50000 - 1000 * this.movementSpeed;
		float lifeSpanBonus = (float) ((60000 - gene.lifeSpan)/10); 
		//float lifeSpanMultiplier = (float) (gene.lifeSpan/60000);
		return 15000 + smallBonus + slowBonus + lifeSpanBonus;
	}
	
	float getPixelsFromPercentWidth(float percentOfScreenWidth) {
		return env.width * (percentOfScreenWidth / 100);
	}
	
	void move(float deltaTime) {
		PVector aimVector = getAimVector(deltaTime);
		if(collide(aimVector)) {
			notEnoughSpace();
			adjustAngle();
		} else {
			setAimVectorAsLocation(aimVector);
		}
		isOutOfBounds();
		setHashPos();
	}
	
	void notEnoughSpace() {
		timeTillStarve = (float) (timeTillStarve - (startingTimeTillStarve * 0.1));
	}
	
	Egg reproduce(int lastLoopTime) {
		// If no food in belly
		if(hasFoodInBelly()) {
			// Update the timer
			timeTillLayEgg = timeTillLayEgg - lastLoopTime;
		}
		
		// Check if can lay egg
		if(timeTillLayEgg < 0 && eatenOnce) {
			// Lay egg
			timeTillLayEgg = startingTimeTillLayEgg;
			return new Egg(pro, env, animals, foodArray, hashGrid, imageManager, gene, position);
		}
		return null;
	}
	
	@Override
	public void show() {
		// Image selected
		pro.tint(colour.r, colour.g, colour.b, 255);
		pro.image(imageManager.animalImage, position.x, position.y, width, width);
		pro.noTint();
	}
	
	boolean hasFoodInBelly() {
		// Must have at least 30% food or the egg counter will restart
		return (timeTillStarve / startingTimeTillStarve > 0.50f);
	}
	
	private PVector getAimVector(float deltaTime) {		
		//Time based animation
		float movementWithDelta = movementSpeed * deltaTime;
		
		// Frame based animation
		float aimX = position.x + movementWithDelta * PApplet.sin(direction);
		float aimY = position.y + movementWithDelta * PApplet.cos(direction);
		
		return new PVector(aimX, aimY);
	}
	
	void setAimVectorAsLocation(PVector aimVector) {
		keepTurning = false;
		position.x = aimVector.x;
		position.y = aimVector.y;
		setHashPos();
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
		
		lifeSpanInMs = lifeSpanInMs - lastLoopTime;
		timeTillStarve = timeTillStarve - lastLoopTime;
		
		float lifeRemaining = lifeSpanInMs / startingLifeSpan;
		// Elderly check
		if(lifeRemaining < startDyingPercentageOfLife && lifeSpanInMs > 0) {
			// Creature starts dying so calculate the speed reduction
			float movementMultiplier = lifeRemaining / startDyingPercentageOfLife;
			elderlySpeed = (float) (gene.speed * movementMultiplier);
		} else {
			elderlySpeed = (float) gene.speed;
		}
		
		// Hungry check
		if(timeTillStarve < whenGetHungryMs) {
			hungerSpeed = (float) (gene.speed * (timeTillStarve / whenGetHungryMs));
		} else {
			hungerSpeed = (float) gene.speed;
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
		if(timeTillStarve < 0 && lifeSpanInMs > 0 && !isDead) {
			lifeSpanInMs = 0;
			didStarve = true;
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
		if(!didStarve)
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
		eatenOnce = true;
		if(timeTillStarve > startingTimeTillStarve) {
			timeTillStarve = startingTimeTillStarve;
			movementSpeed = (float) gene.speed;
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
			hitSide = true;
		}
			
		if(position.x < env.x) {
			position.x = env.x;
			hitSide = true;
		}
		
		if(position.y + width > env.topY) {
			position.y = env.topY - width;
			hitSide = true;
		}
		
		if(position.y < env.y) {
			position.y = env.y;
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
		return (float) (movementSpeed / gene.speed);
	}
	
	public float normaliseEggTime() {
		return timeTillLayEgg / startingTimeTillLayEgg;
	}
	
}
