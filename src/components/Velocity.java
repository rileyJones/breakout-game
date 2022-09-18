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
	public Component clone() {
		return new Velocity(x, y);
	}
	
	public void add(Velocity a) {
		this.x += a.x;
		this.y += a.y;
	}
	public void subtract(Velocity a) {
		this.x -= a.x;
		this.y -= a.y;
	}
	public void set(Velocity a) {
		this.x = a.x;
		this.y = a.y;
	}
	public void add(float x2, float y2) {
		this.x += x2;
		this.y += y2;
	}
	public void subtract(float x2, float y2) {
		this.x -= x2;
		this.y -= y2;	
	}
	public void set(float x2, float y2) {
		this.x = x2;
		this.y = y2;
	}
	public void normaliseX(float max, float min) {
		if(this.x > max) {
			this.x = max;
		}
		if(this.x < min) {
			this.x = min;
		}
	}
	public void normaliseY(float max, float min) {
		if(this.y > max) {
			this.y = max;
		}
		if(this.y < min) {
			this.y = min;
		}
	}
}
