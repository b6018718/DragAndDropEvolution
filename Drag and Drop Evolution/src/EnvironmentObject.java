import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;
import org.gicentre.utils.geom.Locatable;

import processing.core.PApplet;
import processing.core.PVector;

public class EnvironmentObject implements Locatable {
	protected PVector position = new PVector();
	protected PVector hashPos = new PVector();
	protected Color colour = new Color();
	protected PApplet pro;
	protected float width;
	protected Environment env;
	protected ArrayList <Species> species;
	protected ArrayList <Food> foodArray;
	protected HashGrid<EnvironmentObject> hashGrid = null;
	public boolean isSelected = false;
	ImageManager imageManager = null;
	
	EnvironmentObject(PApplet pro, Environment env, ArrayList<Species> species, ArrayList <Food> foodArray, HashGrid<EnvironmentObject> hashGrid, ImageManager imageManager){
		this.pro = pro;
		this.env = env;
		this.species = species;
		this.foodArray = foodArray;
		this.hashGrid = hashGrid;
		this.imageManager = imageManager;
		setHashPos();
	}
	
	// Needed for hash grid
	public PVector getLocation(){
	    return hashPos;
	}
	
	public void setHashPos() {
		hashPos.x = position.x + width/2;
		hashPos.y = position.y + width/2;
	}
	
	public void show() {
		if(imageManager == null) {
			// No image selected
			pro.fill(colour.r, colour.g, colour.b, 255);
			pro.stroke(0);
			pro.square(position.x, position.y, width);
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
	
	protected boolean collisionAABB(RectObj obj, PVector aimPos) {
		if(obj.x <= aimPos.x + width && obj.x + obj.width >= aimPos.x) { // X Collision
			if(obj.y <= aimPos.y + width && obj.y + obj.height >= aimPos.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
}
