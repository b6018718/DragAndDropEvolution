import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;

class AnimalTest extends PApplet{

	@Test
	void test() {
		ArrayList<Animal> animals = new ArrayList<Animal>();
		float maxObjectSize = 15f;
		RectObj envArea = new RectObj(0, 0, height, width);
		for(int i = 0; i < 1000; i++) {
			animals.add(new Animal(this, animals, maxObjectSize, envArea));
			assertTrue(animals.get(i).getRandomAngle() >= 0);
			assertTrue(animals.get(i).getRandomAngle() <= 360);
		}
		
		
		
		
		
	}

}
