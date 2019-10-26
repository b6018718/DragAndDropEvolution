import processing.core.PApplet;

public class Animal {
	// Properties
	int x;
	int y;
	float radius;
	float diameter;
	Color colour = new Color();
	
	class Color{
		int r;
		int g;
		int b;
	}
	
	// This processor object allows us to access Processor methods outside of the main class
	PApplet pro;
	
	Animal(PApplet parent){
		// Add the processing applet into the class
		pro = parent;
		// Set the attributes
		radius = (int) pro.random(50, 200);
		diameter = radius * 2;
		x = (int) pro.random(0 + radius, pro.width - radius);
		y = (int) pro.random(0 + radius, pro.height - radius);
		
		// Set random colour
		colour.r = (int) pro.random(0, 255);
		colour.g = (int) pro.random(0, 255);
		colour.b = (int) pro.random(0, 255);
		
	}
	
	void show() {
		pro.fill(colour.r, colour.g, colour.b);
		pro.stroke(0);
		pro.ellipse(x, y, diameter, diameter);
	}
}
