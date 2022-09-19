package states;

import java.util.NoSuchElementException;
import javax.xml.bind.ValidationException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import components.*;
import etc.Common;
import etc.Result;
import game.BreakoutGame;
import game.LevelList;
import game.LevelModel;
import physics.Physics;

public class GameState extends BasicGameState{

	Entity player;
	Entity ball;
	Entity[] wallEntities;
	float timer;
	private final float launchPower = 0.9f;
	int lives;
	int level;
	LevelModel lm;
	boolean pause;
	float deathAnimTimer;
	float invulnTimer;
	TrueTypeFont bigFont;
	
	
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		level = 1;
		java.awt.Font awtFont = new java.awt.Font("Serif", java.awt.Font.PLAIN, 60);
		bigFont =  new TrueTypeFont(awtFont, false);
		try {
			lm = new LevelModel(LevelList.get(level));
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}
	public void changeLevel(int level) {
		this.level = level;
		try {
			lm = new LevelModel(LevelList.get(level));
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		lives = 3;
		timer = 999;
		pause = true;
		invulnTimer = 0;
		deathAnimTimer = 0;
		player = new Entity(new Component[] {
			new Player(((BreakoutGame)game).controller),
			new Box(container.getWidth()/3f, container.getHeight()-3*8-32, 24, 32),
			new Velocity(0,0)
		});
		ball = new Entity(new Component[] {
			new Box(container.getWidth()/2f, container.getHeight()-3*8-32-8, 24, 24),	
			new Velocity(0,0),
			new Mass(1f)
		});
		wallEntities = new Entity[] {
			new Entity(new Component[] {
				new Box(0,0,4*8,container.getHeight()),
				new Velocity(0,0),
				new Mass(-1f)
			}),
			new Entity(new Component[] {
				new Box(container.getWidth()-4*8,0,4*8,container.getHeight()),
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
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(container.getInput().isKeyDown(Input.KEY_SLASH)) {
			game.enterState(3);	
		}
		deathAnimTimer -= delta/87f;
		invulnTimer -= delta/87f;
		Result<Component, NoSuchElementException> playerAI_R = player.getTraitByID(TRAIT.AI);
		if(playerAI_R.is_ok()) {
			AI playerAI = (AI)playerAI_R.unwrap();
			playerAI.update(player, delta);
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
		if(pause) {
			if(((BreakoutGame)game).controller.buttonPressed(BreakoutGame.KEY_ACT)) {
				pause = false;
			}
			return;
		}
		if(level <= LevelList.levelMax) {
			timer -= delta/87f;
		}
		Result<Component, NoSuchElementException> ballBox_R = ball.getTraitByID(TRAIT.BOX);
		Result<Component, NoSuchElementException> ballVel_R = ball.getTraitByID(TRAIT.VELOCITY);
		Result<Component, NoSuchElementException> ballMass_R = ball.getTraitByID(TRAIT.MASS);
		if(ballBox_R.is_ok() && ballVel_R.is_ok() && ballMass_R.is_ok()) {
			Box ballBox = (Box)ballBox_R.unwrap();
			Velocity ballVel = (Velocity)ballVel_R.unwrap();
			Mass ballMass = (Mass)ballMass_R.unwrap();
			lm.update(ball, delta);
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
							if(eBox.r.getCenterY() > container.getHeight()*2/3f) {
								Physics.assertDirectionInelasticCollision(Physics.DIRECTION.Y_MINUS, eBox, eVel, eMass, ballBox, ballVel, ballMass, delta, 0.98f);
							} else {
								Physics.doInelasticCollision(eBox, eVel, eMass, ballBox, ballVel, ballMass, delta, 0.98f);
							}
						}
					}
				}
			}
			if(playerAI_R.is_ok()) {
				Player playerAI = (Player)playerAI_R.unwrap();
				playerAI.shotList.removeIf(shot -> {
					Result<Component, NoSuchElementException> shotBox_R = shot.getTraitByID(TRAIT.BOX);
					if(shotBox_R.is_ok()) {
						Box shotBox = (Box)shotBox_R.unwrap();
						if(shotBox.r.intersects(ballBox.r)) {
							Vector2f intersectDir = new Vector2f(ballBox.r.getCenterX()-shotBox.r.getCenterX(),100f*(ballBox.r.getCenterY()-shotBox.r.getMaxY()));
							float factorX = (float) (intersectDir.x *0.03);
							intersectDir.normalise();
							intersectDir.y -= 1.0f;
							intersectDir.normalise();
							intersectDir.scale(0.08f);
							ballVel.set(factorX + ballVel.x/4f, intersectDir.y + ballVel.y/8f);
							return true;
						}
					}
					return false;
				});
			}
			
			if(playerBox_R.is_ok() && playerAI_R.is_ok()) {
				Box playerBox = (Box)playerBox_R.unwrap();
				Player playerAI = (Player)playerAI_R.unwrap();
				if(playerAI.currentState == Player.STATE.ATTACKING) {
					if(playerAI.attack != null) {
						if(playerAI.attack.intersects(ballBox.r)) {
							Vector2f intersectDir = new Vector2f(ballBox.r.getCenterX()-playerAI.attack.getCenterX(), playerAI.upBias * 1.4f*(ballBox.r.getCenterY()-playerBox.r.getMaxY()));
							intersectDir.normalise();
							intersectDir.scale(launchPower);
							ballVel.set(intersectDir.x-ballVel.x/2f, intersectDir.y-ballVel.y/2f);
						}
					}
				} else {
					if((playerBox.r.intersects(ballBox.r) || ballBox.r.getMinY() > container.getHeight()) && invulnTimer < 0) {
						timer = Math.max(300,timer);
						if(ballBox.r.getMinY() > container.getHeight()) {
							ball = new Entity(new Component[] {
									new Box(container.getWidth()/2f, container.getHeight()-3*8-32-8, 24, 24),	
									new Velocity(0,0),
									new Mass(1f)
								});
								pause = true;
						} else {
							ballVel.y -= 1;
						}
						if(level <= LevelList.levelMax && invulnTimer < 0) {
							lives--;
							invulnTimer = 20;
							deathAnimTimer = 10;
						}
						if(lives < 0) {
							game.enterState(1, new FadeOutTransition(), new EmptyTransition());
						}
					}
				}
			}
		}
		if(lm.getTileCount() == 0 && level <= LevelList.levelMax) {
			level++;
			try {
				lm = new LevelModel(LevelList.get(level));
			} catch (ValidationException e) {
				e.printStackTrace();
			}
			game.enterState(0, new FadeOutTransition(), new FadeInTransition());
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawString(""+Math.max(0,(int)timer), 32, 32);
		Result<Component, NoSuchElementException> playerAI = player.getTraitByID(TRAIT.AI);
		if(playerAI.is_ok()) {
			g.setColor(Color.green);
			if(((Player)playerAI.unwrap()).attack != null) {
				g.fill(((Player)playerAI.unwrap()).attack);
			}
			((Player)playerAI.unwrap()).shotList.forEach(shot -> {
				Result<Component, NoSuchElementException> shotBox_R = shot.getTraitByID(TRAIT.BOX);
				if(shotBox_R.is_ok()) {
					Box shotBox = (Box)shotBox_R.unwrap();
					g.fill(shotBox.r);
				}
			});
			g.setColor(Color.white);
		}
		
		Result<Component, NoSuchElementException> playerRect = player.getTraitByID(TRAIT.BOX);
		if(playerRect.is_ok()) {
			Rectangle playerBox = ((Box)playerRect.unwrap()).r;
			if(deathAnimTimer > 0) {
				g.setColor(Color.red);
				g.fill(new Circle(playerBox.getCenterX(), playerBox.getCenterY(), 200*(5-Math.abs(deathAnimTimer-5))+30));
			} else {
				if(invulnTimer > 0) {
					g.setColor(Color.cyan);
				}
				g.draw(playerBox);
			}
			g.setColor(Color.white);
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
		for(int i = 0; i < lives; i++) {
			g.fill(new Rectangle( 120+15*i, 30, 10, 15)); 
		}
		
		for(int x = 0; x < lm.maxX; x++) {
			for(int y = 0; y < lm.maxY; y++) {
				Color tempColor = lm.getColor(x,y);
				if(tempColor != null) {
					g.setColor(tempColor);
					g.fill(new Rectangle(lm.posX + x*lm.tileWidth, lm.posY + y*lm.tileHeight, lm.tileWidth, lm.tileHeight));
				}
			}
		}
		g.setColor(Color.white);
		g.drawString(LevelList.getName(level), container.getWidth()/2f, 16);
		if(pause && timer == 999) {
			org.newdawn.slick.Font oldFont = g.getFont();
			g.setFont(bigFont);
			g.scale(3,3);
			g.drawString(""+level, container.getWidth()/9f, container.getHeight()/8f);
			g.scale(1/3f,1/3f);
			g.setFont(oldFont);
		}
	}

	@Override
	public int getID() {
		return 0;
	}



}
