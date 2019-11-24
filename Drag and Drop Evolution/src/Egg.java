import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PVector;

public class Egg extends EnvironmentObject {
	Gene gene;
	double mutationRate = 0.05;
	int timeTillHatch;
	
	// For Speeding up from the UI
	float speedMultiplier = 1;
	
	Egg(PApplet pro, RectObj env, ArrayList<Animal> animals, ArrayList<Food> foodArray,
			HashGrid<EnvironmentObject> hashGrid, ImageManager imageManager, Gene gene, PVector pos) {
		super(pro, env, animals, foodArray, hashGrid, imageManager);
		position.x = pos.x;
		position.y = pos.y;
		this.colour = gene.colour;
		width = gene.size;
	}
	
	private void mutateGenes() {
		
	}
	
	private void mutateColorTint() {
		
	}
	
	@Override
	public void show() {
		// Image selected
		pro.tint(colour.r, colour.g, colour.b, 255);
		pro.image(imageManager.eggImage, position.x, position.y, width, width);
		pro.noTint();
	}
	
	
}
