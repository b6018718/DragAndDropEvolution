import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListenerProxy;

import org.gicentre.utils.stat.XYChart;

import g4p_controls.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.Table;
import processing.data.TableRow;

import org.gicentre.utils.move.*;

public class UI {
	PApplet pro;
	ArrayList<UiElement> uiElements = new ArrayList<UiElement>();
	ArrayList<UiBarChart> barCharts = new ArrayList<UiBarChart>();
	
	// Line Charts
	ArrayList<UiLineChart> uiLineCharts = new ArrayList<UiLineChart>();
	UiLineChart fpsLineChart;
	UiLineChart animalPopulation;
	UiLineChart birthRate;
	UiLineChart sizeChart;
	UiLineChart speedChart;
	UiLineChart foodChart;
	UiLineChart hungerChart;
	
	// Check boxes
	GCheckbox learnCheckBox;
	
	// Buttons
	GButton saveAsCsvBtn;
	
	// Neural network
	UiNeuralNetwork uiNeuralNetwork;
	
	// Drop downs
	GDropList graphSelect;
	final String POPULATION = "Population";
	final String LIFESPAN = "Lifespan";
	final String SIZE = "Size";
	final String SPEED = "Speed";
	final String FRAMERATE = "Framerate";
	final String FOOD = "Food";
	final String HUNGER = "Hunger";
	
	Environment env;
	PImage viewPort;
	PGraphics mask;
	double uiOffset;
	
	// Zoom elements
	ZoomPan zoomer;
	boolean zoomedIn = false;
	float speedMultiplier = 1;
	boolean drawWalls = false;
	double maxZoom = 2.2;
	
	// Draw Walls
	boolean wallsButtonPressed = false;
	PVector startingWall = new PVector();
	
	// Draw Sea
	boolean drawingSea = false;
	boolean seaStarted = false;
	PVector seaStart = new PVector();
	
	XYChart chart;
	
	UI(PApplet pro, Environment env, double uiOffset){
		this.pro = pro;
		this.env = env;
		this.uiOffset = uiOffset;
		
		String [] items;
		
		graphSelect = new GDropList(pro, pro.width * 0.85f, pro.height * 0.6f, pro.width * 0.1f, pro.height * 0.25f, 4);
		items = new String [] {POPULATION, LIFESPAN, SIZE, SPEED, FRAMERATE, FOOD, HUNGER};
		graphSelect.setItems(items, 0);
		graphSelect.tag = "graphSelect";
		
		//Set up zoomer 
		zoomer = new ZoomPan(pro);  // Initialise the zoomer
		zoomer.setMinZoomScale(1);
		zoomer.setMaxZoomScale(maxZoom);
		//zoomer.setMouseMask(PConstants.SHIFT);
		
		// Speed Up Button
		RectObj speedUpPos = new RectObj(pro.width * 0.4f, pro.height * 0.1f, pro.width * 0.05f, pro.height * 0.05f);
		UiElement speedUpUi = new UiSpeedUpButton(speedUpPos, true, uiElements, env, 4);
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
		
		// Create population line chart
		float chartXAn = (float) (pro.width * 0.81);
		float chartYAn = (float) (pro.height * 0.65);
		float chartWidthAn = (float) (pro.width * 0.18);
		float chartHeightAn = (float) (pro.height * 0.25);
		RectObj anRect = new RectObj(chartXAn, chartYAn, chartWidthAn, chartHeightAn);
		
		animalPopulation = new UiLineChart(pro, env, anRect, 1, true, POPULATION);
		
		// Frame rate chart
		fpsLineChart = new UiLineChart(pro, env, anRect, 60, true, FRAMERATE);
		fpsLineChart.display = false;
		
		// Create population line chart
		birthRate = new UiLineChart(pro, env, anRect, 1, true, LIFESPAN);
		birthRate.display = false;
		
		sizeChart = new UiLineChart(pro, env, anRect, 1, true, SIZE);
		sizeChart.display = false;
		
		speedChart = new UiLineChart(pro, env, anRect, 1, true, SPEED);
		speedChart.display = false;
		
		foodChart = new UiLineChart(pro, env, anRect, 1, true, FOOD);
		foodChart.display = false;
		
		hungerChart = new UiLineChart(pro, env, anRect, 1, true, HUNGER);
		hungerChart.display = false;
		
		uiLineCharts.add(animalPopulation);
		uiLineCharts.add(birthRate);
		uiLineCharts.add(sizeChart);
		uiLineCharts.add(speedChart);
		uiLineCharts.add(foodChart);
		uiLineCharts.add(fpsLineChart);
		uiLineCharts.add(hungerChart);
		
		// Create buttons
		saveAsCsvBtn = new GButton(pro, pro.width * 0.85f, pro.height * 0.925f, pro.width * 0.08f, pro.height * 0.05f, "Save As CSV");
		saveAsCsvBtn.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
		
		// Check boxes
		learnCheckBox = new GCheckbox(pro, pro.width * 0.7f, pro.height * 0.12f, pro.width * 0.1f, pro.height * 0.1f, "Machine Learning");
		
		// Neural network
		uiNeuralNetwork = new UiNeuralNetwork(pro, pro.width * 0.5f, pro.height * 0.02f, pro.width * 0.2f, pro.height * 0.15f);
	}
	
