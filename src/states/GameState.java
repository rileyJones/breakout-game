package states;

import java.util.NoSuchElementException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import components.*;
import etc.Common;
import etc.Result;
import game.BreakoutGame;
import physics.Physics;

public class GameState extends BasicGameState{

	Entity player;
	Entity ball;
	Entity[] wallEntities;
	float timer;
	private final float launchPower = 0.9f;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO Auto-generated method stub
		player = new Entity(new Component[] {
			new Player(((BreakoutGame)game).controller),
			new Box(container.getWidth()/3f, container.getHeight()-3*8-32, 24, 32),
			new Velocity(0,0)
		});
		ball = new Entity(new Component[] {
			new Box(container.getWidth()/2f, container.getHeight()/2f, 24, 24),	
			new Velocity(0,0),
			new Mass(1f)
		});
		wallEntities = new Entity[] {
			new Entity(new Component[] {
				new Box(0,0,3*8,container.getHeight()),
				new Velocity(0,0),
				new Mass(-1f)
			}),
			new Entity(new Component[] {
				new Box(container.getWidth()-3*8,0,3*8,container.getHeight()),
				new Velocity(0,0),
				new Mass(-1f)
			}),
			new Entity(new Component[] {
				new Box(0,container.getHeight()-3*8,container.getWidth(),3*8),
				new Velocity(0,0),
				new Mass(-1f)
			}),
			new Entity(new Component[] {
				new Box(0,0,container.getWidth(),3*8*5),
				new Velocity(0,0),
				new Mass(-1f)
			}),
		};
		timer = 999;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer -= delta/87f;
		Result<Component, NoSuchElementException> playerAI_R = player.getTraitByID(TRAIT.AI);
		if(playerAI_R.is_ok()) {
			AI playerAI = (AI)playerAI_R.unwrap();
			playerAI.update(player);
		}
		Result<Component, NoSuchElementException> playerVel_R = player.getTraitByID(TRAIT.VELOCITY);
		Result<Component, NoSuchElementException> playerBox_R = player.getTraitByID(TRAIT.BOX);
		if(playerBox_R.is_ok() && playerVel_R.is_ok()) {
			Box playerBox = (Box)playerBox_R.unwrap();
			Velocity playerVel = (Velocity)playerVel_R.unwrap();
			Physics.doVelocity(playerBox, playerVel, 0, 0, delta);
			for(Entity e: wallEntities) {
				Result<Component, NoSuchElementException> eBox_R = e.getTraitByID(TRAIT.BOX);
				Result<Component, NoSuchElementException> eVel_R = e.getTraitByID(TRAIT.VELOCITY);
				if(eBox_R.is_ok() && eVel_R.is_ok()) {
					Box eBox = (Box)eBox_R.unwrap();
					Velocity eVel = (Velocity)eVel_R.unwrap();
					if(playerBox.r.intersects(eBox.r)) {
						Physics.doSimpleCollision(eBox, eVel, playerBox, playerVel, delta);
					}
				}
			}
		}
		Result<Component, NoSuchElementException> ballBox_R = ball.getTraitByID(TRAIT.BOX);
		Result<Component, NoSuchElementException> ballVel_R = ball.getTraitByID(TRAIT.VELOCITY);
		Result<Component, NoSuchElementException> ballMass_R = ball.getTraitByID(TRAIT.MASS);
		if(ballBox_R.is_ok() && ballVel_R.is_ok() && ballMass_R.is_ok()) {
			Box ballBox = (Box)ballBox_R.unwrap();
			Velocity ballVel = (Velocity)ballVel_R.unwrap();
			Mass ballMass = (Mass)ballMass_R.unwrap();
			ballVel.normaliseX(1.0f,-1.0f);
			ballVel.normaliseY(1.5f,-1.5f);
			Physics.doVelocity(ballBox, ballVel, 0, 0, delta);
			Physics.doAcceleration(ballBox, ballVel, -0.0001f*Common.sign(ballVel.x), 0.01f, delta);
			for(Entity e: wallEntities) {
				Result<Component, NoSuchElementException> eBox_R = e.getTraitByID(TRAIT.BOX);
				Result<Component, NoSuchElementException> eVel_R = e.getTraitByID(TRAIT.VELOCITY);
				Result<Component, NoSuchElementException> eMass_R = e.getTraitByID(TRAIT.MASS);
				if(eBox_R.is_ok() && eVel_R.is_ok() && eMass_R.is_ok()) {
					Box eBox = (Box)eBox_R.unwrap();
					Velocity eVel = (Velocity)eVel_R.unwrap();
					Mass eMass = (Mass)eMass_R.unwrap();
					if(ballBox.r.intersects(eBox.r)) {
						if(timer > 0 || eBox.r.getMinY() == 0) {
							Physics.doInelasticCollision(eBox, eVel, eMass, ballBox, ballVel, ballMass, delta, 0.98f);
						}
					}
				}
			}
			if(playerBox_R.is_ok()) {
				Box playerBox = (Box)playerBox_R.unwrap();
				if(playerBox.r.intersects(ballBox.r)) {
					Vector2f intersectDir = new Vector2f(ballBox.r.getCenterX()-playerBox.r.getCenterX(), 1.4f*(ballBox.r.getCenterY()-playerBox.r.getMaxY()));
					intersectDir.normalise();
					intersectDir.scale(launchPower);
					ballVel.set(intersectDir.x-ballVel.x/2f, intersectDir.y-ballVel.y/2f);
					System.out.println(ballVel.y);
				}
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawString(""+Math.max(0,(int)timer), 32, 32);
		Result<Component, NoSuchElementException> playerRect = player.getTraitByID(TRAIT.BOX);
		if(playerRect.is_ok()) {
			g.draw(((Box)playerRect.unwrap()).r);
		}
		Result<Component, NoSuchElementException> ballRect = ball.getTraitByID(TRAIT.BOX);
		if(ballRect.is_ok()) {
			g.draw(((Box)ballRect.unwrap()).r);
		}
		for(Entity e: wallEntities) {
			Result<Component, NoSuchElementException> eBox_R = e.getTraitByID(TRAIT.BOX);
			if(eBox_R.is_ok()) {
				Box eBox = (Box)eBox_R.unwrap();
				if(timer > 0 || eBox.r.getMinY() == 0) {
					g.draw(eBox.r);
				}
			}
		}
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}



}
