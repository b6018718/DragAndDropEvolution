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
		int hiddenLayerCount = animal.gene.neuralNetwork.getHiddenLayers();
		int hiddenCount = animal.gene.neuralNetwork.getHiddenNodes();
		int outCount = animal.gene.neuralNetwork.getOutputNodes();
		
		int layersTotalCount = 2 + hiddenLayerCount;
		
		diameter = location.height / (inputCount * 2f + 1) ;
		
		// Draw Circles
		for (int i = 0; i < inputCount; i++) {
			inputs.add(new PVector(location.x + (location.width / (layersTotalCount + 1)) * 1, location.y + (location.height / (inputCount + 1)) * (i + 1)  ));
		}

		
		for(int i = 0; i < hiddenLayerCount; i++) {
			ArrayList <PVector> arr = new ArrayList <PVector>();
			for(int j = 0; j < hiddenCount; j++) {
				arr.add(new PVector(location.x + (location.width / (layersTotalCount + 1)) * (i + 2), location.y + (location.height / (hiddenCount + 1)) * (j + 1)  ));
			}
			hidden.add(arr);
		}
		
		for (int i = 0; i < outCount; i++) {
			outputs.add(new PVector(location.x + (location.width / (layersTotalCount + 1)) * (2 + hiddenLayerCount), location.y + (location.height / (outCount + 1)) * (i + 1)  ));
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
		
		
		if(animal != null && show) {
			// Draw lines
			pro.stroke(0);
			pro.strokeWeight(1);
			for (int i = 0; i < inputs.size(); i++) {
				for (int j = 0; j < hidden.get(0).size(); j++) {
					pro.line(inputs.get(i).x, inputs.get(i).y, hidden.get(0).get(j).x, hidden.get(0).get(j).y);
				}
			}
			
			for (int i = 0; i < hidden.size() -1; i++) {
				for (int j = 0; j < hidden.get(i).size(); j++) {
					for (int k = 0; k < hidden.get(i + 1).size(); k++) {
						pro.line(hidden.get(i).get(j).x, hidden.get(i).get(j).y, hidden.get(i + 1).get(k).x, hidden.get(i + 1).get(k).y);
					}
				}
			}
			
			for (int i = 0; i < hidden.get(hidden.size() - 1).size() ; i++) {
				for (int j = 0; j < outputs.size(); j++) {
					pro.line( hidden.get(hidden.size() - 1).get(i).x , hidden.get(hidden.size() - 1).get(i).y, outputs.get(j).x, outputs.get(j).y);
				}
			}
			
			// Draw Circles
			double[] inputColours = animal.nArgs;
			double[] outputColours = animal.weighOptions;
			
			for (int i = 0; i < inputs.size(); i++) {
				pro.fill((int) (inputColours[i] * 255));
				pro.circle(inputs.get(i).x, inputs.get(i).y, diameter);
			}
			
			for (int i = 0; i < hidden.size(); i++) {
				for (int j = 0; j < hidden.get(i).size(); j++) {
					//pro.fill((int) (inputColours[i] * 255));
					pro.fill(255);
					pro.circle(hidden.get(i).get(j).x, hidden.get(i).get(j).y, (diameter / 2));
				}
			}
			
			for (int i = 0; i < outputs.size(); i++) {
				pro.fill((int) (outputColours[i] * 255));
				pro.circle(outputs.get(i).x, outputs.get(i).y, diameter);
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
