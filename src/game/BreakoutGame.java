package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import controller.Controller;

public class BreakoutGame extends StateBasedGame{

	public static final int KEY_LEFT  = 0;
	public static final int KEY_DOWN  = 1;
	public static final int KEY_UP    = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_SHOOT = 4;
	public static final int KEY_ACT   = 5;
	public static final int KEY_PAUSE = 6;
	Controller controller;
	
	public BreakoutGame(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		controller = new Controller(new int[] {
				Input.KEY_LEFT, Input.KEY_DOWN, Input.KEY_UP, Input.KEY_RIGHT, Input.KEY_Z, Input.KEY_X, Input.KEY_ENTER
				});
	}
	
	@Override
	protected void preUpdateState(GameContainer container, int delta) throws SlickException {
		super.preUpdateState(container, delta);
		controller.update(container.getInput(), delta);
	}
	
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new BreakoutGame("Breakout - Riley Jones"));
			app.setDisplayMode(896, 672, false);
			//app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
