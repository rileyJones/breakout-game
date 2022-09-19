package components;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class AnimComponent extends Component{
	Animation currentAnimation;
	SpriteSheet sprites;
	int currentIndex;
	int currentSpeed;
	int currentWidth;
	public AnimComponent(SpriteSheet sprites) {
		this.sprites = sprites;
		currentIndex = -1;
		currentSpeed = -1;
		currentWidth = -1;
		switchAnimation(0, 500, 1);
	}
	private void switchAnimation(int index, int updateSpeed, int width) {
		if(index != currentIndex || updateSpeed != currentSpeed || width != currentWidth) {
			currentAnimation = new Animation(sprites, 0, index, width-1, index, true, updateSpeed, true);
			currentIndex = index;
			currentSpeed = updateSpeed;
			currentWidth = width;
		}
	}
	public void render(Graphics g, int posX, int posY, float scale) {
		currentAnimation.getCurrentFrame().draw(posX, posY, scale);
	}
	public void update(int delta,AI thisAI) {
		int[] data = thisAI.getAnimNum();
		switchAnimation(data[0], data[1], data[2]);
		currentAnimation.update(delta);
	}
	
	@Override
	public TRAIT ID() {
		// TODO Auto-generated method stub
		return TRAIT.ANIMATION;
	}
	
	@Override
	public Component clone() {
		// TODO Auto-generated method stub
		return new AnimComponent(sprites);
	}
	
	
}
