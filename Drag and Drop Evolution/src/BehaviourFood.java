
public interface BehaviourFood {
	boolean doesEatMeat();
	boolean doesEatPlants();
	float hungerRate();
}

class carnivorous implements BehaviourFood{
	public boolean doesEatMeat() {
		return true;
	}
	
	public boolean doesEatPlants() {
		return false;
	}
	
	public float hungerRate() {
		return 0.7f;
	}
}

class herbivorous implements BehaviourFood{
	public boolean doesEatMeat() {
		return false;
	}
	
	public boolean doesEatPlants() {
		return true;
	}
	
	public float hungerRate() {
		return 1;
	}
}

class omnivorous implements BehaviourFood{
	public boolean doesEatMeat() {
		return true;
	}
	
	public boolean doesEatPlants() {
		return true;
	}
	
	public float hungerRate() {
		return 1.3f;
	}
}