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
	float direction;
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
	
	// Machine learning
	Gene gene;
	double[] nArgs;
	double[] weighOptions;
	
	// This processor object allows us to access Processor methods outside of the main class
	//PApplet pro;
	
	Animal(PApplet parent, ArrayList <Animal> animals, ArrayList <Food> foodArray, Environment env, HashGrid<EnvironmentObject> hashGrid, ImageManager imageManager, Gene gene, PVector eggPos){
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
				position.x = pro.random(env.env.x, env.env.topX - width);
				position.y = pro.random(env.env.y, env.env.topY - width);
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
		return (startingLifeSpan) /3;
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
		return env.env.width * (percentOfScreenWidth / 100);
	}
	
	void move(float deltaTime) {
		boolean moveForward = adjustAngle(deltaTime);
		if(!moveForward)
			deltaTime = deltaTime * 0.7f;
		PVector aimVector = getAimVector(deltaTime);
		if(collide(aimVector)) {
			notEnoughSpace(deltaTime);
			//adjustAngle();
		} else {
			setAimVectorAsLocation(aimVector);
		}
		isOutOfBounds();
		setHashPos();
	}
	
	void notEnoughSpace(float deltaTime) {
		timeTillStarve = (float) (timeTillStarve - (startingTimeTillStarve * 0.1 * deltaTime));
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
		float aimX = position.x + movementWithDelta * PApplet.cos(PApplet.radians(direction));
		float aimY = position.y + movementWithDelta * PApplet.sin(PApplet.radians(direction));
		
		return new PVector(aimX, aimY);
	}
	
	void setAimVectorAsLocation(PVector aimVector) {
		keepTurning = false;
		position.x = aimVector.x;
		position.y = aimVector.y;
		setHashPos();
	}
	
	public boolean adjustAngle(float deltaTime) {
		float nearestFood = getNearFoodAngle();
		double arg1 = normaliseRadians(nearestFood);
		
		float nearestAnimal = getNearAnimalAngle();
		double arg2 = normaliseRadians(nearestAnimal);
		
		double arg3 = this.direction / 360;
		
		double arg4 = getNearWallDistance();
		
		nArgs = new double[]{ arg1, arg2, arg3, arg4 };
		weighOptions = gene.neuralNetwork.guess(nArgs);
		int selection = getIndexOfMax(weighOptions);
		
		//System.out.println(nArgs[0]);
		//System.out.println(nArgs[1]);
		//System.out.println(nArgs[2]);
		//System.out.println(nArgs[3]);
		
		// Find out which direction to move
		if(selection == 0) {
			direction = direction - 60 * deltaTime;
			fixDirection();
			//System.out.println("LEFT");
			return false;
		} else if(selection == 1) {
			direction = direction + 60 * deltaTime;
			//System.out.println("RIGHT");
			fixDirection();
			return false;
		}
		//System.out.println("FORWARD");
		// else if(selection == 2) {
			//Do Nothing
		//}
		return true;
		
		//direction = (int) (guess[0] * 360);
		//System.out.println(direction);
		//System.out.println("------------NEW----------");
		//System.out.println(direction);
//		if(!keepTurning)
//			coinToss = pro.random(0, 1);
//		if(coinToss > 0.5)
//			direction += 65;
//		else
//			direction -= 65;
//		keepTurning = true;
		//fixDirection();
	}
	
	private int getIndexOfMax(double [] arr) {
		int largest = 0;
		for (int i = 1; i < arr.length; i++) {
			if(arr[i] > arr[largest])
				largest = i;
		}
		return largest;
	}
	
	private double getSmallestValueInArr(ArrayList<Double> arr) {
		int smallest = 0;
		for (int i = 1; i < arr.size(); i++) {
			System.out.println(arr.get(i));
			if(arr.get(i) < arr.get(smallest))
				smallest = i;
		}
		return arr.get(smallest);
	}
	
	private int getIndexOfMin(double [] arr) {
		int smallest = 0;
		for (int i = 1; i < arr.length; i++) {
			if(arr[i] < arr[smallest])
				smallest = i;
		}
		return smallest;
	}
	
	private double normaliseX(float x) {
		return (double) (x / env.env.width);
	}
	
	private double normaliseY(float y) {
		return (double) (y / env.env.height);
	}
	
	private double normaliseRadians(float radians) {
		return (double) radians / (2 * PConstants.PI);
	}
	
	private double getNearWallDistance() {
		//double[] arr = new double[]{(double) (normaliseX(this.position.x - env.x)),
									//(double) (normaliseX(env.env.topX - this.position.x)),
									//(double) (normaliseY(this.position.y - env.y)),
									//(double) (normaliseY(env.env.topY - this.position.y))};
		
		ArrayList<Double> dists = new ArrayList<Double>();
		for (Wall wall : env.walls) {
			PVector intersect = doesRayIntersectWall(wall);
			if(intersect != null) {
				dists.add((double) intersect.dist(position));
				//pro.line(position.x, position.y, intersect.x, intersect.y);
			}
		}
		
		if(dists.size() > 0)
			return (double) (normaliseY((float) getSmallestValueInArr(dists)));
		else
			return 1;
		//pro.text("" + direction, position.x, position.y);
		
		
		//pro.text("" + arr[3], position.x, position.y);
		//return arr[getIndexOfMin(arr)];
		//return arr;
	}
	
	private PVector doesRayIntersectWall(Wall wall) {
		PVector dir = PVector.fromAngle(PApplet.radians(direction));
		dir.normalize();
		// Wall 1
		float x1 = wall.start.x;
		float y1 = wall.start.y;
		float x2 = wall.end.x;
		float y2 = wall.end.y;
		
		float x3 = position.x;
		float y3 = position.y;
		float x4 = position.x + dir.x;
		float y4 = position.y + dir.y;
		
		float denominator = (x1 - x2) * (y3 -y4) - (y1 - y2) * (x3 - x4);
		if (denominator == 0) {
			return null;
		}
		
		float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / denominator;
		float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / denominator;
		if(t > 0 && t < 1 && u > 0) {
			float x = x1 + t * (x2 - x1);
			float y = y1 + t * (y2 - y1);
			return new PVector(x, y);
		}
		return null;
	}
	
	private float getNearFoodAngle() {
		if(hashGrid != null) {
			Set<EnvironmentObject> objectsInRange = hashGrid.get(position);
			for (EnvironmentObject obj : objectsInRange) {
				if(obj instanceof Food) {
					// Cast object to the food type, for type safety
					Food food = (Food) obj;
					//System.out.println(food.position.x);
					float angle = getAngle(this.position, food.position);
					//pro.line(this.position.x, this.position.y, food.position.x, food.position.y);
					return angle;
				}
			}
		}
		return 0;
	}
	
	private float getNearAnimalAngle() {
		if(hashGrid != null) {
			Set<EnvironmentObject> objectsInRange = hashGrid.get(position);
			for (EnvironmentObject obj : objectsInRange) {
				if(obj instanceof Animal) {
					Animal an = (Animal) obj;
					float angle = getAngle(this.position, an.position);
					return angle;
				}
			}
		}
		return 0;
	}
	
	float getAngle(PVector a, PVector b){
		  return PApplet.atan2(b.y - a.y, b.x - a.x) + PApplet.PI;
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
		
		if(position.x + width > env.env.topX) {
			position.x = env.env.topX - width;
			hitSide = true;
		}
			
		if(position.x < env.env.x) {
			position.x = env.env.x;
			hitSide = true;
		}
		
		if(position.y + width > env.env.topY) {
			position.y = env.env.topY - width;
			hitSide = true;
		}
		
		if(position.y < env.env.y) {
			position.y = env.env.y;
			hitSide = true;
		}
		
		//if(hitSide)
			//adjustAngle();
		
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
