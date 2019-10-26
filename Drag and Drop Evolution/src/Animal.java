import processing.core.PApplet;

public class Animal {
	// Properties
	int x;
	int y;
	float radius;
	// This processor object allows us to access Processor methods outside of the main class
	PApplet pro;
	
	
	Animal(PApplet pro){
		x = (int) pro.random(0, pro.width);
		y = (int) pro.random(0, pro.height);
		radius = 35;
	}
	
	void draw() {
		
	}
}
