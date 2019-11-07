import org.gicentre.utils.geom.Locatable;

import processing.core.PApplet;
import processing.core.PVector;

public abstract class EnvironmentObject implements Locatable {
	protected PVector position = new PVector();
	protected Color colour = new Color();
	protected PApplet pro;
	protected float diameter;
	protected float radius;
	protected RectObj env;
	
	
	EnvironmentObject(PApplet pro, RectObj env){
		this.pro = pro;
		this.env = env;
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
		pro.circle(position.x, position.y, diameter);
	}
}
