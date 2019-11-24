public class Gene {
	int size;
	int speed;
	int lifeSpan;
	Color colour;
	
	Gene(Gene parent){
		if (parent == null) {
			size = 12;
			speed = 50;
			lifeSpan = 60000;
			colour = null;
		}
	}
}
