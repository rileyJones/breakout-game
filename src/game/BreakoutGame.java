package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import controller.Controller;
import states.GameOverState;
import states.GameState;
import states.LevelSelectState;
import states.TitleState;

public class BreakoutGame extends StateBasedGame{

	public static final int KEY_LEFT  = 0;
	public static final int KEY_DOWN  = 1;
	public static final int KEY_UP    = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_SHOOT = 4;
	public static final int KEY_ACT   = 5;
	public static final int KEY_PAUSE = 6;
	public Controller controller;
	
	public BreakoutGame(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		controller = new Controller(new int[] {
				Input.KEY_LEFT, Input.KEY_DOWN, Input.KEY_UP, Input.KEY_RIGHT, Input.KEY_Z, Input.KEY_X, Input.KEY_ENTER
				});
		addState(new TitleState());
		addState(new GameState());
		addState(new GameOverState());
		addState(new LevelSelectState());
	}
	
	@Override
	protected void preUpdateState(GameContainer container, int delta) throws SlickException {
		super.preUpdateState(container, delta);
		controller.update(container.getInput(), delta);
	}
	@Override
	protected void postRenderState(GameContainer container, Graphics g) throws SlickException {
		super.postRenderState(container, g);
		System.gc();
	}
	
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new BreakoutGame("Breakout - Riley Jones"));
			app.setDisplayMode(896, 672, false);
			int scaleX = 32;
			int scaleY = 32;
			System.out.println((896-4*8*2)/1f/scaleX);
			System.out.println((672-3*8*6)/1f/scaleY);
			//app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
