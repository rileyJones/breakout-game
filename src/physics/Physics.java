package physics;

import components.Box;
import components.Mass;
import components.Velocity;
import etc.Common;

public class Physics {
	public enum DIRECTION {
		X_MINUS,
		X_PLUS,
		Y_MINUS,
		Y_PLUS
	}
	
	public static DIRECTION getAntiDirection(DIRECTION d) {
		switch(d) {
			case X_MINUS:
				return DIRECTION.X_PLUS;
			case X_PLUS:
				return DIRECTION.X_MINUS;
			case Y_MINUS:
				return DIRECTION.Y_PLUS;
			case Y_PLUS:
				return DIRECTION.Y_MINUS;
			default:
				return null;
		}
	}
	
	public static float getBoxCollideDelta(Box aBox, Velocity aVel, Box bBox, Velocity bVel) {
		return (Math.max(Math.min(
				(aBox.r.getMaxX()-bBox.r.getMinX())/(bVel.x-aVel.x)
				,
				(aBox.r.getMinX()-bBox.r.getMaxX())/(bVel.x-aVel.x)
				),Math.min(
				(aBox.r.getMaxY()-bBox.r.getMinY())/(bVel.y-aVel.y)
				,
				(aBox.r.getMinY()-bBox.r.getMaxY())/(bVel.y-aVel.y)		
				)))-0.001f;
	}
	public static DIRECTION getBoxCollideDirection(Box aBox, Velocity aVel, Box bBox, Velocity bVel) {
		float xp = (aBox.r.getMaxX()-bBox.r.getMinX())/(bVel.x-aVel.x);
		float xm = (aBox.r.getMinX()-bBox.r.getMaxX())/(bVel.x-aVel.x);
		float yp = (aBox.r.getMaxY()-bBox.r.getMinY())/(bVel.y-aVel.y);
		float ym = (aBox.r.getMinY()-bBox.r.getMaxY())/(bVel.y-aVel.y);
		if(Math.min(xp,xm) > Math.min(yp,ym)) {
			if(xp < xm) {
				return DIRECTION.X_PLUS;
			} else {
				return DIRECTION.X_MINUS;
			}
		} else {
			if(yp < ym) {
				return DIRECTION.Y_PLUS;
			} else {
				return DIRECTION.Y_MINUS;
			}
		}
	}
	
	public static float assertDirectionDelta(DIRECTION dir, Box aBox, Velocity aVel, Box bBox, Velocity bVel) {
		float xp = (aBox.r.getMaxX()-bBox.r.getMinX())/(bVel.x-aVel.x);
		float xm = (aBox.r.getMinX()-bBox.r.getMaxX())/(bVel.x-aVel.x);
		float yp = (aBox.r.getMaxY()-bBox.r.getMinY())/(bVel.y-aVel.y);
		float ym = (aBox.r.getMinY()-bBox.r.getMaxY())/(bVel.y-aVel.y);
		switch(dir) {
			case X_MINUS:
				return xm-0.001f;
			case X_PLUS:
				return xp-0.001f;
			case Y_MINUS:
				return ym-0.001f;
			case Y_PLUS:
				return yp-0.001f;
			default:
				return 0;
		}
	}
	
	public static void doAcceleration(Box aBox, Velocity aVel, float x, float y, float delta) {
		aVel.x += x*delta;
		aVel.y += y*delta;
		aBox.r.setCenterX(
				aBox.r.getCenterX() + x*delta*delta/2.0f
			);
		aBox.r.setCenterY(
				aBox.r.getCenterY() + y*delta*delta/2.0f
			);
	}
	public static void doVelocity(Box aBox, Velocity aVel, float x, float y, float delta) {
		aVel.x += x;
		aVel.y += y;
		aBox.r.setCenterX(
				aBox.r.getCenterX() + aVel.x*delta
			);
		aBox.r.setCenterY(
				aBox.r.getCenterY() + aVel.y*delta
			);
	}
	
	public static float doBoxClip(Box aBox, Velocity aVel, Box bBox, Velocity bVel, int delta) {
		float tempDelta = Physics.getBoxCollideDelta(aBox, aVel, bBox, bVel);
		Physics.doVelocity(aBox, aVel, 0, 0, tempDelta);
		Physics.doVelocity(bBox, bVel, 0, 0, tempDelta);
		return -tempDelta;
	}
	public static float assertDirectionClip(DIRECTION dir, Box aBox, Velocity aVel, Box bBox, Velocity bVel, int delta) {
		float tempDelta = Physics.assertDirectionDelta(dir, aBox, aVel, bBox, bVel);
		Physics.doVelocity(aBox, aVel, 0, 0, tempDelta);
		Physics.doVelocity(bBox, bVel, 0, 0, tempDelta);
		return -tempDelta;
	}
	
