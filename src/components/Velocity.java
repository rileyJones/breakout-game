package components;

public class Velocity extends Component {
	public float x, y;
	public Velocity(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Velocity(Velocity a, Velocity b) {
		this.x = a.x+b.x;
		this.y = a.y+b.y;
	}
	public TRAIT ID() {
		return TRAIT.VELOCITY;
	}
	
	public void add(Velocity a) {
		this.x += a.x;
		this.y += a.y;
	}
	public void subtract(Velocity a) {
		this.x -= a.x;
		this.y -= a.y;
	}
}
