import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class UI {
	PApplet pro;
	PImage speedUpImg1;
	PImage speedUpImg2;
	RectObj speedUpPos;
	ArrayList<UiElement> uiElements = new ArrayList<UiElement>();
	Environment env;
	
	class UiElement{
		RectObj position;
		ArrayList<PImage> spriteArray = new ArrayList<PImage>();
		boolean show;
		boolean clicked = false;
		int spriteNum = 0;
		UiElement(RectObj p, boolean s){
			position = p;
			show = s;
		}
		
		public void onClick() {}
	}
	
	class speedUpButton extends UiElement{
		speedUpButton(RectObj p, boolean show) {
			super(p, show);
		}
		
		public void onClick() {
			super.onClick();
			if(spriteNum == 1) {
				spriteNum = 0;
				clicked = false;
			} else {
				clicked = true;
				spriteNum = 1;
				env.speedUpDouble();
			}
		}
	}
	
	UI(PApplet pro, Environment env){
		this.pro = pro;
		this.env = env;
		
		// Speed Up Button
		RectObj speedUpPos = new RectObj(pro.width * 0.4f, pro.height * 0.1f, pro.width * 0.05f, pro.height * 0.05f);
		UiElement speedUpUi = new speedUpButton(speedUpPos, true);
		speedUpUi.spriteArray.add(pro.loadImage("Speed_Up_1.png"));
		speedUpUi.spriteArray.add(pro.loadImage("Speed_Up_2.png"));
		
		uiElements.add(speedUpUi);
		
	}
	
	public void display() {
		for (UiElement ui : uiElements) {
			if(ui.show)
				pro.image(ui.spriteArray.get(ui.spriteNum), ui.position.x, ui.position.y, ui.position.width, ui.position.height);
		}
	}
	
	public void checkCollisions(PVector mouse) {
		for (UiElement ui : uiElements) {
			if(ui.show && collisionAABB(mouse, ui.position)) {
				ui.onClick();
			}
		}
	}
	
	private boolean collisionAABB(PVector obj1, RectObj obj2) {
		if(obj1.x <= obj2.x + obj2.width && obj1.x >= obj2.x) { // X Collision
			if(obj1.y <= obj2.y + obj2.height && obj1.y >= obj2.y) { // Y Collision
				return true;
			}
		}
		return false;
	}
}
