import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;
import org.gicentre.utils.geom.Locatable;

import processing.core.PApplet;
import processing.core.PImage;
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
	public boolean isSelected = false;
	PImage image = null;
	
	EnvironmentObject(PApplet pro, RectObj env, ArrayList<Animal> animals, ArrayList <Food> foodArray, HashGrid<EnvironmentObject> hashGrid, PImage image){
		this.pro = pro;
		this.env = env;
		this.animals = animals;
		this.foodArray = foodArray;
		this.hashGrid = hashGrid;
		this.image = image;
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
		if(image == null) {
			// No image selected
			pro.fill(colour.r, colour.g, colour.b, 255);
			pro.stroke(0);
			pro.square(position.x, position.y, width);
		} else {
			// Image selected
			pro.tint(colour.r, colour.g, colour.b, 255);
			pro.image(image, position.x, position.y, width, width);
			pro.noTint();
		}
	}
	
	public void showSelectedCircle() {
		// Draw outline to show is selected
		if (isSelected) {
			pro.fill(colour.r, colour.g, colour.b, 100);
			pro.ellipse(position.x + width/2, position.y + width/2, width * 3f, width * 3f);

		}
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
