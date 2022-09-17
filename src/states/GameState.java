package states;

import java.util.NoSuchElementException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import components.*;
import etc.Result;
import game.BreakoutGame;
import physics.Physics;

public class GameState extends BasicGameState{

	Entity player;
	Entity ball;
	Entity[] wallEntities;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO Auto-generated method stub
		player = new Entity(new Component[] {
			new Player(((BreakoutGame)game).controller),
			new Box(container.getWidth()/2f, container.getHeight()-3*8*3, 16*3, 16*3),
			new Velocity(0,0)
			});
		wallEntities = new Entity[] {
			new Entity(new Component[] {
				new Box(0,0,3*8,container.getHeight()),
				new Velocity(0,0)
			}),
			new Entity(new Component[] {
				new Box(container.getWidth()-3*8,0,3*8,container.getHeight()),
				new Velocity(0,0)
			}),
			new Entity(new Component[] {
				new Box(0,container.getHeight()-3*8,container.getWidth(),3*8),
				new Velocity(0,0)
			}),
			new Entity(new Component[] {
				new Box(0,0,container.getWidth(),3*8*5),
				new Velocity(0,0)
			}),
		};
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
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
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		Result<Component, NoSuchElementException> playerRect = player.getTraitByID(TRAIT.BOX);
		if(playerRect.is_ok()) {
			g.draw(((Box)playerRect.unwrap()).r);
		}
		Result<Component, NoSuchElementException> ballRect = player.getTraitByID(TRAIT.BOX);
		if(ballRect.is_ok()) {
			g.draw(((Box)ballRect.unwrap()).r);
		}
		for(Entity e: wallEntities) {
			Result<Component, NoSuchElementException> eBox_R = e.getTraitByID(TRAIT.BOX);
			if(eBox_R.is_ok()) {
				Box eBox = (Box)eBox_R.unwrap();
				g.draw(eBox.r);
			}
		}
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}



}
