package states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

import controller.Controller;
import game.BreakoutGame;

public class GameOverState extends BasicGameState{
	int timer;
	Controller controller;
	Shape fill;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		controller = ((BreakoutGame)game).controller;
		fill = new Rectangle(0,0,container.getWidth(), container.getHeight());
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		timer = 1000;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer -= delta;
		if(controller.buttonPressed(BreakoutGame.KEY_PAUSE)) {
			game.enterState(0, new EmptyTransition(), new HorizontalSplitTransition());
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		

		float fadeEffect = Math.min(1,(1000-timer)/2000f);
		g.setColor(new Color((int)(fadeEffect*0xa5),(int)(fadeEffect*0x4e),(int)(fadeEffect*0x33)));
		g.fill(fill);
		g.setColor(Color.white);
		g.scale(2, 2);
		g.drawString("GAME OVER", container.getWidth()/4f-container.getWidth()*1/18f+6, container.getHeight()/4f);
		if(timer < 0) {
			g.scale(0.5f,0.5f);
			g.drawString("press enter to continue?", container.getWidth()/2f-container.getWidth()*1/8f, 46+container.getHeight()/2f);
		}
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 1;
	}


}
