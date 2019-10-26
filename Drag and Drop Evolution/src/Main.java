import processing.core.PApplet;

public class Main extends PApplet {

	public static void main(String[] args) {
		PApplet.main("Main");

	}
	
	public void settings() {
		fullScreen();
	}
	
	public void setup() {
		background(0);
	}
	
	public void draw() {
		setGradient(0, 0, width, height, color(60, 60, 60), color(10, 50, 255), Y_AXIS);
		
	}
	

	
	// Code from:
	//
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
