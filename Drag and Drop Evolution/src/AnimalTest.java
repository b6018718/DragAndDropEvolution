import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import processing.core.PApplet;

class AnimalTest extends PApplet{

	@Test
	void test() {
		ArrayList<Animal> animals = new ArrayList<Animal>();
		for(int i = 0; i < 1000; i++) {
			animals.add(new Animal(this, i, animals));
			assertTrue(animals.get(i).getRandomAngle() >= 0);
			assertTrue(animals.get(i).getRandomAngle() <= 360);
		}
		
		
		
		
		
	}

}
