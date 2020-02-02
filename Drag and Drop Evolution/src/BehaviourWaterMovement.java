
public interface BehaviourWaterMovement {
	float getWaterHunger(float hunger);
	float getWaterMovement(float movement, boolean inWater);
}

class hydrophile implements BehaviourWaterMovement {

	public float getWaterHunger(float hunger) {
		return hunger;
	}
	
	public float getWaterMovement(float movement, boolean inWater) {
		if(inWater)
			return movement * 1.5f;
		else
			return movement * 0.3f;
	}
}

class hydrophobe implements BehaviourWaterMovement {
	
	public float getWaterHunger(float hunger) {
		return hunger * 1.5f;
	}
	
	public float getWaterMovement(float movement, boolean inWater) {
		if(!inWater)
			return movement * 1.5f;
		else
			return movement * 0.3f;
	}
}

class amphibious implements BehaviourWaterMovement {
	
	public float getWaterHunger(float hunger) {
		return hunger;
	}
	
	public float getWaterMovement(float movement, boolean inWater) {
		return movement;
	}
}