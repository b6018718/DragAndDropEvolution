import g4p_controls.GImageButton;
import processing.core.PApplet;

public class AnimalIcon {
	GImageButton imageButton;
	Species species;
	
	AnimalIcon(PApplet pro, Species species, float p0, float p1, float p2, float p3){
		this.species = species;
		String[] files = new String [] {species.imageFilePath , species.imageFilePath, species.imageFilePath};
		imageButton = new GImageButton(pro, p0, p1, p2, p3, files);
	}
}
