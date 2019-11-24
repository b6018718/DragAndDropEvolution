import processing.core.PApplet;
import processing.core.PImage;

public class ImageManager {
	
	public PImage animalImage;
	public PImage eggImage;
	
	ImageManager(PApplet pro){
		animalImage = pro.loadImage("Rabbit.png");
		eggImage = pro.loadImage("Egg.png");
	}

}
