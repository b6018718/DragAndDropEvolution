import processing.core.PVector;

public class Wall {
	PVector start;
	PVector end;
	Boolean waterWall;
	
	Wall(float startx, float starty, float endx, float endy, boolean waterWall){
		this.start = new PVector(startx, starty);
		this.end = new PVector(endx, endy);
		this.waterWall = waterWall;
	}
}
