import java.util.ArrayList;

import org.gicentre.utils.stat.BarChart;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class UI {
	PApplet pro;
	ArrayList<UiElement> uiElements = new ArrayList<UiElement>();
	ArrayList<UiBarChart> barCharts = new ArrayList<UiBarChart>();
	Environment env;
	PImage viewPort;
	PGraphics mask;
	
	UI(PApplet pro, Environment env){
		this.pro = pro;
		this.env = env;
		
		// Speed Up Button
		RectObj speedUpPos = new RectObj(pro.width * 0.4f, pro.height * 0.1f, pro.width * 0.05f, pro.height * 0.05f);
		UiElement speedUpUi = new UiSpeedUpButton(speedUpPos, true, uiElements, env, 3);
		PImage load1 = pro.loadImage("Speed_Up_1.png");
		PImage load2 = pro.loadImage("Speed_Up_2.png");
		speedUpUi.spriteArray.add(load1);
		speedUpUi.spriteArray.add(load2);
		
		uiElements.add(speedUpUi);
		
		
		RectObj speedDownPos = new RectObj(pro.width * 0.3f, pro.height * 0.1f, pro.width * 0.05f, pro.height * 0.05f);
		UiElement speedDownUi = new UiSpeedUpButton(speedDownPos, true, uiElements, env, 0);
		speedDownUi.spriteArray.add(getReversePImage(load1));
		speedDownUi.spriteArray.add(getReversePImage(load2));
		
		uiElements.add(speedDownUi);
	}
	
	public void display() {
		// Display Bar Charts
		for (UiBarChart barChart : barCharts) {
			// Draw Data
			barChart.show();
		}
		//Display Ui Elements
		for (UiElement ui : uiElements) {
			if(ui.show)
				pro.image(ui.spriteArray.get(ui.spriteNum), ui.position.x, ui.position.y, ui.position.width, ui.position.height);
		}
		showAnimalViewPort();
	}
	
	private PImage getReversePImage( PImage image ) {
		PImage reverse;
		reverse = pro.createImage(image.width, image.height, PConstants.ARGB );

		for( int i=0; i < image.width; i++ ){
			for(int j=0; j < image.height; j++){
				int xPixel, yPixel;
				xPixel = image.width - 1 - i;
				yPixel = j;
				reverse.pixels[yPixel*image.width+xPixel]=image.pixels[j*image.width+i] ;
			}
		}
		return reverse;
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
	
	public void showAnimalChart(Animal an) {
		barCharts.clear();
		RectObj position = new RectObj(pro.width * 0.8f, pro.height * 0.3f, pro.width * 0.2f, pro.height * 0.2f);
		UiBarChart barChart = new UiBarChart(new BarChart(pro), position, an);
		barCharts.add(barChart);
	}
	
	public void showAnimalViewPort() {
		if(barCharts.size() > 0) {
			Animal an = barCharts.get(0).an;
			viewPort = pro.get((int) (an.position.x - an.width * 3),
					(int) (an.position.y - an.width * 3),
					(int) an.width * 6,
					(int) an.width * 6);
			
			mask = pro.createGraphics((int) an.width * 6, (int) an.width * 6);
			mask.beginDraw();
			// Erase graphics
			mask.background(0);
			mask.fill(255);
			mask.noStroke();
			//mask.ellipse(an.position.x - an.width * 3, an.position.y - an.width * 3, an.width * 3, an.width * 3);
			mask.ellipse(an.width * 3, an.width * 3, an.width * 6, an.width * 6);
			mask.stroke(128);
			mask.strokeWeight(5);
			mask.endDraw();
			
			viewPort.mask(mask);
			
			pro.image(viewPort, (int) (pro.width * 0.85), (int) (pro.height * 0.2));
			
			/*pro.copy(
					(int) (an.position.x - an.width * 3),
					(int) (an.position.y - an.width * 3),
					(int) an.width * 6,
					(int) an.width * 6,
					(int) (pro.width * 0.85),
					(int) (pro.height * 0.2),
					(int) an.width * 6,
					(int) an.width * 6
					);*/
		}
	}
}
