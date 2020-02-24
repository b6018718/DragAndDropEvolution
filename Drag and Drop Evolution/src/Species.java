import java.util.ArrayList;

import g4p_controls.GDropList;
import g4p_controls.GEvent;
import processing.core.PApplet;
import processing.core.PImage;



public class Species {
	public ArrayList<Animal> animals = new ArrayList<Animal>();
	int speciesNumber;
	String name;
	// Generations
	int generations = 1;
	public ArrayList<Animal> absoluteUnits = new ArrayList<Animal>();
	int numberOfUnits = 5;
	public boolean animalSelected = false;
	int globalSpeciesNumber = 0;
	
	final String POPULATION = "Population";
	final String LIFESPAN = "Lifespan";
	final String SIZE = "Size";
	final String SPEED = "Speed";
	final String FRAMERATE = "Framerate";
	final String FOOD = "Food";
	final String HUNGER = "Hunger";
	
	PImage animalImage;
	public String imageFilePath;
	
	// Line Charts
	ArrayList<UiLineChart> uiLineCharts = new ArrayList<UiLineChart>();
	UiLineChart fpsLineChart;
	UiLineChart animalPopulation;
	UiLineChart birthRate;
	UiLineChart sizeChart;
	UiLineChart speedChart;
	UiLineChart foodChart;
	UiLineChart hungerChart;
	GDropList dropDown;
	UI userInterface;
	
	// Speed
	public BehaviourSpeed behaveSpeed;
	
	// Size
	public BehaviourSize behaveSize;
	
	// Lifespan
	public BehaviourLifespan behaveLifespan;
	
	// Water movement
	public BehaviourWaterMovement behaveWaterMovement;
	
	// Food
	public BehaviourFood behaveFood;
	
	PApplet pro;
	
	Species(PApplet pro, Environment env, String imageFilePath, String name, UI userInterface,
			int speciesNumber, PImage animalImage, BehaviourSpeed behaveSpeed,
			BehaviourSize behaveSize, BehaviourLifespan behaveLifespan, BehaviourWaterMovement behaveWaterMovement,
			BehaviourFood behaveFood){
		this.speciesNumber = speciesNumber;
		this.animalImage = animalImage;
		this.userInterface = userInterface;
		this.pro = pro;
		this.behaveSpeed = behaveSpeed;
		this.behaveSize = behaveSize;
		this.behaveLifespan = behaveLifespan;
		this.behaveWaterMovement = behaveWaterMovement;
		this.imageFilePath = imageFilePath;
		this.name = name;
		this.behaveFood = behaveFood;
		
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
	}
	
	public void selectSpecies() {
		userInterface.selectedSpecies = this;
		handleDropListEvents(userInterface.graphSelect, null);
	}
	
	public void handleDropListEvents(GDropList list, GEvent event) {
		String selectedItem = list.getSelectedText();
		if(list == userInterface.graphSelect) {
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
	
	public void hideAllGraphs() {
		for (UiLineChart lineChart : uiLineCharts) {
			lineChart.display = false;
		}
	}
	
	public void showCharts() {
		for (UiLineChart lineChart : uiLineCharts) {
			lineChart.show();
		}
	}
	
	public void clearCharts() {
		for (UiLineChart chart : uiLineCharts) {
			chart.dataPoints.clear();
		}
	}
	
}
