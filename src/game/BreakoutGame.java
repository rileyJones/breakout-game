package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import components.Box;
import components.Mass;
import components.Velocity;
import controller.Controller;
import physics.Physics;
import etc.Common;

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
	Mass aMass;
	Box bBox;
	Velocity bVel;
	Mass bMass;
	Velocity pVel;
	Box cBox;
	Velocity cVel;
	Mass cMass;
	
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
		bVel = new Velocity(-0.0f,0);
		aMass = new Mass(1.0f);
		bMass = new Mass(1.0f);
		pVel = new Velocity(0,0);
		cBox = new Box(0, container.getHeight()*15/16f, container.getWidth(), container.getHeight()/16f);
		cVel = new Velocity(0,0);
		cMass = new Mass(-1);
	}
	
	@Override
	protected void preUpdateState(GameContainer container, int delta) throws SlickException {
		super.preUpdateState(container, delta);
		controller.update(container.getInput(), delta);
		if(controller.buttonHeld(KEY_UP)) {
			pVel.y = -0.1f;
		} else if(controller.buttonHeld(KEY_DOWN)) {
			pVel.y =  0.1f;
		} else {
			pVel.y = 0.0f;
		}
		if(controller.buttonHeld(KEY_LEFT)) {
			pVel.x = -0.1f;
		} else if(controller.buttonHeld(KEY_RIGHT)) {
			pVel.x = 0.1f;
		} else {
			pVel.x = 0.0f;
		}
		Physics.doVelocity(aBox, aVel, 0, 0, delta);
		Physics.doVelocity(aBox, pVel, 0, 0, delta);
		Physics.doVelocity(bBox, bVel, 0, 0, delta);
		Physics.doAcceleration(aBox, aVel, 0.0001f*-Common.sign(aVel.x), 0.0001f*-Common.sign(aVel.y), delta);
		//Physics.doAcceleration(bBox, bVel, 0.0001f*-Common.sign(bVel.x), 0.0001f*-Common.sign(bVel.y), delta);
		Physics.doAcceleration(bBox, bVel, 0, 0.0001f, delta);
		aVel.add(pVel);
		if(aBox.r.intersects(bBox.r)) {
			System.out.print("HERE: ");
			Physics.doInelasticCollision(aBox, aVel, aMass, bBox, bVel, bMass, delta, 0.9f);
		}
		if(cBox.r.intersects(bBox.r)) {
			Physics.doInelasticCollision(cBox, cVel, cMass, bBox, bVel, bMass, delta, 0.9f);
		}
		aVel.subtract(pVel);
		//System.out.println(aVel.x);
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
		g.setColor(Color.green);
		g.fill(cBox.r);
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
