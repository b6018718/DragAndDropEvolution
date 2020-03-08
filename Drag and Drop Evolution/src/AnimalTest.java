import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import junit.framework.TestCase;
import processing.core.PImage;

class AnimalTest extends TestCase{
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
		setUp();
		// TEST 1 Check correct number of animals added
		assertTrue(env.speciesArray.get(0).animals.size() == env.numOfAnimals);
		//for(int i = 0; i < 1000; i++) {
			//animals.add(new Animal(this, animals, foodArray, envArea, null, null, new Gene(null), null));
			//assertTrue(animals.get(i).getRandomAngle() >= 0);
			//assertTrue(animals.get(i).getRandomAngle() <= 360);
		//}
	}
	
	@Test
	void createFood() {
		setUp();
		// TEST 2 Check that there is correct amount of food
		assertTrue(env.foodArray.get(0).foodArray.size() == env.initialFood);
	}
}
