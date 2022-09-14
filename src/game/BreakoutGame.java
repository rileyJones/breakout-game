package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class BreakoutGame extends StateBasedGame{

	public BreakoutGame(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// TODO Auto-generated method stub
		
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
