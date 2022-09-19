package components;

import org.newdawn.slick.geom.Rectangle;

public class Box extends Component{
	public Rectangle r;
	public Box(float x, float y, float width, float height) {
		r = new Rectangle(x+0.001f, y+0.001f, width-0.002f, height-0.002f);
	}
	public Box(Rectangle r) {
		this.r = r;
	}
	
	public TRAIT ID() {
		return TRAIT.BOX;
	}
	@Override
	public Component clone() {
		return new Box(new Rectangle(r.getX(), r.getY(), r.getWidth(), r.getHeight()));
	}
	
}
