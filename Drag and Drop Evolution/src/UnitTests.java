import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import junit.framework.TestCase;
import processing.core.PImage;

class UnitTests extends TestCase{
	// 21 Tests to Go!
	public static Environment env;
	//ImageManager imageManager;
	
	public void setUp() {
		RectObj envArea = new RectObj(0, 0, 0, 0);
		
		//imageManager = new ImageManager(this);
		env = new Environment(null, envArea, null);
		double offset = 0.2;
		
		createOmnivoreSpecies(env);
	}
	
	public void createOmnivoreSpecies(Environment env) {
		String filePath = "Animals/Rabbit.png";
		//PImage animalImage = loadImage(filePath);
		PImage animalImage = null;
		String name = "Test Animal";
		BehaviourSpeed behaveSpeed = new slow();
		BehaviourSize behaveSize = new big();
		BehaviourLifespan behaveLifespan = new longLife();
		BehaviourWaterMovement behaveWaterMovement = new amphibious();
		BehaviourFood behaveFood = new omnivorous();
		BehaviourMutation behaveMutation = new mediumMutation();
		String brainFilePath = "";
		env.createSpecies(animalImage, filePath, name, behaveSpeed,
				behaveSize, behaveLifespan, behaveWaterMovement,
				behaveFood, behaveMutation, brainFilePath);
	}
	
	public void createHerbivoreSpecies(Environment env) {
		String filePath = "Animals/Rabbit.png";
		//PImage animalImage = loadImage(filePath);
		PImage animalImage = null;
		String name = "Test Animal";
		BehaviourSpeed behaveSpeed = new slow();
		BehaviourSize behaveSize = new small();
		BehaviourLifespan behaveLifespan = new longLife();
		BehaviourWaterMovement behaveWaterMovement = new amphibious();
		BehaviourFood behaveFood = new herbivorous();
		BehaviourMutation behaveMutation = new mediumMutation();
		String brainFilePath = "";
		env.createSpecies(animalImage, filePath, name, behaveSpeed,
				behaveSize, behaveLifespan, behaveWaterMovement,
				behaveFood, behaveMutation, brainFilePath);
	}
	
	
	@Test
	void createAnimals() {
		// TEST 1 Check correct number of animals added
		setUp();
		assertTrue(env.speciesArray.get(0).animals.size() == env.numOfAnimals);
		//for(int i = 0; i < 1000; i++) {
			//animals.add(new Animal(this, animals, foodArray, envArea, null, null, new Gene(null), null));
			//assertTrue(animals.get(i).getRandomAngle() >= 0);
			//assertTrue(animals.get(i).getRandomAngle() <= 360);
		//}
	}
	
	@Test
	void createFood() {
		// TEST 2 Check that there is correct amount of food
		setUp();
		assertTrue(env.foodArray.get(0).foodArray.size() == env.initialFood);
	}
	
	@Test
	void checkInitialTime() {
		// TEST 3 Check that the initial time multiplier is set to 1
		setUp();
		assertTrue(env.speedMultiplier == 1.0f);
	}
	
	@Test
	void checkWallsHaveBeenCreated() {
		// TEST 4 Check that there is the correct number of walls when the application is set up
		setUp();
		assertTrue(env.walls.size() == 4);
	}
	
	@Test
	void checkTimeStartsProperly() {
		// TEST 5 Check that there is the correct number of walls when the application is set up
		setUp();
		assertTrue(env.paused == false);
	}
	