	public void handleDropListEvents(GDropList list, GEvent event) {
		String selectedItem = list.getSelectedText();
		if(list == graphSelect) {
			hideAllGraphs();
			switch(selectedItem) {
			case POPULATION:
				animalPopulation.display = true;
				break;
			case LIFESPAN:
				birthRate.display = true;
				break;
			case SPEED:
				speedChart.display = true;
				break;
			case SIZE:
				sizeChart.display = true;
				break;
			case FRAMERATE:
				fpsLineChart.display = true;
				break;
			case FOOD:
				foodChart.display = true;
				break;
			case HUNGER:
				hungerChart.display = true;
				break;
			}
		}
	}
	
	public void handleButtonEvents(GButton button, GEvent event) {
		  if (button == saveAsCsvBtn && event == GEvent.CLICKED) {
			  // Save the table in a different thread
			  pro.thread("saveTableThread");
		  }  
	}
	
	public void handleToggleControlEvents(GToggleControl checkBox, GEvent event) {
		if(checkBox == learnCheckBox) {
			learnEvent(checkBox);
		}
	}
	
	private void learnEvent(GToggleControl checkBox) {
		env.setMachineLearning(checkBox.isSelected());
	}
	
	public void saveTableThread() {
		  String filePath = G4P.selectFolder("Select where to save CSV file");
		  ArrayList<UiLineChart> uiLineChartsSave = new ArrayList<UiLineChart>(uiLineCharts);
		  Table table = new Table();
		  // Add columns
		  table.addColumn("Seconds");
		  for (UiLineChart chart : uiLineChartsSave) {
			  table.addColumn(chart.graphName);
		  }
		  // Add Rows
		  for (int i = 0; i < uiLineChartsSave.get(0).dataPoints.size(); i++) {
			  // Create row
		  TableRow newRow = table.addRow();
		  // Add seconds
		  newRow.setFloat("Seconds", uiLineChartsSave.get(0).dataPoints.get(i).x);
		  // Add additional columns
			  for (UiLineChart chart : uiLineChartsSave) {
				newRow.setFloat(chart.graphName, chart.dataPoints.get(i).y);
			  }
		  }
		  pro.saveTable(table, filePath + "/evolution_data_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
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
		showAnimalNetwork();
		
		// Show charts
		showCharts();
		
		// Show wall drawing operations
		showWallInProgress();
		
		showSeaInProgress();
		
		// Show generations
		pro.fill(0,0,0);
		pro.text("Generations " + env.generations, pro.width * 0.85f, pro.height * 0.15f);
	}
	
	private void showAnimalNetwork() {
		if(barCharts.size() > 0) {
			uiNeuralNetwork.draw();
		}
	}
	
	private void hideAllGraphs() {
		for (UiLineChart lineChart : uiLineCharts) {
			lineChart.display = false;
		}
	}
	
	private void showCharts() {
		for (UiLineChart lineChart : uiLineCharts) {
			lineChart.show();
		}
	}
	
	public void clearCharts() {
		for (UiLineChart chart : uiLineCharts) {
			chart.dataPoints.clear();
		}
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
		RectObj position = new RectObj(pro.width * 0.8f, pro.height * 0.4f, pro.width * 0.2f, pro.height * 0.2f);
		UiBarChart barChart = new UiBarChart(position, an, pro);
		barCharts.add(barChart);
		// Set the neural network
		uiNeuralNetwork.setAnimal(an);
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
			if(!drawWalls) {
				float zoomScale = (float) zoomer.getZoomScale();
				float widthCalc = an.width / 2 * zoomScale;
				PVector zoomPos = new PVector(- ((an.position.x - widthCalc - pro.width / 2) * zoomScale), -((an.position.y - widthCalc - pro.height / 2) * zoomScale)); //zoomer.getCoordToDisp(an.position);
				
				zoomer.setPanOffset(zoomPos.x, zoomPos.y);
				correctPanOffset();
			}
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
	
	public void beginDrawingWalls() {
		this.drawWalls = !this.drawWalls;
		if(drawWalls) {
			zoomOut();
		} else {
			zoomIn();
			resetWalls();
		}
	}
	
	private void zoomOut() {
		zoomer.reset();
		zoomer.allowPanButton(false);
		zoomer.allowZoomButton(false);
		zoomer.setMaxZoomScale(1);
	}
	
	private void zoomIn() {
		zoomer.reset();
		zoomer.allowPanButton(true);
		zoomer.allowZoomButton(true);
		zoomer.setMaxZoomScale(maxZoom);
	}
	
	public void drawWallsPressed() {
		if(this.drawWalls) {
			this.wallsButtonPressed = !this.wallsButtonPressed;
			
			// Create the walls
			if(!this.wallsButtonPressed) {
				// End drawing
				env.walls.add(new Wall(this.startingWall.x, this.startingWall.y, pro.mouseX, pro.mouseY));
				this.startingWall.x = pro.mouseX;
				this.startingWall.y = pro.mouseY;
				this.wallsButtonPressed = true;
			} else {
				// Start drawing
				this.startingWall.x = pro.mouseX;
				this.startingWall.y = pro.mouseY;
			}
		}
	}
	
	public void showWallInProgress() {
		if(this.drawWalls && startingWall.y != 0) {
			pro.stroke(0);
			pro.line(startingWall.x, startingWall.y, pro.mouseX, pro.mouseY);
		}
	}
	
	public void showSeaInProgress() {
		if(drawingSea && seaStarted) {
			float width = getSeaDistance(seaStart.x, pro.mouseX);
			float height = getSeaDistance(seaStart.y, pro.mouseY);
			
			float x = seaStart.x;
			float y = seaStart.y;
			if(seaStart.x > pro.mouseX)
				x = pro.mouseX;
			
			if(seaStart.y > pro.mouseY)
				y = pro.mouseY;
			
			pro.fill(10, 0, 175);
			pro.rect(x, y, width, height);
		}
	}
	
	public void resetWalls() {
		startingWall.y = 0;
		wallsButtonPressed = false;
	}
	
	public void beginDrawingSea() {
		//drawingSea = true;
		if(!drawingSea) {
			// Start drawing
			drawingSea = true;
			zoomOut();
		} else {
			// End drawing
			drawingSea = false;
			zoomIn();
		}
	}
	
	public void drawSeaPressed() {
		if(seaStarted) {
			float width = getSeaDistance(seaStart.x, pro.mouseX);
			float height = getSeaDistance(seaStart.y, pro.mouseY);
			float x = seaStart.x;
			float y = seaStart.y;
			if(seaStart.x > pro.mouseX)
				x = pro.mouseX;
			
			if(seaStart.y > pro.mouseY)
				y = pro.mouseY;
			
			env.sea.add(new RectObj(x, y, width, height));
			seaStarted = false;
		} else if(drawingSea) {
			seaStart.x = pro.mouseX;
			seaStart.y = pro.mouseY;
			seaStarted = true;
		}
	}
	
	private float getSeaDistance(float point1, float point2) {
		float dist = point1 - point2;
		if(dist < 0)
			dist = dist * -1;
		return dist;
	}
	
	public void resetSeaDrawing() {
		seaStart.y = 0;
		seaStarted = false;
	}
	
}
