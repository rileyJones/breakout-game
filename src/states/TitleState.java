package states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;


import controller.Controller;
import game.BreakoutGame;

public class TitleState extends BasicGameState{

	Controller controller;
	int timer;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		timer = 0;
		controller = ((BreakoutGame)game).controller;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer += delta;
		if(controller.buttonPressed(BreakoutGame.KEY_PAUSE)) {
			game.enterState(0, new EmptyTransition(), new FadeInTransition());
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		float radius = Math.min(1,Math.max(0,(timer-500)/5000f));
		Circle spotLight = new Circle(container.getWidth()/2f, container.getHeight()/2f, radius*container.getWidth()/2f-1);
		Polygon screen = new Polygon(new float[] {
				0,0, container.getWidth()/2f-5,0, container.getWidth()/2f,container.getHeight()/2f,
				container.getWidth()/2f+5,0, container.getWidth(),0, container.getWidth(),container.getHeight(),
				0,container.getHeight()
		});
		g.setColor(Color.lightGray);
		g.fill(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
		g.setColor(Color.orange);
		g.scale(10, 10);
		g.drawString("BREAKOUT", container.getWidth()/20f-container.getWidth()*13/320f, container.getHeight()/40f);
		g.scale(0.2f,0.2f);
		g.drawString("Riley Jones", container.getWidth()/4f-container.getWidth()/16f, container.getHeight()/4f+container.getHeight()/16f);
		g.scale(0.5f,0.5f);
		g.setColor(Color.black);
		if(timer > 6500 && (timer/400)%2==1) {
			g.drawString("press enter to begin", container.getWidth()/2f-container.getWidth()*1/8f+13, 46+container.getHeight()*2/3f);
		}
		g.setColor(new Color(0xba4712));
		for(Shape s: screen.subtract(spotLight)) {
			g.fill(s);
		}
		g.fill(new Rectangle(0,0,container.getWidth(), container.getHeight()/2f - radius*container.getWidth()/2f+1));
		
	}

	@Override
	public int getID() {
		return 2;
	}

}
