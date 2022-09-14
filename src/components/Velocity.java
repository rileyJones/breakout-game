package components;

public class Velocity extends Component {
	public float x, y;
	public Velocity(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public TRAIT ID() {
		return TRAIT.VELOCITY;
	}
}
