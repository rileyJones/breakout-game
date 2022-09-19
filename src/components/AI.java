package components;

public abstract class AI extends Component{
	
	public abstract void update(Entity self, int delta);
	
	public abstract int[] getAnimNum();
	
	@Override
	public TRAIT ID() {
		return TRAIT.AI;
	}

}
