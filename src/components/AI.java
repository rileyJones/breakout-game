package components;

public abstract class AI extends Component{
	
	public abstract void update(Entity self, int delta);
	
	@Override
	public TRAIT ID() {
		return TRAIT.AI;
	}

}
