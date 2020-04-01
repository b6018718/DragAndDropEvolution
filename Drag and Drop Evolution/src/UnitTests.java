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
}
