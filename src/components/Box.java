package components;

import org.newdawn.slick.geom.Rectangle;

public class Box extends Component{
	public Rectangle r;
	public Box(float x, float y, float width, float height) {
		r = new Rectangle(x, y, width, height);
	}
	public Box(Rectangle r) {
		this.r = r;
	}
	
	public TRAIT ID() {
		return TRAIT.BOX;
	}
	
}
