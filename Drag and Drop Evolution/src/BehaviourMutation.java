
public interface BehaviourMutation {
	double getBehaviourMutationRate();
	double getPhysicalMutationRate();
}

class highMutation implements BehaviourMutation{
	public double getBehaviourMutationRate() {
		return 0.2;
	}
	public double getPhysicalMutationRate() {
		return 0.25;
	}
}

class lowMutation implements BehaviourMutation{
	public double getBehaviourMutationRate() {
		return 0.01;
	}
	public double getPhysicalMutationRate() {
		return 0.05;
	}
}

class mediumMutation implements BehaviourMutation{
	public double getBehaviourMutationRate() {
		return 0.1;
	}
	public double getPhysicalMutationRate() {
		return 0.15;
	}
}