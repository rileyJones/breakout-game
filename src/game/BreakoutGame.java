package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import components.Box;
import components.Velocity;
import controller.Controller;
import physics.Physics;

public class BreakoutGame extends StateBasedGame{

	public static final int KEY_LEFT  = 0;
	public static final int KEY_DOWN  = 1;
	public static final int KEY_UP    = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_SHOOT = 4;
	public static final int KEY_ACT   = 5;
	public static final int KEY_PAUSE = 6;
	Controller controller;
	
	Box aBox;
	Velocity aVel;
	Box bBox;
	Velocity bVel;
	
	
	public BreakoutGame(String name) {
		super(name);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		controller = new Controller(new int[] {
				Input.KEY_LEFT, Input.KEY_DOWN, Input.KEY_UP, Input.KEY_RIGHT, Input.KEY_Z, Input.KEY_X, Input.KEY_ENTER
				});
		aBox = new Box(10,10,100,100);
		bBox = new Box(container.getWidth()/2.0f, container.getHeight()/2.0f, 100, 100);
		aVel = new Velocity(0,0);
		bVel = new Velocity(0,0);
	}
	
	@Override
	protected void preUpdateState(GameContainer container, int delta) throws SlickException {
		super.preUpdateState(container, delta);
		controller.update(container.getInput(), delta);
		if(controller.buttonHeld(KEY_UP)) {
			aVel.y = -0.1f;
		} else if(controller.buttonHeld(KEY_DOWN)) {
			aVel.y =  0.1f;
		} else {
			aVel.y = 0.0f;
		}
		if(controller.buttonHeld(KEY_LEFT)) {
			aVel.x = -0.1f;
		} else if(controller.buttonHeld(KEY_RIGHT)) {
			aVel.x = 0.1f;
		} else {
			aVel.x = 0.0f;
		}
		Physics.doVelocity(aBox, aVel, 0, 0, delta);
		if(aBox.r.intersects(bBox.r)) {
			int tempDelta = Physics.getBoxCollideDelta(aBox, aVel, bBox, bVel);
			System.out.println(tempDelta);
			Physics.doVelocity(aBox, aVel, 0, 0, tempDelta);
		}
	}
	
	@Override
	protected void preRenderState(GameContainer container, Graphics g) throws SlickException {
		super.preRenderState(container, g);
		g.setBackground(Color.black);
		g.setColor(Color.white);
		g.draw(aBox.r);
		g.draw(bBox.r);
		g.setColor(Color.yellow);
		g.fill(aBox.r);
		g.setColor(Color.blue);
		g.fill(bBox.r);
	}
	
	
	public static void main(String[] args) {
		try {
			AppGameContainer app = new AppGameContainer(new BreakoutGame("Breakout - Riley Jones"));
			app.setDisplayMode(896, 672, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
