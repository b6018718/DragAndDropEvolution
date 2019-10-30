import processing.core.PApplet;
import java.util.*;

public class Main extends PApplet {

	public static void main(String[] args) {
		PApplet.main("Main");
	}
	
	ArrayList<Animal> animals = new ArrayList<Animal>();
	
	public void settings() {
		fullScreen();
		width = displayWidth;
		height = displayHeight;
	}
	
	public void setup() {
		background(0);
		for(int i = 0; i < 100; i++) {
			animals.add(new Animal(this, i, animals));
		}
		drawBackground();
	}
	
	public void drawBackground() {
		setGradient(0, 0, width, height, color(160, 160, 160), color(10, 50, 255), Y_AXIS);
	}
	
	
	public void draw() {
		drawBackground();
		for(int i = animals.size()-1; i >= 0; i--) {
			Animal an = animals.get(i);
			an.move(animals);
			an.show();
			an.checkIfDead(animals, i);
		}
	}
	
	
	// Constants
	int Y_AXIS = 1;
	int X_AXIS = 2;
	void setGradient(int x, int y, float w, float h, int c1, int c2, int axis ) {

		  noFill();

		  if (axis == Y_AXIS) {  // Top to bottom gradient
		    for (int i = y; i <= y+h; i++) {
		      float inter = map(i, y, y+h, 0, 1);
		      int c = lerpColor(c1, c2, inter);
		      stroke(c);
		      line(x, i, x+w, i);
		    }
		  }  
		  else if (axis == X_AXIS) {  // Left to right gradient
		    for (int i = x; i <= x+w; i++) {
		      float inter = map(i, x, x+w, 0, 1);
		      int c = lerpColor(c1, c2, inter);
		      stroke(c);
		      line(i, y, i, y+h);
		    } 
		  }
		}

}
