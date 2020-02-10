import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PImage;

public class ImageManager {
	
	public ArrayList<PImage> animalImages = new ArrayList<PImage>();
	public PImage eggImage;
	PApplet pro;
	
	
	ImageManager(PApplet pro){
		this.pro = pro;
		//animalImage = pro.loadImage("Animals/Rabbit.png");
		eggImage = pro.loadImage("Egg.png");
	}
	
	public PImage addAnimalImage(String file) {
		try {
			animalImages.add(pro.loadImage(file));
			return animalImages.get(animalImages.size() -1);
		} catch(Exception e) {
			animalImages.add(pro.loadImage("Animals/Rabbit.png"));
		}
		return animalImages.get(animalImages.size() - 1);
	}

}