	public static void doSimpleCollision(Box aBox, Velocity aVel, Box bBox, Velocity bVel, int delta) {
		float tempDelta = doBoxClip(aBox, aVel, bBox, bVel, delta);
		DIRECTION boxCollideDirection = getBoxCollideDirection(aBox, aVel, bBox, bVel);
		switch(boxCollideDirection) {
			case X_MINUS:
				aVel.x = Math.max(Math.min(bVel.x,0),aVel.x);
				bVel.x = Math.min(Math.max(aVel.x,0),bVel.x);
				break;
			case X_PLUS:
				aVel.x = Math.min(Math.max(bVel.x,0),aVel.x);
				bVel.x = Math.max(Math.min(aVel.x,0),bVel.x);
				break;
			case Y_MINUS:
				aVel.y = Math.max(Math.min(bVel.y,0),aVel.y);
				bVel.y = Math.min(Math.max(aVel.y,0),bVel.y);
				break;
			case Y_PLUS:
				aVel.y = Math.min(Math.max(bVel.y,0),aVel.y);
				bVel.y = Math.max(Math.min(aVel.y,0),bVel.y);
				break;
			default:
				break;
				
		}
		Physics.doVelocity(aBox, aVel, 0, 0, tempDelta);
		Physics.doVelocity(bBox, bVel, 0, 0, tempDelta);
	}
	public static void doInelasticCollision(Box aBox, Velocity aVel, Mass aMass, Box bBox, Velocity bVel, Mass bMass, int delta, float factor) {
		float tempDelta = doBoxClip(aBox, aVel, bBox, bVel, delta);
		DIRECTION boxCollideDirection = getBoxCollideDirection(aBox, aVel, bBox, bVel);
		switch(boxCollideDirection) {
			case X_MINUS:
			case X_PLUS:
				{
					float[] vNew = inelasticCollisionFormula(aMass.m, bMass.m, aVel.x, bVel.x, factor);
					aVel.x = vNew[0];
					bVel.x = vNew[1];
				}
				break;
			case Y_MINUS:
			case Y_PLUS:
				{
					float[] vNew = inelasticCollisionFormula(aMass.m, bMass.m, aVel.y, bVel.y, factor);
					aVel.y = vNew[0];
					bVel.y = vNew[1];
				}
				break;
			default:
				break;
				
		}
		Physics.doVelocity(aBox, aVel, 0, 0, tempDelta);
		Physics.doVelocity(bBox, bVel, 0, 0, tempDelta);
	}
	public static void assertDirectionInelasticCollision(DIRECTION dir, Box aBox, Velocity aVel, Mass aMass, Box bBox, Velocity bVel, Mass bMass, int delta, float factor) {
		float tempDelta = assertDirectionClip(dir, aBox, aVel, bBox, bVel, delta);
		//DIRECTION boxCollideDirection = getBoxCollideDirection(aBox, aVel, bBox, bVel);
		switch(dir) {
			case X_MINUS:
			case X_PLUS:
				{
					float[] vNew = inelasticCollisionFormula(aMass.m, bMass.m, aVel.x, bVel.x, factor);
					aVel.x = vNew[0];
					bVel.x = vNew[1];
				}
				break;
			case Y_MINUS:
			case Y_PLUS:
				{
					float[] vNew = inelasticCollisionFormula(aMass.m, bMass.m, aVel.y, bVel.y, factor);
					aVel.y = vNew[0];
					bVel.y = vNew[1];
				}
				break;
			default:
				break;
				
		}
		Physics.doVelocity(aBox, aVel, 0, 0, tempDelta);
		Physics.doVelocity(bBox, bVel, 0, 0, tempDelta);
	}
	
	private static float[] inelasticCollisionFormula(float m1, float m2, float u1, float u2, float F) {
		if((m1 == 0 && m2 == 0) || (m1 == -1 && m2 == -1)){
			return new float[] {u1,u2};
		} else if( m1 == -1 || m2 == 0) {
			if(u2 > 0) {
				u2 = Math.min(u1, sqrt(F)*(u1-u2));
			} else if(u2 < 0) {
				u2 = Math.max(u1, sqrt(F)*(u1-u2));
			} else {
				u2 = u1;
			}
			return new float[] {u1,u2*F};
		} else if( m2 == -1 || m1 == 0) {
			if(u1 > 0) {
				u1 = Math.min(u2, sqrt(F)*(u2-u1));
			} else if(u1 < 0) {
				u1 = Math.max(u2, sqrt(F)*(u2-u1));
			} else {
				u1 = u2;
			}
			return new float[] {u1*F,u2};
		}
		float[] answer = new float[2];
		float A = (m2*m2)/(m1)+m2;
		float B = -2*m2*u1-(2*m2*m2*u2)/(m1);
		float C = -F*m1*u1*u1-F*m2*u2*u2+m1*u1*u1+2*m2*u1*u2+(m2*m2*u2*u2)/(m1);
		if(B*B-4*A*C < 0) {
			return inelasticCollisionFormula(m1, m2, u1, u2, 1.0f);
		}
		answer[1] = (-B + Common.sign(u1-u2)*sqrt(B*B-4*A*C))/(2*A);
		answer[0] = (m1*u1+m2*u2-m2*answer[1])/(m1);
		return answer;
	}
	private static float sqrt(float in) {
		return (float) Math.sqrt(in);
	}
	
	
}
