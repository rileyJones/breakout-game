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
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO Auto-generated method stub
		player = new Entity(new Component[] {
			new Player(((BreakoutGame)game).controller),
			new Box(container.getWidth()/2f, container.getHeight()-3*8*3, 16*3, 16*3),
			new Velocity(0,0)
			});
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
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}



}
