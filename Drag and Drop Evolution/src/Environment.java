import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Environment {
	// Processing applet
	PApplet pro;
	// Starting population animal count
	int numOfAnimals = 10;
	// Max size of animals
	float maxObjectSize = 24f;
	// Amount of food
	int foodPerEvent = 2;
	// Ms per food event
	float msPerFoodEvent = 2000;
	int initialFood = 20;
	float originalFoodPerEvent = msPerFoodEvent;
	// Counter to track time till next food spawn
	int foodCounter = 2000;
	// Use a hash grid
	boolean useHashGrid = true;
	// Hashgrid
	HashGrid<EnvironmentObject> hashGrid;
	// Environment dimensions
	RectObj env;
	// Ui interaction
	UI userInterface;
	// UI setting
	float speedMultiplier = 1;
	
	boolean paused = false;
	
	// Line between animals
	boolean showLines = false;
	
	// Walls
	ArrayList<Wall> walls = new ArrayList<Wall>();
	
	//ArrayList<Animal> animals = new ArrayList<Animal>();
	ArrayList<Food> foodArray = new ArrayList<Food>();
	ArrayList<Egg> eggArray = new ArrayList<Egg>();
	
	//ArrayList<Animal> topTenAnimals = new ArrayList<Animal>();
	ArrayList<RectObj> sea = new ArrayList<RectObj>();
	ArrayList<Species> speciesArray = new ArrayList<Species>();
	int speciesCount = 0;
	ImageManager imageManager;
	
	float tempSpeedMultipler;
	float sightDistance;
	
	Environment(PApplet processing, RectObj env, ImageManager imageManager){
		if(processing != null)
			pro = processing;
		this.env = env;
		if(imageManager != null)
			this.imageManager = imageManager;
		
		// Get the max proper max size
		if(pro != null) {
			maxObjectSize = calculateObjectSize();
			sightDistance = maxObjectSize * 9 + 1;
			
			// Create a hash grid
			if(useHashGrid) {
				hashGrid = new HashGrid<EnvironmentObject>(env.topX, env.topY, sightDistance);
			}
		}
		
		
		setupWalls();
		//createAnimals();
		createInitialFood();
	}
	
	private void setupWalls() {
		//Top wall
		walls.add(new Wall(env.x, env.y, env.topX, env.y, false));
		// Right wall
		walls.add(new Wall(env.topX, env.y, env.topX, env.topY, false));
		//Bottom wall
		walls.add(new Wall(env.x, env.topY, env.topX, env.topY, false));
		// Left wall
		walls.add(new Wall(env.x, env.y, env.x, env.topY, false));
	}
	
	public void createSpecies(PImage animalImage, String filePath, String name,
			BehaviourSpeed behaveSpeed, BehaviourSize behaveSize, BehaviourLifespan behaveLifespan,
			BehaviourWaterMovement behaveWaterMovement, BehaviourFood behaveFood, BehaviourMutation behaveMutation,
			String brainFilePath) {
		
		Species newSpecies = new Species(this.pro, this, filePath, name, userInterface, speciesCount,
				animalImage, behaveSpeed, behaveSize, behaveLifespan, behaveWaterMovement, behaveFood, behaveMutation, brainFilePath);
		speciesArray.add(newSpecies);
		speciesCount = speciesCount + 1;
		createAnimals(speciesArray.get(speciesArray.size() -1));
		if(pro != null) {
			userInterface.addAnimalIcon(newSpecies);
			newSpecies.selectSpecies();
		}
	}
	
	private void createAnimals(Species species) {
		// Create the animals
		for(int i = 0; i < numOfAnimals; i++) {
			if(!species.absoluteUnits.isEmpty()) {
				addAnimal(new Gene(species.absoluteUnits.get((int) (pro.random(species.absoluteUnits.size() - 1))).gene, species) , null, species);
			} else {
				addAnimal(new Gene(null, species), null, species);
			}
		}
		species.absoluteUnits.clear();
	}
	
	public void reset(Species species) {
		if(useHashGrid) {
			hashGrid.removeAll(species.animals);
		}
		
		species.animals.clear();
		species.generations++;

		createAnimals(species);
	}
	
	void createInitialFood() {
		for (int i = 0; i < initialFood; i++) {
			addFood();
		}
	}
	
	void draw(float deltaTime, int lastLoopTime) {
		boolean switched = false;
		if(paused) {
			tempSpeedMultipler = speedMultiplier;
			speedMultiplier = 0;
			paused = false;
			switched = true;
		}
			
		deltaTime = deltaTime * speedMultiplier;
		lastLoopTime = (int) (lastLoopTime * speedMultiplier);
		//pro.stroke(0);
		pro.fill(153, 255, 51, 190);
		pro.noStroke();
		pro.rect(env.x, env.y, env.width, env.height);
		
		//Save the best animal for next generation
		for(int i = 0; i < speciesArray.size(); i++) {
			ArrayList<Animal> animals = speciesArray.get(i).animals;
			if (animals.size() == 0) {
				reset(speciesArray.get(i));
			} else {
				ArrayList<Animal> topTenAnimals = speciesArray.get(i).absoluteUnits;
				if (animals.size() < 4 && topTenAnimals.isEmpty()) {
					topTenAnimals.addAll(animals);
				} else if(animals.size() > 4 && !topTenAnimals.isEmpty()) {
					topTenAnimals.clear();
				}
			}
		}
		
		showSea();
		
		// Draw the food onto the environment
		showFood();
		
		// Draw the eggs
		showEggs(lastLoopTime);
		
		// Iterate over the loop backwards so that we can remove animals from the list
		for(int i = speciesArray.size()-1; i >= 0; i--) {
			Species species = speciesArray.get(i);
			for(int j = species.animals.size()-1; j >= 0; j--) {
				Animal an = species.animals.get(j);
				an.show();
				an.move(deltaTime);
				animalReproduce(an, lastLoopTime);
				an.checkIfDead(lastLoopTime);
				
			}
		}
		
		// Draw walls
		drawAllWalls();
		
		// Update counters
		if(msPerFoodEvent != -1)
			foodCounter += lastLoopTime;
		
		if(useHashGrid)
			hashGrid.updateAll();
		
		if(switched)
			speedMultiplier = tempSpeedMultipler;
	}
	
	void showEggs(int lastLoopTime) {
		for(int i = eggArray.size()-1; i >= 0; i--) {
			Egg egg = eggArray.get(i);
			if(egg.hatch(lastLoopTime)) {
				addAnimal(egg.gene, egg.position, egg.gene.species);
				eggArray.remove(egg);
			} else {
				egg.show();
			}
		}
	}
	
	void animalReproduce(Animal an, int lastLoopTime) {
		Egg egg = an.reproduce(lastLoopTime);
		if(egg != null)
			eggArray.add(egg);
	}
	
	float calculateObjectSize() {
		return pro.width * 0.01f;
	}
	
	void showSelectedAnimal() {
		for (Species species : speciesArray) {
			for (Animal animal : species.animals) {
				animal.showSelectedCircle();
			}
		}
	}
	
	void showFood() {
		if(foodCounter > msPerFoodEvent && msPerFoodEvent != -1) {
			// Produce food
			foodCounter = 0;
			for (int i = 0; i < foodPerEvent; i++) {
				addFood();
			}
		}
		
		for (int i = 0; i < foodArray.size(); i++) {
			Food food = foodArray.get(i);
			food.show();
		}
	}
	
	void addFood() {
		foodArray.add(new Food(pro, speciesArray, foodArray, this, hashGrid, null));
		if(useHashGrid && pro != null) {
			hashGrid.add(foodArray.get(foodArray.size() - 1));
		}
	}
	
	void addAnimal(Gene gene, PVector eggPos, Species species) {
		for(int i = 0; i < speciesArray.size(); i++) {
			if(species == speciesArray.get(i)) {
				// Create new animal
				ArrayList<Animal> animals = speciesArray.get(i).animals;
				animals.add(new Animal(pro, speciesArray, foodArray, this, hashGrid, imageManager, gene, eggPos, eggArray));
				if(useHashGrid && pro != null) {
					hashGrid.add(animals.get(animals.size() -1));
				}
			}
		}
	}
	
	void clickOnEnv(PVector mouseZoomed, PVector mouseUnzoomed) {
		if(withinEnv(mouseUnzoomed)) {
			Animal an = null;
			if(userInterface.saveModelButton != null) {
				userInterface.saveModelButton.dispose();
			}
			if(hashGrid != null) {
				an = checkForAnimalCollisions(mouseZoomed, null);
			} else {
				for (int i = 0; i < speciesArray.size(); i++) {
					an = checkForAnimalCollisions(mouseZoomed, speciesArray.get(i).animals);
				}
			}
			// Send the selected animal to the user interface
			unselectAllAnimals();
			if(an != null){
				an.isSelected = true;
				userInterface.showAnimalChart(an);
			} else {
				userInterface.barCharts.clear();
			}
		}
	}
	
	private Animal checkForAnimalCollisions(PVector collidePos, ArrayList<? extends EnvironmentObject> arrList) {
		Set<EnvironmentObject> objectsInRange;
		if(hashGrid != null) {
			objectsInRange = hashGrid.get(collidePos);
		} else {
			objectsInRange = new HashSet<EnvironmentObject>(arrList);
		}
		
		for(EnvironmentObject obj : objectsInRange) {
			if(obj instanceof Animal) {
				Animal animal = (Animal)obj;				
				if(collisionAABBWithMouse(animal, collidePos)) {
					return animal;
				}
			}
		}
		return null;
	}
	
	protected boolean collisionAABBWithMouse(EnvironmentObject obj, PVector aimPos) {
		if(obj.position.x <= aimPos.x + obj.width * 2 && obj.position.x + obj.width * 2 >= aimPos.x) { // X Collision
			if(obj.position.y <= aimPos.y + obj.width * 2 && obj.position.y + obj.width * 2 >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
	
	protected boolean isInsideEnv(PVector aimPos) {
		if(env.x <= aimPos.x && env.topX >= aimPos.x) { // X Collision
			if(env.y <= aimPos.y && env.topY >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
	
	public void changeSpeedMultiplierInEnv(double multiplier) {
		speedMultiplier = (float) multiplier;
	}
	
	public void setUi(UI ui) {
		userInterface = ui;
	}
	
	public void unselectAllAnimals() {
		for (Species species : speciesArray) {
			for (Animal animal : species.animals) {
				animal.isSelected = false;
			}
		}
	}
	
	public boolean withinEnv(PVector mouse) {
		return (mouse.x > env.x && mouse.x < env.topX && mouse.y > env.y & mouse.y < env.topY);
	}
	
	public void showAnimalLines() {
		this.showLines = !this.showLines;
	}
	
	public void setMachineLearning(boolean learningBool) {
		for (Species species : speciesArray) {
			for (Animal animal : species.animals) {
				animal.gene.mutate = learningBool;
			}
		}
		
		for(Egg egg: eggArray) {
			egg.gene.generateRandomNetwork();
		}
	}
	
	private void drawAllWalls() {
		for (int i = 0; i < walls.size(); i++) {
			Wall wall = walls.get(i);
			pro.stroke(0);
			pro.line(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
		}
	}
	
	public void showSea() {
		for (int i = 0; i < sea.size(); i++) {
			RectObj seaSquare = sea.get(i);
			pro.fill(10, 0, 175);
			pro.rect(seaSquare.x, seaSquare.y, seaSquare.width, seaSquare.height);
		}
	}
}
