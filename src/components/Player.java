package components;

import java.util.NoSuchElementException;

import controller.Controller;
import etc.Result;
import game.BreakoutGame;

public class Player extends AI{
	private Controller c;
	private final float walkSpeed = 0.25f;
	
	public Player(Controller c) {
		this.c = c;
	}

	@Override
	public void update(Entity self) {
//		Result<Component, NoSuchElementException> boxR = self.getTraitByID(TRAIT.BOX);
//		if(boxR.is_err()) return;
//		Box box = (Box)boxR.unwrap();
		Result<Component, NoSuchElementException> velR = self.getTraitByID(TRAIT.VELOCITY);
		if(velR.is_err()) return;
		Velocity vel = (Velocity)velR.unwrap();
		if(c.buttonPressed(BreakoutGame.KEY_LEFT)) {
			vel.x = -walkSpeed;
		}
		if(c.buttonPressed(BreakoutGame.KEY_RIGHT)) {
			vel.x = walkSpeed;
		}
		if(!c.buttonHeld(BreakoutGame.KEY_LEFT) && !c.buttonHeld(BreakoutGame.KEY_RIGHT)) {
			vel.x = 0f;
		}
	}
}
