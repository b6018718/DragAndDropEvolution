import java.util.ArrayList;
import processing.core.PImage;

public class UiElement{
	RectObj position;
	ArrayList<PImage> spriteArray = new ArrayList<PImage>();
	ArrayList<UiElement> ui;
	boolean show;
	boolean clicked = false;
	int spriteNum = 0;
	Environment env;
	UiElement(RectObj p, boolean s, ArrayList<UiElement> ui, Environment env){
		position = p;
		show = s;
		this.ui = ui;
		this.env = env;
	}
	
	public void onClick() {}
}