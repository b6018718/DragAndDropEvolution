import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
	
	// Check boxes
	GCheckbox learnCheckBox;
	
	// Buttons
	GButton saveAsCsvBtn;
	GButton addAnimal;
	GButton exitButton;
	GButton getAnimalImage;
	GButton createSpecies;
	
	// Image button
	GImageButton animalPanelImage;
	ArrayList<AnimalIcon> animalIconArray = new ArrayList<AnimalIcon>();
	
	String animalFilePath;
	
	boolean animalPanel = false;
	
	// Text fields
	GTextField animalNameTextField;
	
	// Panels
	GPanel animalPanelUi;
	
	// Neural network
	UiNeuralNetwork uiNeuralNetwork;
	
	// Drop downs
	GDropList graphSelect;
	
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
	
	ImageManager imageManager;
	
	XYChart chart;
	
	// Radio buttons
	GOption behaviourLifespanDynamic;
	GOption behaviourLifespanLong;
	GOption behaviourLifespanShort;

	GOption behaviourSizeDynamic;
	GOption behaviourSizeBig;
	GOption behaviourSizeSmall;
	
	GOption behaviourSpeedDynamic;
	GOption behaviourSpeedSlow;
	GOption behaviourSpeedFast;
	
	GOption behaviourWaterAmphibious;
	GOption behaviourWaterLovesWater;
	GOption behaviourWaterHatesWater;
	
	GOption behaviourFoodHerbivore;
	GOption behaviourFoodCarnivore;
	GOption behaviourFoodOmnivore;
	
	GToggleGroup lifespanGroup;
	GToggleGroup sizeGroup;
	GToggleGroup speedGroup;
	GToggleGroup waterGroup;
	GToggleGroup foodGroup;
	
	// Radio label
	GLabel lifespanLabel;
	GLabel sizeLabel;
	GLabel speedLabel;
	GLabel waterLabel;
	GLabel eatLabel;
	
	final String POPULATION = "Population";
	final String LIFESPAN = "Lifespan";
	final String SIZE = "Size";
	final String SPEED = "Speed";
	final String FRAMERATE = "Framerate";
	final String FOOD = "Food";
	final String HUNGER = "Hunger";
	
	Species selectedSpecies = null;
	
	ArrayList<GAbstractControl> panelControls = new ArrayList<GAbstractControl>();
	
	UI(PApplet pro, Environment env, double uiOffset, ImageManager imageManager){
		this.pro = pro;
		this.env = env;
		this.uiOffset = uiOffset;
		this.imageManager = imageManager;
		
		String [] items;
		graphSelect = new GDropList(pro, pro.width * 0.85f, pro.height * 0.6f, pro.width * 0.1f, pro.height * 0.25f, 4);
		items = new String [] {POPULATION, LIFESPAN, SIZE, SPEED, FRAMERATE, FOOD, HUNGER};
		graphSelect.setItems(items, 0);
		graphSelect.tag = "graphSelect";
		
		//Set up zoomer 
		zoomer = new ZoomPan(pro);  // Initialise the zoomer
		zoomer.setMinZoomScale(1);
		//zoomer.setMaxZoomScale(maxZoom);
		zoomer.allowZoomButton(false);
		zoomer.setZoomMouseButton(PConstants.RIGHT);
		zoomer.setMaxZoomScale(1);
		zoomer.setMouseMask(PConstants.SHIFT);
		
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
		
		// Create buttons
		saveAsCsvBtn = new GButton(pro, pro.width * 0.85f, pro.height * 0.925f, pro.width * 0.08f, pro.height * 0.05f, "Save As CSV");
		saveAsCsvBtn.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
		
		// Check boxes
		//learnCheckBox = new GCheckbox(pro, pro.width * 0.7f, pro.height * 0.12f, pro.width * 0.1f, pro.height * 0.1f, "Machine Learning");
		
		// Button
		addAnimal = new GButton(pro, pro.width * 0.05f, pro.height * 0.12f, pro.width * 0.05f, pro.height * 0.05f, "Add Animal");
		addAnimal.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
		
		// Neural network
		uiNeuralNetwork = new UiNeuralNetwork(pro, pro.width * 0.5f, pro.height * 0.02f, pro.width * 0.2f, pro.height * 0.15f);
	}
	
	public void handleButtonEvents(GButton button, GEvent event) {
		if (event == GEvent.CLICKED) {
		  if (button == saveAsCsvBtn) {
			  // Save the table in a different thread
			  pro.thread("saveTableThread");
		  } else if(button == addAnimal && !animalPanel) {
			  // *** Spawn animal panel ***
			  openAnimalPanel();
		  } else if(button == addAnimal && animalPanel) {
			  closeAnimalPanel();
		  } else if(button == exitButton && animalPanel) {
			  closeAnimalPanel();
		  } else if(button == getAnimalImage && animalPanel) {
			  env.paused = true;
			  getAnimalImage();
		  } else if(button == createSpecies && animalPanel) {
			  env.createSpecies(
					  env.imageManager.animalImages.get(env.imageManager.animalImages.size() -1),
					  animalFilePath, getName(),  getSpeed(), getSize(), getLifespan(), getWaterMove(), getFood());
			  closeAnimalPanel();
		  }
		}
	}
	
	public void handleButtonEvents(GImageButton button, GEvent event) {
		  // Check animal icon buttons
		  for (AnimalIcon animalIcon : animalIconArray) {
			if(animalIcon.imageButton == button && animalIcon.species != selectedSpecies) {
				animalIcon.species.selectSpecies();
			}
		  }
	}
	
	private String getName() {
		return animalNameTextField.getText();
	}
	
	private BehaviourSpeed getSpeed() {
		if(behaviourSpeedDynamic.isSelected()) {
			return new dynamicSpeed();
		} else if(behaviourSpeedSlow.isSelected()) {
			return new slow();
		} else {
			return new fast();
		}
	}
	
	private BehaviourSize getSize() {
		if(behaviourSizeDynamic.isSelected()) {
			return new dynamicSize();
		} else if(behaviourSizeSmall.isSelected()) {
			return new small();
		} else {
			return new big();
		}
	}
	
	private BehaviourLifespan getLifespan() {
		if(behaviourLifespanDynamic.isSelected()) {
			return new dynamicLifespan();
		} else if(behaviourLifespanLong.isSelected()) {
			return new longLife();
		} else {
			return new shortLife();
		}
	}
	
	private BehaviourWaterMovement getWaterMove() {
		if(behaviourWaterAmphibious.isSelected()) {
			return new amphibious();
		} else if(behaviourWaterHatesWater.isSelected()) {
			return new hydrophobe();
		} else {
			return new hydrophile();
		}
	}
	
	private BehaviourFood getFood() {
		if(behaviourFoodHerbivore.isSelected()) {
			return new herbivorous();
		} else if(behaviourFoodOmnivore.isSelected()) {
			return new omnivorous();
		} else {
			return new carnivorous();
		}
	}
	
	
	// **************** ANIMAL PANEL **************************
	private void openAnimalPanel() {
		animalPanelUi = new GPanel(pro, pro.width * 0.1f, pro.height * 0.1f, pro.width * 0.6f, pro.height * 0.6f, "Creature Creator");
	  	//animalPanelUi.setDraggable(false);
	  	animalPanelUi.setCollapsible(false);
	  	animalPanelUi.setLocalColorScheme(GCScheme.GREEN_SCHEME);
	  
	  	float buttonWidth = pro.width * 0.05f;
	  	float buttonHeight = pro.height * 0.05f;
	  
	  	// Create exit button
	  	exitButton = new GButton(pro, pro.width * 0.54f, pro.height * 0.03f, buttonWidth, buttonHeight, "Exit");
	  	exitButton.setLocalColorScheme(GCScheme.ORANGE_SCHEME);
	  	panelControls.add(exitButton);
	  
	  	// Animal Name Text Field
	  	animalNameTextField = new GTextField(pro, pro.width * 0.01f, pro.height * 0.12f, pro.width * 0.09f, pro.height * 0.025f);
	  	animalNameTextField.setPromptText("Species name");
	  	panelControls.add(animalNameTextField);
	  
	  	// Create animal button
	  	getAnimalImage = new GButton(pro, pro.width * 0.1f, pro.height * 0.03f, buttonWidth, buttonHeight, "Load Image");
	  	panelControls.add(getAnimalImage);
		
	  	float radioWidth = pro.width * 0.09f;
	  	float radioHeight = pro.height * 0.03f;
	  	
	  	float distanceBetweenRadios = radioWidth * 1.1f;
	  	
	  	float startX = pro.width * 0.01f;
	  	float startY = pro.height * 0.3f;
	  	
	  	// LIFESPAN
	  	lifespanLabel = new GLabel(pro, startX, startY, radioWidth, radioHeight, "Lifespan");
	  	panelControls.add(lifespanLabel);
	  	
		behaviourLifespanDynamic = new GOption(pro, startX, startY + radioHeight, radioWidth, radioHeight, "Evolve freely");
		behaviourLifespanLong = new GOption(pro, startX, startY + radioHeight * 2, radioWidth, radioHeight, "Long lifespan");
		behaviourLifespanShort = new GOption(pro, startX, startY + radioHeight * 3, radioWidth, radioHeight, "Short lifespan");
		
		panelControls.add(behaviourLifespanDynamic);
		panelControls.add(behaviourLifespanLong);
		panelControls.add(behaviourLifespanShort);
		
		lifespanGroup = new GToggleGroup();
		lifespanGroup.addControls(behaviourLifespanDynamic, behaviourLifespanLong, behaviourLifespanShort);
		behaviourLifespanDynamic.setSelected(true);
		
		// SIZE
		startX = startX + distanceBetweenRadios;
		sizeLabel = new GLabel(pro, startX, startY, radioWidth, radioHeight, "Size");
		panelControls.add(sizeLabel);

		behaviourSizeDynamic = new GOption(pro, startX, startY + radioHeight, radioWidth, radioHeight, "Evolve freely");
		behaviourSizeBig = new GOption(pro, startX, startY + radioHeight * 2, radioWidth, radioHeight, "Big");
		behaviourSizeSmall = new GOption(pro, startX, startY + radioHeight * 3, radioWidth, radioHeight, "Small");
		
		panelControls.add(behaviourSizeDynamic);
		panelControls.add(behaviourSizeBig);
		panelControls.add(behaviourSizeSmall);
		
		sizeGroup = new GToggleGroup();
		sizeGroup.addControls(behaviourSizeDynamic, behaviourSizeBig, behaviourSizeSmall);
		behaviourSizeDynamic.setSelected(true);
		
		// SPEED
		startX = startX + distanceBetweenRadios;
		speedLabel = new GLabel(pro, startX, startY, radioWidth, radioHeight, "Speed");
		panelControls.add(speedLabel);

		behaviourSpeedDynamic = new GOption(pro, startX, startY + radioHeight, radioWidth, radioHeight, "Evolve freely");
		behaviourSpeedFast = new GOption(pro, startX, startY + radioHeight * 2, radioWidth, radioHeight, "Fast");
		behaviourSpeedSlow = new GOption(pro, startX, startY + radioHeight * 3, radioWidth, radioHeight, "Slow");
		
		panelControls.add(behaviourSpeedDynamic);
		panelControls.add(behaviourSpeedFast);
		panelControls.add(behaviourSpeedSlow);
		
		speedGroup = new GToggleGroup();
		speedGroup.addControls(behaviourSpeedDynamic, behaviourSpeedFast, behaviourSpeedSlow);
		behaviourSpeedDynamic.setSelected(true);
		
		// Water
		startX = startX + distanceBetweenRadios;
		waterLabel = new GLabel(pro, startX, startY, radioWidth, radioHeight, "Swimming");
		panelControls.add(waterLabel);

		behaviourWaterAmphibious = new GOption(pro, startX, startY + radioHeight, radioWidth, radioHeight, "Evolve freely");
		behaviourWaterHatesWater = new GOption(pro, startX, startY + radioHeight * 2, radioWidth, radioHeight, "Land creature");
		behaviourWaterLovesWater = new GOption(pro, startX, startY + radioHeight * 3, radioWidth, radioHeight, "Sea creature");
		
		panelControls.add(behaviourWaterAmphibious);
		panelControls.add(behaviourWaterHatesWater);
		panelControls.add(behaviourWaterLovesWater);
		
		waterGroup = new GToggleGroup();
		waterGroup.addControls(behaviourWaterAmphibious, behaviourWaterHatesWater, behaviourWaterLovesWater);
		behaviourWaterAmphibious.setSelected(true);
		
		// Water
		startX = startX + distanceBetweenRadios;
		eatLabel = new GLabel(pro, startX, startY, radioWidth, radioHeight, "Eating");
		panelControls.add(eatLabel);

		behaviourFoodHerbivore = new GOption(pro, startX, startY + radioHeight, radioWidth, radioHeight, "Herbivore");
		behaviourFoodCarnivore = new GOption(pro, startX, startY + radioHeight * 2, radioWidth, radioHeight, "Carnivore");
		behaviourFoodOmnivore = new GOption(pro, startX, startY + radioHeight * 3, radioWidth, radioHeight, "Omnivore");
		
		panelControls.add(behaviourFoodHerbivore);
		panelControls.add(behaviourFoodCarnivore);
		panelControls.add(behaviourFoodOmnivore);
		
		foodGroup = new GToggleGroup();
		foodGroup.addControls(behaviourFoodHerbivore, behaviourFoodCarnivore, behaviourFoodOmnivore);
		behaviourFoodHerbivore.setSelected(true);
		
		// Add animal to environment button
		createSpecies = new GButton(pro, pro.width * 0.28f, pro.height * 0.53f, buttonWidth, buttonHeight, "Create Animal");
		panelControls.add(createSpecies);
		
		// Connect to the panel
		for (int i = 0; i < panelControls.size(); i++) {
			animalPanelUi.addControl(panelControls.get(i));
		}
		
		animalPanel = true;
	}
	
	public void getAnimalImage() {
		String animalLocation = pro.sketchPath() + "\\src\\data\\Animals";
		String filePath = G4P.selectInput("Select an image file", "png, jpeg", "Image Files", animalLocation);
		PImage image = imageManager.addAnimalImage(filePath);
		animalFilePath = filePath;
		// Set animal image
		try {
			if(image != null) {
				animalPanelImage = new GImageButton(pro, pro.width * 0.03f, pro.height * 0.05f, 36, 36, new String[] { filePath, filePath, filePath });
				animalPanelUi.addControl(animalPanelImage);
			}
			filePath = filePath.substring(filePath.lastIndexOf('\\')+1);
			filePath = filePath.replaceAll("\\.[^.]*$", "");
			animalNameTextField.setText(filePath);
		} catch (Exception e) {
			System.out.println("Operations cancelled");
		}
	}
	
	private void closeAnimalPanel() {
		  animalPanel = false;
		  // Erase panel
		  animalPanelUi.dispose();
		  animalPanelUi = null;
		  
		  for (GAbstractControl panelControl : panelControls) {
			panelControl.dispose();
			panelControl = null;
		  }
		  panelControls.clear();
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
	  if(selectedSpecies != null) {
		  String filePath = G4P.selectOutput("Select where to save CSV file");
		  ArrayList<UiLineChart> uiLineChartsSave = new ArrayList<UiLineChart>(selectedSpecies.uiLineCharts);
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
		if(selectedSpecies != null)
			selectedSpecies.showCharts();
		
		// Show wall drawing operations
		showWallInProgress();
		
		showSeaInProgress();
		
		// Show generations
		pro.fill(0,0,0);
		
		if(env.speciesArray.size() > 0 && selectedSpecies != null)
			pro.text("Generations " + selectedSpecies.generations, pro.width * 0.85f, pro.height * 0.15f);
	}
	
	private void showAnimalNetwork() {
		if(barCharts.size() > 0) {
			uiNeuralNetwork.draw();
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
				env.walls.add(new Wall(this.startingWall.x, this.startingWall.y, pro.mouseX, pro.mouseY, false));
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
			// Add water walls
			env.walls.add( new Wall(x, y, x + width, y, true));
			env.walls.add( new Wall(x + width, y, x + width, y + height, true));
			env.walls.add( new Wall(x + width, y + height, x + width, y, true));
			env.walls.add( new Wall(x, y, x, y + height, true));
			
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
	
	public void addAnimalIcon(Species species) {
		int size = animalIconArray.size();
		float startX = pro.width * 0.01f;
		float startY = pro.height * 0.03f;
		float jumpSizeX = pro.width * 0.04f;
		float height = pro.height * 0.04f;
		float width = height;
		animalIconArray.add(new AnimalIcon(pro, species, startX + jumpSizeX * size, startY, height, width));
	}
}
