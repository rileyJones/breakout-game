package components;

public class Mass extends Component {
	public float m;
	
	public Mass(float m) {
		this.m = m;
	}
	
	@Override
	public TRAIT ID() {
		return TRAIT.MASS;
	}

}
