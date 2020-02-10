
public interface BehaviourWaterMovement {
	float getWaterMovement(float movement, boolean inWater);
	float getHunger(float movement, boolean inWater);
}

class hydrophile implements BehaviourWaterMovement {
	
	public float getHunger(float hunger, boolean inWater) {
		if(inWater)
			return hunger;
		else
			return hunger * 4;
	}
	
	public float getWaterMovement(float movement, boolean inWater) {
		if(inWater)
			return movement * 1.25f;
		else
			return movement * 0.3f;
	}
}

class hydrophobe implements BehaviourWaterMovement {
	
	public float getHunger(float hunger, boolean inWater) {
		if(!inWater)
			return hunger;
		else
			return hunger * 4;
	}
	
	public float getWaterMovement(float movement, boolean inWater) {
		if(!inWater)
			return movement * 1.25f;
		else
			return movement * 0.3f;
	}
}

class amphibious implements BehaviourWaterMovement {
	
	public float getHunger(float hunger, boolean inWater) {
		return hunger;
	}
	
	public float getWaterMovement(float movement, boolean inWater) {
		return movement;
	}
}