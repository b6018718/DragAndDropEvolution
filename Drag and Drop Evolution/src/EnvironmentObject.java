import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;
import org.gicentre.utils.geom.Locatable;

import processing.core.PApplet;
import processing.core.PVector;

public abstract class EnvironmentObject implements Locatable {
	protected PVector position = new PVector();
	protected Color colour = new Color();
	protected PApplet pro;
	protected float width;
	protected RectObj env;
	protected ArrayList <Animal> animals;
	protected ArrayList <Food> foodArray;
	protected HashGrid<EnvironmentObject> hashGrid = null;
	
	
	EnvironmentObject(PApplet pro, RectObj env, ArrayList<Animal> animals, ArrayList <Food> foodArray, HashGrid<EnvironmentObject> hashGrid){
		this.pro = pro;
		this.env = env;
		this.animals = animals;
		this.foodArray = foodArray;
		this.hashGrid = hashGrid;
	}
	
	class Color{
		int r;
		int g;
		int b;
	}
	
	public PVector getLocation(){
	    return position;
	}
	
	public void show() {
		pro.fill(colour.r, colour.g, colour.b);
		pro.stroke(0);
		pro.square(position.x, position.y, width);
	}
	
	protected boolean collisionAABB(EnvironmentObject obj, PVector aimPos) {
		if(obj.position.x <= aimPos.x + width && obj.position.x + obj.width >= aimPos.x) { // X Collision
			if(obj.position.y <= aimPos.y + width && obj.position.y + obj.width >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
}
