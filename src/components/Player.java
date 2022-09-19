package components;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import controller.Controller;
import etc.Result;
import game.BreakoutGame;
import physics.Physics;

public class Player extends AI{
	private Controller c;
	private final float walkSpeed = 0.05f*5;
	private final float attackSpeed = 0.075f*5;
	public STATE currentState; 
	private int movementDirection;
	private final float attackTime = 0.5f;
	private final float shootTime = 0.3f;
	private int attackLayer;
	private int attackDirection;
	public Shape attack;
	public float upBias;
	float timer;
	int reshotTimer;
	int timesReshot;
	
	Entity shotPrototype;
	public ArrayList<Entity> shotList; 
	
	public enum STATE {
		WALKING,
		ATTACKING,
		SHOOTING
	};
	public Player(Controller c) {
		this.c = c;
		currentState = STATE.WALKING;
		movementDirection = 0;
		attackLayer = 0;
		attackDirection = 1;
		attack = null;
		upBias = 1.0f;
		shotList = new ArrayList<Entity>();
		shotPrototype = new Entity( new Component[] {
			new Box(0,0,15,20),
			new Velocity(0, -1.0f)
		});
	}

	@Override
	public void update(Entity self, int delta) {
		keyMovement(self);
		updateShots(self, delta);
		timer -= delta/1000f;
		switch(currentState) {
			case WALKING:
				walking(self, delta);
				break;
			case ATTACKING:
				attacking(self, delta);
				break;
			case SHOOTING:
				shooting(self, delta);
				break;
		}
	}
	public void updateShots(Entity self, int delta) {
		shotList.removeIf(shot -> {
			Result<Component, NoSuchElementException> shotBox_R = shot.getTraitByID(TRAIT.BOX);
			Result<Component, NoSuchElementException> shotVel_R = shot.getTraitByID(TRAIT.VELOCITY);
			if(shotBox_R.is_ok() && shotVel_R.is_ok()) {
				Box shotBox = (Box)shotBox_R.unwrap();
				Velocity shotVel = (Velocity)shotVel_R.unwrap();
				Physics.doVelocity(shotBox, shotVel, 0, 0, delta);
				if(shotBox.r.getMaxX() < 0) {
					return true;
				}
				return false;
			}
			return true;
		});
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
		vel.x = movementDirection * walkSpeed;
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
		} if(c.buttonPressed(BreakoutGame.KEY_SHOOT)) {
			Entity newShot = shotPrototype.clone();
			Result<Component, NoSuchElementException> newShotBox_R = newShot.getTraitByID(TRAIT.BOX);
			if(newShotBox_R.is_ok()) {
				Box newShotBox = (Box)newShotBox_R.unwrap();
				newShotBox.r.setCenterX(box.r.getCenterX());
				newShotBox.r.setCenterY(box.r.getY()-newShotBox.r.getHeight());
				shotList.add(newShot);
			}
		}
	}
	
	public void attacking(Entity self, int delta) {
		Result<Component, NoSuchElementException> velR = self.getTraitByID(TRAIT.VELOCITY);
		if(velR.is_err()) return;
		Velocity vel = (Velocity)velR.unwrap();
		Result<Component, NoSuchElementException> boxR = self.getTraitByID(TRAIT.BOX);
		if(boxR.is_err()) return;
		Box box = (Box)boxR.unwrap();
		vel.x = attackDirection * attackSpeed;
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
		if(c.buttonPressed(BreakoutGame.KEY_SHOOT) && attackLayer == 0) {
			currentState = STATE.SHOOTING;
			attack = null;
			timer = shootTime;
			reshotTimer = 33;
			timesReshot = 25;
		}
	}
	public void shooting(Entity self, int delta) {
		Result<Component, NoSuchElementException> velR = self.getTraitByID(TRAIT.VELOCITY);
		if(velR.is_err()) return;
		Velocity vel = (Velocity)velR.unwrap();
		Result<Component, NoSuchElementException> boxR = self.getTraitByID(TRAIT.BOX);
		if(boxR.is_err()) return;
		Box box = (Box)boxR.unwrap();
		vel.x = 0;
		reshotTimer -= delta;
		if(timer < 0) {
			currentState = STATE.WALKING;
		}
		if(reshotTimer < 0) {
			reshotTimer = 33;
			Entity newShot = shotPrototype.clone();
			Result<Component, NoSuchElementException> newShotBox_R = newShot.getTraitByID(TRAIT.BOX);
			if(newShotBox_R.is_ok()) {
				Box newShotBox = (Box)newShotBox_R.unwrap();
				newShotBox.r.setCenterX((float) (box.r.getCenterX() + box.r.getWidth()*2/3f*Math.sin(2*Math.PI*Math.random())));
				newShotBox.r.setCenterY(box.r.getY()-newShotBox.r.getHeight());
				shotList.add(newShot);
			}
		}
		if(c.buttonPressed(BreakoutGame.KEY_SHOOT)) {
			timer = Math.max(timesReshot/100f,timer);
			timesReshot--;
		}
	}

	@Override
	public Component clone() {
		System.out.println("HAHA nice try");
		return null;
	}
}
