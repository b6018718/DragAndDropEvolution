
public interface BehaviourSize {
	double getSize(double size);
}

class big implements BehaviourSize{
	double maxSize = 20;
	public double getSize(double size) {
		if(size < maxSize)
			return maxSize;
		else
			return size;
	}
}

class small implements BehaviourSize{
	double minSize = 8;
	public double getSize(double size) {
		if(size > minSize)
			return minSize;
		else
			return size;
	}
}

class dynamicSize implements BehaviourSize{
	public double getSize(double size) {
		return size;
	}
}