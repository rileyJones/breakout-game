package components;

enum TRAIT{
	VELOCITY,
	BOX,
	MASS
}

public abstract class Component {
	public abstract TRAIT ID();
}
