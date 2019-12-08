import java.util.ArrayList;

import org.gicentre.utils.geom.HashGrid;

import processing.core.PApplet;
import processing.core.PVector;

public class Egg extends EnvironmentObject {
	Gene gene;
	int timeTillHatch = 5000;
	
	
	Egg(PApplet pro, Environment env, ArrayList<Animal> animals, ArrayList<Food> foodArray,
			HashGrid<EnvironmentObject> hashGrid, ImageManager imageManager, Gene parentGene, PVector pos) {
		super(pro, env, animals, foodArray, hashGrid, imageManager);
		position.x = pos.x;
		position.y = pos.y;
		//hashGrid.add(this);
		
		// Mutate genes
		this.gene = new Gene(parentGene);
		this.colour = gene.colour;
		width = (float) gene.size;
	}
	
	public boolean hatch(int lastLoopTime) {
		timeTillHatch = timeTillHatch - lastLoopTime;
		if(timeTillHatch < 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void show() {
		// Image selected
		pro.tint(colour.r, colour.g, colour.b, 255);
		pro.image(imageManager.eggImage, position.x, position.y, width, width);
		pro.noTint();
	}
	
	
}
