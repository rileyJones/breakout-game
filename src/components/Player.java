package components;

import java.util.NoSuchElementException;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import controller.Controller;
import etc.Result;
import game.BreakoutGame;

public class Player extends AI{
	private Controller c;
	private final float walkSpeed = 0.05f;
	private final float attackSpeed = 0.075f;
	public STATE currentState; 
	private int movementDirection;
	private final float attackTime = 0.5f;
	private int attackLayer;
	private int attackDirection;
	public Shape attack;
	public float upBias;
	
	float timer;
	
	public enum STATE {
		WALKING,
		ATTACKING
	};
	public Player(Controller c) {
		this.c = c;
		currentState = STATE.WALKING;
		movementDirection = 0;
		attackLayer = 0;
		attackDirection = 1;
		attack = null;
		upBias = 1.0f;
	}

	@Override
	public void update(Entity self, int delta) {
		keyMovement(self);
		switch(currentState) {
			case WALKING:
				walking(self, delta);
				break;
			case ATTACKING:
				attacking(self, delta);
				break;
		}
	}
	public void keyMovement(Entity self) {
		if(c.buttonPressed(BreakoutGame.KEY_LEFT)) {
			movementDirection -= 1;
		} else if(c.buttonReleased(BreakoutGame.KEY_LEFT)) {
			movementDirection += 1;
		}
		if(c.buttonPressed(BreakoutGame.KEY_RIGHT)) {
			movementDirection += 1;
		} else if(c.buttonReleased(BreakoutGame.KEY_RIGHT)) {
			movementDirection -= 1;
		}
		if(!c.buttonHeld(BreakoutGame.KEY_LEFT) && !c.buttonHeld(BreakoutGame.KEY_RIGHT)) {
			movementDirection = 0;
		}
	}
	public void walking(Entity self, int delta) {
		Result<Component, NoSuchElementException> velR = self.getTraitByID(TRAIT.VELOCITY);
		if(velR.is_err()) return;
		Velocity vel = (Velocity)velR.unwrap();
		Result<Component, NoSuchElementException> boxR = self.getTraitByID(TRAIT.BOX);
		if(boxR.is_err()) return;
		Box box = (Box)boxR.unwrap();
		vel.x = movementDirection * walkSpeed * delta;
		if(c.buttonPressed(BreakoutGame.KEY_ACT)) {
			currentState = STATE.ATTACKING;
			timer = attackTime;
			attackDirection = movementDirection;
			if(attackDirection == 0) {
				attackLayer = -1;
				attack = new Circle(box.r.getCenterX(), box.r.getMaxY(), box.r.getHeight()+4);
				upBias = 1.0f;
			} else {
				attackLayer = 0;
				attack = new Rectangle(box.r.getMinX() - 10, box.r.getMinY()-4, box.r.getWidth()+20, box.r.getHeight()+4);
				upBias = 0.9f;
			}
		}
	}
	
	public void attacking(Entity self, int delta) {
		timer -= delta/1000f;
		Result<Component, NoSuchElementException> velR = self.getTraitByID(TRAIT.VELOCITY);
		if(velR.is_err()) return;
		Velocity vel = (Velocity)velR.unwrap();
		Result<Component, NoSuchElementException> boxR = self.getTraitByID(TRAIT.BOX);
		if(boxR.is_err()) return;
		Box box = (Box)boxR.unwrap();
		vel.x = attackDirection * attackSpeed * delta;
		if(timer < 0) {
			currentState = STATE.WALKING;
			attack = null;
		}
		if(attack != null) {
			switch(attackLayer) {
				case -1:
					attack.setCenterX(box.r.getCenterX());
					attack.setCenterY(box.r.getMaxY());
					break;
				case 0:
				case 2:
					attack.setCenterX(box.r.getCenterX());
					attack.setCenterY(box.r.getCenterY());
					break;
				case 1:
					float minX = Math.min(attack.getMinX(), box.r.getMinX() - 10);
					float maxX = Math.max(attack.getMaxX(), box.r.getMaxX() + 10);
					((Rectangle)attack).setX(minX);
					((Rectangle)attack).setWidth(maxX-minX);
					break;
			}
		}
		if(c.buttonPressed(BreakoutGame.KEY_ACT) && attackLayer == 0 ) {
			if(movementDirection == -attackDirection) {
				timer = attackTime;
				attackDirection = 0;
				attackLayer = 2;
				attack = new Circle(box.r.getCenterX(), box.r.getCenterY()-4, box.r.getHeight()/2f+12);
				upBias = 2.0f;
			} else {
				timer = attackTime*2/3f;
				upBias = 1.0f;
				attackLayer = 1;
			}
		}
	}
}
