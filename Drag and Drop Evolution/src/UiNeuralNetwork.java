import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class UiNeuralNetwork {
	RectObj location;
	PApplet pro;
	Animal animal = null;
	boolean show = true;
	float diameter;
	ArrayList<PVector> inputs = new ArrayList<PVector>();
	ArrayList<ArrayList <PVector>> hidden = new ArrayList<ArrayList <PVector>>();
	ArrayList<PVector> outputs = new ArrayList<PVector>();
	
	
	UiNeuralNetwork(PApplet pro, float x, float y, float width, float height){
		location = new RectObj(x, y, width, height);
		this.pro = pro;
	}
	
	public void setAnimal(Animal animal) {
		inputs.clear();
		hidden.clear();
		outputs.clear();
		
		
		this.animal = animal;
		int inputCount = animal.gene.neuralNetwork.getInputNodes();
		diameter = location.height / (inputCount * 2f + 1) ;
		
		for (int i = 0; i < inputCount; i++) {
			inputs.add(new PVector(location.x + location.height / 3, location.y + diameter * (i + 0.5f)  * 2));
		}
		
		int hiddenLayerCount = animal.gene.neuralNetwork.getHiddenLayers();
		int hiddenCount = animal.gene.neuralNetwork.getHiddenNodes();
		
		for(int i = 0; i < hiddenLayerCount; i++) {
			ArrayList <PVector> arr = new ArrayList <PVector>();
			for(int j = 0; j < hiddenCount; j++) {
				
			}
			hidden.add(arr);
		}
		
	}
	
	public void resetAnimal() {
		this.animal = null;
		inputs.clear();
		hidden.clear();
		outputs.clear();
	}
	
	public void draw() {
		pro.fill(160);
		pro.rect(location.x, location.y, location.width, location.height);
		
		double[] inputColours = animal.nArgs;
		if(animal != null && show) {
			for (int i = 0; i < inputs.size(); i++) {
				pro.fill((int) (inputColours[i] * 255));
				pro.circle(inputs.get(i).x, inputs.get(i).y, diameter);
			}
		}
	}
	
	
	/*float radius = pro.width * 0.01f;
	double[] args = barCharts.get(0).an.nArgs;
	for (int i = 0; i < args.length; i++) {
		pro.fill((int) (args[i] * 255));
		pro.circle(x, y, radius);
		y = y + radius * 1.5f;
	}
	args = barCharts.get(0).an.weighOptions;
	x = pro.width * 0.52f;
	y = pro.height * 0.02f;
	for (int i = 0; i < args.length; i++) {
		pro.fill((int) (args[i] * 255));
		pro.circle(x, y, radius);
		y = y + radius * 1.5f;
	}*/
}
