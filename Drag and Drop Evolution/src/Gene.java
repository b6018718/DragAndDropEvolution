import basicneuralnetwork.NeuralNetwork;
import basicneuralnetwork.activationfunctions.ActivationFunction;

public class Gene {
	double size;
	double speed;
	double lifeSpan;
	Color colour = new Color();
	NeuralNetwork neuralNetwork;
	// 5% chance of mutation
	private double mutationChance = 0.5;
	// 20% variation spread per mutation (-/+)
	private double mutationRate = 0.15;
	private double behaviourMutationRate = 0.1;
	// If mutation is allowed
	public boolean mutate = true;
	public Species species;
	
	public void generateRandomNetwork() {
		neuralNetwork =  new NeuralNetwork(8, 2, 7, 4);
	}
	
	Gene(Gene parent, Species species){
		this.species = species;
		if (parent == null) {
			//behaveSpeed = new slow();
			//behaveSpeed = new dynamicSpeed();
			//behaveSpeed = new fast();
			
			//behaveSize = new big();
			//behaveSize = new dynamicSize();
			//behaveSize = new small();
			
			//behaveLifespan = new shortLife();
			//behaveLifespan = new dynamicLifespan();
			//behaveLifespan = new longLife();
			
			//behaveWaterMove = new hydrophile();
			//behaveWaterMove = new hydrophobe();
			//behaveWaterMove = new amphibious();
			
			size = 12;
			speed = 50;
			lifeSpan = 60000;
			colour = null;
			generateRandomNetwork();
			neuralNetwork.setActivationFunction(ActivationFunction.RELU);
			//neuralNetwork.setLearningRate(2);
		} else {
			
			mutate = parent.mutate;
			// If no mutation, generate a random new network
			if(mutate)
				neuralNetwork = parent.neuralNetwork.copy();
			else 
				generateRandomNetwork();
			// Perform mutations
			if(random(0,1) <= mutationChance && mutate) {
				// Genes can mutate
				size = applyMutation(parent.size);
				speed = applyMutation(parent.speed);
				lifeSpan = applyMutation(parent.lifeSpan);
				mutateColor(parent.colour);
				neuralNetwork.mutate(behaviourMutationRate);
			} else {
				// No mutation occurred
				size = parent.size;
				speed = parent.speed;
				lifeSpan = parent.lifeSpan;
				colour.r = parent.colour.r;
				colour.g = parent.colour.g;
				colour.b = parent.colour.b;
			}
		}
		
		if(size > 24) {
			size = 24;
		}
		
		speed = species.behaveSpeed.getSpeed(speed);
		size = species.behaveSize.getSize(size);
		lifeSpan = species.behaveLifespan.getLifespan(lifeSpan);
	}
	
	private void mutateColor(Color parentColour) {
		//double redShift = randomColourMutation();
		//double greenShift = randomColourMutation();
		//double blueShift = randomColourMutation();
		
		//colour.r = preventColourOverflow((int) (parentColour.r * redShift));
		//colour.g = preventColourOverflow((int) (parentColour.g * greenShift));
		//colour.b = preventColourOverflow((int) (parentColour.b * blueShift));
		colour.r = (int) random(0, 255);
		colour.g = (int) random(0,255);
		colour.b = (int) random(0, 255);
	}
	
	private double randomMutationChange() {
		return random(1-mutationRate, 1+mutationRate);
	}
	
	private double randomColourMutation() {
		double colourShiftMultiplier = 5;
		return random((1-mutationRate) * colourShiftMultiplier, (1+mutationRate) * colourShiftMultiplier);
	}
	
	private int preventColourOverflow(int colour) {
		if(colour < 0)
			return 0;
		if(colour > 255)
			return 255;
		return colour;
	}
	
	private double applyMutation(double parentGenome) {
		double newValue = parentGenome * randomMutationChange();
		if(newValue < 1)
			newValue = 1;
		return newValue;
	}
	
	private double random(double min, double max) {
	    return (double) (min + (Math.random() * (max - min)));
	}
}