	@Test
	void checkThatAnimalHasASpeedStrategy() {
		// TEST 6 Check that the strategy pattern was correctly applied to the speed
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveSpeed instanceof BehaviourSpeed);
	}
	
	@Test
	void checkThatAnimalHasASizeStrategy() {
		// TEST 7 Check that the strategy pattern was correctly applied to the size
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveSize instanceof BehaviourSize);
	}
	
	@Test
	void checkThatAnimalHasALifespanStrategy() {
		// TEST 8 Check that the strategy pattern was correctly applied to the lifespan
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveLifespan instanceof BehaviourLifespan);
	}
	
	@Test
	void checkThatAnimalHasAFoodStrategy() {
		// TEST 9 Check that the strategy pattern was correctly applied to the food
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveFood instanceof BehaviourFood);
	}
	
	@Test
	void checkThatAnimalHasAMutationStrategy() {
		// TEST 10 Check that the strategy pattern was correctly applied to the speed
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveMutation instanceof BehaviourMutation);
	}
	
	@Test
	void checkThatAnimalHasAWaterMovementStrategy() {
		// TEST 11 Check that the strategy pattern was correctly applied to the speed
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveWaterMovement instanceof BehaviourWaterMovement);
	}
	
	@Test
	void setAnimalSpeedStrategy() {
		// TEST 12 Check that the strategy pattern can be set for the speed
		setUp();
		env.speciesArray.get(0).animals.get(0).gene.species.behaveSpeed = new slow();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveSpeed instanceof BehaviourSpeed
				&& env.speciesArray.get(0).animals.get(0).gene.species.behaveSpeed instanceof slow);
	}
	
	@Test
	void setAnimalSizeStrategy() {
		// TEST 13 Check that the strategy pattern was correctly applied to the size
		setUp();
		env.speciesArray.get(0).animals.get(0).gene.species.behaveSize = new small();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveSize instanceof BehaviourSize
				&& env.speciesArray.get(0).animals.get(0).gene.species.behaveSize instanceof small);
	}
	
	@Test
	void setLifespanStrategy() {
		// TEST 14 Check that the strategy pattern was correctly applied to the lifespan
		setUp();
		env.speciesArray.get(0).animals.get(0).gene.species.behaveLifespan = new longLife();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveLifespan instanceof BehaviourLifespan
				&& env.speciesArray.get(0).animals.get(0).gene.species.behaveLifespan instanceof longLife);
	}
	
	@Test
	void setFoodStrategy() {
		// TEST 15 Check that the strategy pattern was correctly applied to the food
		setUp();
		env.speciesArray.get(0).animals.get(0).gene.species.behaveFood = new carnivorous();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveFood instanceof BehaviourFood
				&& env.speciesArray.get(0).animals.get(0).gene.species.behaveFood instanceof carnivorous);
	}
	
	@Test
	void setMutationStrategy() {
		// TEST 16 Check that the strategy pattern was correctly applied to the mutation
		setUp();
		env.speciesArray.get(0).animals.get(0).gene.species.behaveMutation = new highMutation();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveMutation instanceof BehaviourMutation
				&& env.speciesArray.get(0).animals.get(0).gene.species.behaveMutation instanceof highMutation);
	}
	
	@Test
	void setWaterMovementStrategy() {
		// TEST 17 Check that the strategy pattern was correctly applied to the water movement
		setUp();
		env.speciesArray.get(0).animals.get(0).gene.species.behaveWaterMovement = new hydrophobe();
		assertTrue(env.speciesArray.get(0).animals.get(0).gene.species.behaveWaterMovement instanceof BehaviourWaterMovement
				&& env.speciesArray.get(0).animals.get(0).gene.species.behaveWaterMovement instanceof hydrophobe);
	}
	
	@Test
	void animalStarveValueInitialised() {
		// TEST 18 Check the did starve value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).didStarve == false);
	}
	
	@Test
	void animalDeadValueInitialised() {
		// TEST 19 Check the is dead value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).isDead == false);
	}
	
	@Test
	void animalSelectedInitialised() {
		// TEST 20 Check the is selected value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).isSelected == false);
	}
	
	@Test
	void animalEatenOnceInitialised() {
		// TEST 21 Check the eaten once value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).eatenOnce == false);
	}
	
	@Test
	void animalInsideWaterInitialised() {
		// TEST 22 Check the inside water value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).insideWater == false);
	}
	
	@Test
	void animalNumberOfChildrenInitialised() {
		// TEST 23 Check the number of children value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).numberOfChildren == 0);
	}
	
	@Test
	void animalLifespanInitialised() {
		// TEST 24 Check the lifespan value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).lifeSpanInMs > 0);
	}
	
	@Test
	void animalFoodInBellyInitialised() {
		// TEST 25 Check the lifespan value has been initialised
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).hasFoodInBelly() == true);
	}
	
	@Test
	void animalCheckIfDeadWhenAlive() {
		// TEST 26 Use the check if dead function to take 10 ms off the life of the animal
		// The animal should still be alive
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).checkIfDead(10) == false);
	}
	
	@Test
	void animalCheckIfDeadWhenDead() {
		// TEST 27 Use the check if dead function to take 9999999 ms off the life of the animal
		// The animal should be dead
		setUp();
		assertTrue(env.speciesArray.get(0).animals.get(0).checkIfDead(9999999) == true);
	}
	
	@Test
	void animalEatAnimal() {
		// TEST 28 Check that animal A can eat animal B
		setUp();
		createHerbivoreSpecies(env);
		int numberOfAnimalsBefore = env.speciesArray.get(1).animals.size();
		// Get the two animals
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		Animal animalB = env.speciesArray.get(1).animals.get(0);
		// Make animal A eat animal B
		animalA.eat(animalB, null);
		int numberOfAnimalsAfter = env.speciesArray.get(1).animals.size();
		assertTrue(numberOfAnimalsBefore - 1 == numberOfAnimalsAfter);
	}
	
	@Test
	void animalEatAnimalCheckEatenOnce() {
		// TEST 29 Check that eaten once flag is properly set
		setUp();
		createHerbivoreSpecies(env);
		// Get the two animals
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		Animal animalB = env.speciesArray.get(1).animals.get(0);
		// Make animal A eat animal B
		animalA.eat(animalB, null);
		assertTrue(animalA.eatenOnce == true);
	}
	
	@Test
	void animalEatAnimalCheckEnergyIsIncreased() {
		// TEST 30 Check that energy is increased after eating an animal
		setUp();
		createHerbivoreSpecies(env);
		// Get the two animals
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		Animal animalB = env.speciesArray.get(1).animals.get(0);
		// Subtract 100ms of energy from the animal
		animalA.checkIfDead(100);
		// Record original time till starve
		float timeTillStarveBefore = animalA.timeTillStarve;
		// Make animal A eat animal B
		animalA.eat(animalB, null);
		// Get new time till starve
		float timeTillStarveAfter = animalA.timeTillStarve;
		assertTrue(timeTillStarveAfter > timeTillStarveBefore);
	}
	
	@Test
	void addFoodToEnvironment() {
		// TEST 31 Check adding a piece of food into the environment
		setUp();
		// Get food number before
		int foodCountBefore = env.foodArray.size();
		env.addFood();
		int foodCountAfter = env.foodArray.size();
		assertTrue(foodCountBefore < foodCountAfter);
	}
	
	@Test
	void animalEatFood() {
		// TEST 32 Check that an animal can eat a piece of food, the food is removed from the array
		setUp();
		// Create food
		env.addFood();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Get the number of food in the array
		int foodCountBefore = env.foodArray.size();
		// Make animal A eat a piece of food
		animalA.eat(env.foodArray.get(0), null);
		// Get the number of food after one has been eaten
		int foodCountAfter = env.foodArray.size();
		assertTrue(foodCountBefore > foodCountAfter);
	}
	
	@Test
	void animalEatFoodCheckEatenOnce() {
		// TEST 33 Check that eaten once flag is properly set after eating food
		setUp();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Create food
		env.addFood();
		// Make animal A eat animal B
		animalA.eat(env.foodArray.get(0), null);
		assertTrue(animalA.eatenOnce == true);
	}
	
	@Test
	void animalEatFoodCheckEnergyIsIncreased() {
		// TEST 34 Check that energy is increased after eating a piece food
		setUp();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Create the food
		env.addFood();
		// Subtract 100ms of energy from the animal
		animalA.checkIfDead(100);
		// Record original time till starve
		float timeTillStarveBefore = animalA.timeTillStarve;
		// Set the width of the food, to provide energy to the animal
		env.foodArray.get(0).width = 10;
		// Make animal A eat the food
		animalA.eat(env.foodArray.get(0), null);
		// Get new time till starve
		float timeTillStarveAfter = animalA.timeTillStarve;
		assertTrue(timeTillStarveAfter > timeTillStarveBefore);
	}
	
	@Test
	void checkEggArrayStartsEmpty() {
		// TEST 35 Check that the egg array is empty
		setUp();
		assertTrue(env.eggArray.size() == 0);
	}
	
	@Test
	void animalLaysAnEgg() {
		// TEST 36 Check that the animal can lay an egg
		setUp();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Add the food
		env.addFood();
		// Eat the food so the belly is full
		animalA.eat(env.foodArray.get(0), null);
		// Calculate how long it needs to lay an egg
		int eggTime = (int) animalA.timeTillLayEgg + 1;
		// Create an egg
		env.animalReproduce(animalA, eggTime);
		// Check if an egg was added to the array
		assertTrue(env.eggArray.size() == 1);
	}
	
	@Test
	void hatchAnEggCheckEggDisappears() {
		// TEST 37 Check that eggs can hatch
		setUp();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Add the food
		env.addFood();
		// Eat the food so the belly is full
		animalA.eat(env.foodArray.get(0), null);
		// Calculate how long it needs to lay an egg
		int eggTime = (int) animalA.timeTillLayEgg + 1;
		// Create an egg
		env.animalReproduce(animalA, eggTime);
		// Hatch the egg
		env.showEggs(env.eggArray.get(0).timeTillHatch + 1);
		// Check that the egg array is empty
		assertTrue(env.eggArray.size() == 0);
	}
	
	@Test
	void hatchAnEggCheckAnimalAppears() {
		// TEST 38 Check that an animal is created when an egg is hatched
		setUp();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Add the food
		env.addFood();
		// Eat the food so the belly is full
		animalA.eat(env.foodArray.get(0), null);
		// Calculate how long it needs to lay an egg
		int eggTime = (int) animalA.timeTillLayEgg + 1;
		// Create an egg
		env.animalReproduce(animalA, eggTime);
		// Get the animal count before the egg is hatched
		int animalCountBefore = env.speciesArray.get(0).animals.size();
		// Hatch the egg
		env.showEggs(env.eggArray.get(0).timeTillHatch + 1);
		// Get the animal count after the egg is hatched
		int animalCountAfter = env.speciesArray.get(0).animals.size();
		// Check that the egg array is empty
		assertTrue(animalCountAfter > animalCountBefore);
	}
	
	@Test
	void hatchAnEggAnimalSpeciesIdIncreases() {
		// TEST 39 Check that the global animal id increases when the animal is created
		setUp();
		// Get the animal
		Animal animalA = env.speciesArray.get(0).animals.get(0);
		// Add the food
		env.addFood();
		// Eat the food so the belly is full
		animalA.eat(env.foodArray.get(0), null);
		// Calculate how long it needs to lay an egg
		int eggTime = (int) animalA.timeTillLayEgg + 1;
		// Create an egg
		env.animalReproduce(animalA, eggTime);
		// Get the global species count before the egg is hatched
		int idCountBefore = env.speciesArray.get(0).globalSpeciesNumber;
		// Hatch the egg
		env.showEggs(env.eggArray.get(0).timeTillHatch + 1);
		// Get the global species count after the egg is hatched
		int idCountAfter = env.speciesArray.get(0).globalSpeciesNumber;
		// Check that id has increased
		assertTrue(idCountAfter > idCountBefore);
	}
	
	@Test
	void spawnFoodInEnvironment() {
		// TEST 40 Check that food is created in the environment
		setUp();
		// Get the amount of food
		int foodAmountBefore = env.foodArray.size();
		// Spawn new food, after setting internal timer to after the food event period
		env.foodCounter = (int) env.msPerFoodEvent + 1;
		env.showFood();
		// Get the new food amount
		int foodAmountAfter = env.foodArray.size();
		// Check that the egg array is empty
		assertTrue(foodAmountAfter == foodAmountBefore + env.foodPerEvent);
	}
}
