package states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class LevelSelectState extends BasicGameState {

	int level;
	boolean enterPressed;
	boolean slashPressed;
	boolean slashNotPressed;
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {		
	}
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		level = 0;
		enterPressed = false;
		slashPressed = false;
		slashNotPressed = false;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(enterPressed) {
			((GameState)game.getState(0)).level = level;
			game.enterState(0);
		} else if(slashPressed && slashNotPressed) {
			game.enterState(0);
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawString(""+level, container.getWidth()/2f, container.getHeight()/2f);
	}
	
	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		if(key == Input.KEY_ENTER) {
			enterPressed = true;
		} else if(key == Input.KEY_SLASH) {
			slashPressed = true;
		}else if(key == Input.KEY_BACK) {
			level /= 10;
		}
		if(c >= '0' && c <= '9') {
			level = level*10+c-'0';
		}
	}
	@Override
	public void keyReleased(int key, char c) {
		super.keyReleased(key, c);
		if(key == Input.KEY_SLASH && slashPressed) {
			slashNotPressed = true;
		}
	}

	@Override
	public int getID() {
		return 3;
	}

}
