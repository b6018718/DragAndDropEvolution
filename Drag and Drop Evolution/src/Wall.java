import processing.core.PVector;

public class Wall {
	PVector start;
	PVector end;
	
	Wall(float startx, float starty, float endx, float endy){
		this.start = new PVector(startx, starty);
		this.end = new PVector(endx, endy);
	}
}
