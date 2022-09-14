package components;

enum TRAIT{
	VELOCITY,
	BOX
}

public abstract class Component {
	public abstract TRAIT ID();
}
