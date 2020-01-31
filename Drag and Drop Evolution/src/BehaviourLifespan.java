
public interface BehaviourLifespan {
	double getLifespan(double lifespan);
}

class longLife implements BehaviourLifespan{
	double lifespanMin = 80000;
	public double getLifespan(double lifespan) {
		if(lifespan < lifespanMin)
			return lifespanMin;
		else
			return lifespan;
	}
}

class shortLife implements BehaviourLifespan{
	double lifespanMin = 30000;
	public double getLifespan(double lifespan) {
		if(lifespan > lifespanMin)
			return lifespanMin;
		else
			return lifespan;
	}
}

class dynamicLifespan implements BehaviourLifespan{
	public double getLifespan(double lifespan) {
		return lifespan;
	}
}
