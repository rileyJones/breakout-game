package components;

public abstract class AI extends Component{
	
	public abstract void update(Entity self);
	
	@Override
	public TRAIT ID() {
		return TRAIT.AI;
	}

}
