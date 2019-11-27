import java.util.ArrayList;

import org.gicentre.utils.stat.XYChart;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import org.gicentre.utils.move.*;

public class UI {
	PApplet pro;
	ArrayList<UiElement> uiElements = new ArrayList<UiElement>();
	ArrayList<UiBarChart> barCharts = new ArrayList<UiBarChart>();
	
	// Line Charts
	UiLineChart fpsLineChart;
	UiLineChart animalPopulation;
	UiLineChart birthRate;
	
	Environment env;
	PImage viewPort;
	PGraphics mask;
	double uiOffset;
	// Zoom elements
	ZoomPan zoomer;
	boolean zoomedIn = false;
	float speedMultiplier = 1;
	
	XYChart chart;
	
	UI(PApplet pro, Environment env, double uiOffset){
		this.pro = pro;
		this.env = env;
		this.uiOffset = uiOffset;
		
		//Set up zoomer 
		zoomer = new ZoomPan(pro);  // Initialise the zoomer
		zoomer.setMinZoomScale(1);
		zoomer.setMaxZoomScale(2.5);
		//zoomer.setMouseMask(PConstants.SHIFT);
		
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
		
		// Create fps line charts
		float chartX = (float) (pro.width * 0.8);
		float chartY = (float) (pro.height * 0.2);
		float chartWidth = (float) (pro.width * 0.2);
		float chartHeight = (float) (pro.height * 0.7);
		RectObj fpsRect = new RectObj(chartX, chartY, chartWidth, chartHeight);
		fpsLineChart = new UiLineChart(pro, env, fpsRect, 60, false);
		fpsLineChart.display = false;
		
		// Create population line chart
		float chartXAn = (float) (pro.width * 0.81);
		float chartYAn = (float) (pro.height * 0.65);
		float chartWidthAn = (float) (pro.width * 0.18);
		float chartHeightAn = (float) (pro.height * 0.25);
		RectObj anRect = new RectObj(chartXAn, chartYAn, chartWidthAn, chartHeightAn);
		animalPopulation = new UiLineChart(pro, env, anRect, 1, true);
		
		// Create population line chart
		birthRate = new UiLineChart(pro, env, anRect, 1, true);
		birthRate.display = false;
	}
	
	public void display() {
		//Draw UI Boxes
		pro.fill(0, 117, 199);
		pro.noStroke();
		pro.rect(0f, 0f, (float) pro.width, (float) (pro.height * uiOffset));
		pro.rect((float) (pro.width * (1 - uiOffset)), 0f, (float) (pro.width * uiOffset), (float) pro.height);
		
		
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
		
		// Show charts
		fpsLineChart.show();
		animalPopulation.show();
		birthRate.show();
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
		RectObj position = new RectObj(pro.width * 0.8f, pro.height * 0.45f, pro.width * 0.2f, pro.height * 0.2f);
		UiBarChart barChart = new UiBarChart(position, an, pro);
		barCharts.add(barChart);
	}
	
	public void showAnimalViewPort() {
		if(barCharts.size() > 0) {
			Animal an = barCharts.get(0).an;
			
			PVector zoomedPos = zoomer.getCoordToDisp(an.position);
			viewPort = pro.get((int) (zoomedPos.x - an.width * 3),
					(int) (zoomedPos.y - an.width * 3),
					(int) an.width * 10,
					(int) an.width * 10);
			
			mask = pro.createGraphics((int) an.width * 10, (int) an.width * 10);
			mask.beginDraw();
			// Erase graphics
			mask.background(0);
			mask.fill(255);
			mask.noStroke();
			//mask.ellipse(an.position.x - an.width * 3, an.position.y - an.width * 3, an.width * 3, an.width * 3);
			pro.ellipseMode(PConstants.CENTER);
			mask.ellipse((float) (an.width * 4),(float) (an.width * 4), an.width * 8, an.width * 8);
			mask.stroke(128);
			mask.strokeWeight(5);
			mask.endDraw();
			
			viewPort.mask(mask);
			
			pro.image(viewPort, (int) (pro.width * 0.85), (int) (pro.height * 0.2), (int) (pro.width * 0.12), (int) (pro.width * 0.12));
			
			// Camera follow animal
			float zoomScale = (float) zoomer.getZoomScale();
			float widthCalc = an.width / 2 * zoomScale;
			PVector zoomPos = new PVector(- ((an.position.x - widthCalc - pro.width / 2) * zoomScale), -((an.position.y - widthCalc - pro.height / 2) * zoomScale)); //zoomer.getCoordToDisp(an.position);
			
			zoomer.setPanOffset(zoomPos.x, zoomPos.y);
			correctPanOffset();
		}
	}
	
	public void zoomStart() {
		pro.pushMatrix();
		zoomer.transform();
	}
	
	public void zoomEnd() {
		pro.popMatrix();
	}
	
	public void correctPanOffset() {
		float fixOffsetx = (float) (pro.width/2 * (zoomer.getZoomScale() - 1));
		float fixOffsety = (float) (pro.height/2 * (zoomer.getZoomScale() - 1));
		float fixOffsetyTop = (float) (pro.height  * (zoomer.getZoomScale() - 1) * 0.3);
		float fixOffsetxTop = (float) (pro.width  * (zoomer.getZoomScale() - 1) * 0.3);
		PVector panOffset = zoomer.getPanOffset();
		// Fix the offset to stop the zoom view going out of bounds
		if(panOffset.x < -fixOffsetxTop)
			panOffset.x = -fixOffsetxTop;
		if(panOffset.x > fixOffsetx)
			panOffset.x = fixOffsetx;
		if(panOffset.y > fixOffsetyTop)
			panOffset.y = fixOffsetyTop;
		if(panOffset.y < -fixOffsety)
			panOffset.y = -fixOffsety;

		zoomer.setPanOffset(panOffset.x, panOffset.y);
	}
}
