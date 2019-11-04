import org.gicentre.utils.geom.Locatable;
import processing.core.PVector;

public class EnvironmentObject implements Locatable {
	protected PVector position = new PVector();
	protected Color colour = new Color();
	
	class Color{
		int r;
		int g;
		int b;
	}
	
	public PVector getLocation(){
	    return position;
	}
}
