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
	float topWaterSpeed = 100;
	int id;
	boolean keepTurning = false;
	boolean isDead = false;
	float coinToss;
	double distanceToWall;
	
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
	boolean turning = false;
	boolean restMode = false;
	
	// Reproduction
	float startingTimeTillLayEgg;
	float timeTillLayEgg;
	int numberOfChildren = 0;
	
	// Water
	boolean insideWater = false;
	boolean nearestWallIsSolid = true;
	
	// Machine learning
	Gene gene;
	double[] nArgs;
	double[] weighOptions;
	ArrayList <Egg> eggArray;
	
	// This processor object allows us to access Processor methods outside of the main class
	//PApplet pro;
	
	Animal(PApplet parent, ArrayList <Species> species, ArrayList <Food> foodArray,
			Environment env, HashGrid<EnvironmentObject> hashGrid,
			ImageManager imageManager, Gene gene, PVector eggPos,
			ArrayList <Egg> eggArray){
		// Add the processing applet into the class
		super(parent, env, species, foodArray, hashGrid, imageManager);
		this.gene = gene;
		this.width = (float) gene.size;
		this.startingLifeSpan = (float) gene.lifeSpan;
		this.lifeSpanInMs = startingLifeSpan;
		this.distanceToWall = this.width;
		this.eggArray = eggArray;
		
		
		// Set the attributes
		// This is technically a bug, can get situations where certain animals have the same id
		this.id = gene.species.globalSpeciesNumber++;
		
		// Survival
		int placeAttempts = 20;
		if(pro != null) {
			if(eggPos == null) {
				do {
					position.x = pro.random(env.env.x, env.env.topX - width);
					position.y = pro.random(env.env.y, env.env.topY - width);
					placeAttempts--;
					if(placeAttempts < 0) {
						placeAttempts = 20;
						width--;
					}
				} while (collide(position, 0) && width > 0);
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
		}
		// Time till starve
		startingTimeTillStarve = calculateStarveTime();
		timeTillStarve = startingTimeTillStarve;
		
		// Reproduction
		startingTimeTillLayEgg = calculateBirthRate();
		timeTillLayEgg = startingTimeTillLayEgg;
	}
	
	float calculateBirthRate() {
		return (startingLifeSpan) /4;
	}
	
	float calculateStarveTime() {
		float smallBonus = 12000 - 1000 * (this.width / 2);
		float slowBonus = 50000 - 1000 * this.movementSpeed;
		float lifeSpanBonus = (float) (gene.lifeSpan / 1000 * 67);
		return 13000 + smallBonus + slowBonus + lifeSpanBonus;
	}
	
	float getPixelsFromPercentWidth(float percentOfScreenWidth) {
		return env.env.width * (percentOfScreenWidth / 100);
	}
	
	void move(float deltaTime) {
		setWallDistance();
		boolean moveForward = adjustAngle(deltaTime);
		if(!moveForward) {
			if(restMode)
				// Resting
				deltaTime = deltaTime * 0.1f;
			else
				// Turning
				deltaTime = deltaTime * 0.65f;
		}
		
		// Calculate water movement
		deltaTime = gene.species.behaveWaterMovement.getWaterMovement(deltaTime, insideWater);
			
		PVector aimVector = getAimVector(deltaTime);
		if(collide(aimVector, deltaTime)) {
			//notEnoughSpace(deltaTime);
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
			numberOfChildren++;
			// Lay egg
			timeTillLayEgg = startingTimeTillLayEgg;
			return new Egg(pro, env, species, foodArray, hashGrid, imageManager, gene, position);
		}
		return null;
	}
	
	@Override
	public void show() {
		// Image selected
		pro.tint(colour.r, colour.g, colour.b, 255);
		pro.image(gene.species.animalImage, position.x, position.y, width, width);
		pro.noTint();
	}
	
	boolean hasFoodInBelly() {
		// Must have at least % food or the egg counter will restart
		return (timeTillStarve / startingTimeTillStarve > 0.45f);
	}
	
	private PVector getAimVector(float deltaTime) {		
		//Time based animation
		float movementWithDelta = movementSpeed * deltaTime;
		inWater();
		if(insideWater && gene.species.behaveWaterMovement instanceof amphibious) {
			float waterSpeed = (float) (topWaterSpeed - gene.speed);
			if(waterSpeed > 75)
				waterSpeed = 75;
			if(waterSpeed < 25)
				waterSpeed = 25;
			if(waterSpeed < 1)
				waterSpeed = 1;
			movementWithDelta = waterSpeed * deltaTime;
		}
		
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
		float[] nearestFood = getNearFoodAngle();
		//System.out.println(nearestFood[0]);
		// Food angle
		double arg1 = normaliseRadians(nearestFood[0]);
		// Food distance
		double arg2 = normaliseSight(nearestFood[1]);
		// Current angle
		//double arg3 = this.direction / 360;
		
		float[] nearestAnimal = getNearAnimalAngle();
		// Animal angle
		double arg4 = normaliseRadians(nearestAnimal[0]);
		// Distance to Animal
		double arg5 = normaliseSight(nearestAnimal[1]);
		
		// Distance to closest wall
		double arg6 = (double) (normaliseX((float) this.distanceToWall));
		//System.out.println(arg5);
		//double arg6 = normaliseHunger();
		
		//double arg7 = normaliseEggTime();
		
		/*double arg8 = 0;
		if(insideWater) {
			arg8 = 1;
		}*/
		
		/*double arg9;
		if(this.restMode)
			arg9 = 1;
		else
			arg9 = 0;*/
		
		nArgs = new double[]{ arg1, arg2, arg4, arg5, arg6 };
		weighOptions = gene.neuralNetwork.guess(nArgs);
		int selection = getIndexOfMax(weighOptions);
		
		//System.out.println(nArgs[0]);
		//System.out.println(nArgs[1]);
		//System.out.println(nArgs[2]);
		//System.out.println(nArgs[3]);
		
		/*restMode = false;
		if(weighOptions[3] > 0.7) {
			// Enter rest mode
			restMode = true;
		}*/
		//System.out.println(weighOptions[3]);
		// Find out which direction to move
		turning = false;
		restMode = false;
		if(selection == 0) {
			direction = direction - 60 * deltaTime;
			fixDirection();
			turning = true;
			//System.out.println("LEFT");
			return false;
		} else if(selection == 1) {
			direction = direction + 60 * deltaTime;
			//System.out.println("RIGHT");
			fixDirection();
			turning = true;
			return false;
		} else if(selection == 2) {
			direction = direction - 10 * deltaTime;
			fixDirection();
			restMode = true;
			turning = true;
			return false;
		} else if(selection == 3) {
			direction = direction + 10 * deltaTime;
			fixDirection();
			restMode = true;
			turning = true;
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
	
	private double normaliseSight(float x) {
		return (double) (x / (env.sightDistance));
	}
	
	private double normaliseY(float y) {
		return (double) (y / env.env.height);
	}
	
	private double normaliseRadians(float radians) {
		return (double) radians / (2 * PConstants.PI);
	}
	
	private void setWallDistance() {
		PVector middlePosition = new PVector(position.x + this.width / 2, position.y + this.width / 2);
		PVector smallestIntersect = null;
		double smallestDist = 30000;
		for (Wall wall : env.walls) {
			PVector intersect = doesRayIntersectWall(wall);
			if(intersect != null) {
				if(smallestIntersect == null) {
					smallestIntersect = intersect;
					smallestDist = intersect.dist(middlePosition);
					nearestWallIsSolid = !wall.waterWall;
				} else {
					double dist = intersect.dist(middlePosition);
					if(dist < smallestDist) {
						smallestIntersect = intersect;
						smallestDist = dist;
						nearestWallIsSolid = !wall.waterWall;
					}
				}
			}
		}
		
		if(env.showLines && smallestIntersect != null)
			pro.line(middlePosition.x, middlePosition.y, smallestIntersect.x, smallestIntersect.y);
		
		this.distanceToWall = smallestDist;
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
	
	private float[] getNearFoodAngle() {
		if(hashGrid != null) {
			Set<EnvironmentObject> objectsInRange = hashGrid.get(position);
			Food closestFood = null;
			double closestDistance = 100.0;
			float sightX = position.x + this.width * 2 * PApplet.cos(PApplet.radians(direction));
			float sightY = position.y + this.width * 2 * PApplet.sin(PApplet.radians(direction));
			for (EnvironmentObject obj : objectsInRange) {
				if(obj instanceof Food) {
					// Cast object to the food type, for type safety
					Food food = (Food) obj;
					double dist = PApplet.dist(sightX, sightY, food.position.x, food.position.y);
					if(dist < closestDistance) {
						closestFood = food;
						closestDistance = dist;
					}
				}
			}
			// Send angle
			if(closestFood != null) {	
				float angle = getAngle(this.position, closestFood.position);
				if(env.showLines)
					pro.line(this.position.x, this.position.y, closestFood.position.x, closestFood.position.y);
				angle = angle - PApplet.radians(this.direction + 180);
				if(angle < 0)
					angle = angle * -1;
				angle = angle * 2;
				return new float[] {angle, (float) closestDistance};
			}
		}
		return new float[] {PApplet.radians(180), env.sightDistance};
	}
	
	private float[] getNearAnimalAngle() {
		if(hashGrid != null) {
			Animal closestAnimal = null;
			float sightX = position.x + this.width * 2 * PApplet.cos(PApplet.radians(direction));
			float sightY = position.y + this.width * 2 * PApplet.sin(PApplet.radians(direction));
			double closestDistance = 100.0;
			Set<EnvironmentObject> objectsInRange = hashGrid.get(position);
			for (EnvironmentObject obj : objectsInRange) {
				if(obj instanceof Animal) {
					Animal an = (Animal) obj;
					if(an.gene.species.speciesNumber != this.gene.species.speciesNumber) {
						double dist = PApplet.dist(sightX, sightY, an.position.x, an.position.y);
						if(dist < closestDistance) {
							closestAnimal = an;
							closestDistance = dist;
						}
					}
				}
			}
			
			if(closestAnimal != null) {
				float angle = getAngle(this.position, closestAnimal.position);
				if(env.showLines)
					pro.line(position.x, position.y, closestAnimal.position.x, closestAnimal.position.y);
				angle = angle - PApplet.radians(this.direction + 180);
				if(angle < 0)
					angle = angle * -1;
				angle = angle * 2;
				return new float[] {angle, (float) closestDistance};
			}
		}
		return new float[] {PApplet.radians(180), env.sightDistance};
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
		
		float waterLastLoop = gene.species.behaveWaterMovement.getHunger(lastLoopTime, insideWater);
		
		lifeSpanInMs = lifeSpanInMs - waterLastLoop;
		
		float hungerRate = gene.species.behaveFood.hungerRate();
		if(restMode || turning) {
			timeTillStarve = timeTillStarve - waterLastLoop * 1.1f * hungerRate;
		} else {
			timeTillStarve = timeTillStarve - waterLastLoop * hungerRate;
		}
		
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
		//if(!didStarve)
			//addFoodAtDeathLocation();
	}
	
	private void removeAnimalFromArray() {
		if(hashGrid != null)
			hashGrid.remove(this);
		gene.species.animals.remove(this);
	}
	
//	private void addFoodAtDeathLocation() {
//		foodArray.add(new Food(pro, animals, foodArray, env, hashGrid, position));
//		if(hashGrid != null) {
//			hashGrid.add(foodArray.get(foodArray.size() - 1));
//		}
//	}
	
	public boolean collide(PVector collidePos, float deltaTime) {
		if(hashGrid != null) {
			return checkForCollisions(collidePos, null, deltaTime);
		} else {
			for(int i = 0; i < species.size(); i++) {
				if(checkForCollisions(collidePos, species.get(i).animals, deltaTime))
					return true;
			}
			checkForCollisions(collidePos, foodArray, deltaTime);
		}
		return false;
	}

	private boolean checkForCollisions(PVector collidePos, ArrayList<? extends EnvironmentObject> arrList, float deltaTime) {
		if(this.distanceToWall <= this.width + this.movementSpeed * deltaTime && nearestWallIsSolid)
			return true;
		
		Set<EnvironmentObject> objectsInRange;
		if(hashGrid != null) {
			objectsInRange = hashGrid.get(collidePos);
		} else {
			objectsInRange = new HashSet<EnvironmentObject>(arrList);
		}
		
		for(EnvironmentObject obj : objectsInRange) {
			if(obj instanceof Animal) {
				Animal animal = (Animal)obj;				
				if(animal != this) {
					if(collisionAABB(animal, collidePos)) {
						eat(animal, hashGrid);
						return false;
					}
				}
			} else if(obj instanceof Food) {
				// Cast object to the food type, for type safety
				Food food = (Food) obj;
				if(collisionAABB(food, collidePos)) {
					eat(food, hashGrid);
				}
			} else if(obj instanceof Egg) {
				Egg egg = (Egg) obj;
				if(collisionAABB(egg, collidePos)) {
					eat(egg, hashGrid);
				}
			}
		}
		return false;
	}
	
	public void eat(Food food, HashGrid<EnvironmentObject> hashGrid) {
		if(gene.species.behaveFood.doesEatPlants()) {
			timeTillStarve += food.width * 4000;
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
	}
	
	public void eat(Egg egg, HashGrid<EnvironmentObject> hashGrid) {
		if(this.gene.species.speciesNumber != egg.gene.species.speciesNumber && this.gene.species.behaveFood.doesEatMeat()) {
			this.timeTillStarve += egg.width * 20000;
			this.eatenOnce = true;
			if(this.timeTillStarve > this.startingTimeTillStarve) {
				this.timeTillStarve = this.startingTimeTillStarve;
				this.movementSpeed = (float) this.gene.speed;
			}
			eggArray.remove(egg);
			if(hashGrid != null) {
				hashGrid.remove(egg);
			}
		}
	}
	
	public boolean eat(Animal animal, HashGrid<EnvironmentObject> hashGrid) {
		Animal prey = animal;
		Animal predator = this;
		
		if(animal.gene.species.behaveFood.doesEatMeat() &&
		  (animal.width > this.width || !(this.gene.species.behaveFood.doesEatMeat()))) {
				prey = this;
				predator = animal;
		}
		
		BehaviourFood preyType = prey.gene.species.behaveFood;
		BehaviourFood predatorType = predator.gene.species.behaveFood;
		
		if(predatorType.doesEatMeat() && prey.gene.species.speciesNumber != predator.gene.species.speciesNumber) {
			predator.timeTillStarve += prey.width * 20000;
			predator.eatenOnce = true;
			if(predator.timeTillStarve > predator.startingTimeTillStarve) {
				predator.timeTillStarve = predator.startingTimeTillStarve;
				predator.movementSpeed = (float) predator.gene.speed;
			}
			prey.gene.species.animals.remove(prey);
			if(hashGrid != null) {
				hashGrid.remove(prey);
			}
			return true;
		}
		return false;
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
	
	public boolean inWater() {
		for (int i = 0; i < env.sea.size(); i++) {
			if(collisionAABB(env.sea.get(i), position)) {
				insideWater = true;
				return true;
			}
		}
		insideWater = false;
		return false;
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
