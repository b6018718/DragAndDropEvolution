
public interface BehaviourSpeed {
	double getSpeed(double initialSpeed);
}

class slow implements BehaviourSpeed{
	double slowSpeed = 25;
	public double getSpeed(double initialSpeed) {
		if(initialSpeed > slowSpeed)
			return slowSpeed;
		else
			return initialSpeed;
	}
}

class fast implements BehaviourSpeed{
	double fastSpeed = 60;
	public double getSpeed(double initialSpeed) {
		if(initialSpeed < fastSpeed)
			return fastSpeed;
		else
			return initialSpeed;
	}
}

class dynamicSpeed implements BehaviourSpeed{
	public double getSpeed(double initialSpeed) {
		return initialSpeed;
	}
}
