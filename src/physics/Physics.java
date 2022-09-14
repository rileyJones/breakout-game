package physics;

import org.newdawn.slick.geom.Rectangle;

import components.Box;
import components.Velocity;

public class Physics {
	
	public static int getBoxCollideDelta(Box aBox, Velocity aVel, Box bBox, Velocity bVel) {
		return (int) Math.floor(Math.max(Math.min(
				(aBox.r.getMaxX()-bBox.r.getMinX())/(bVel.x-aVel.x)
				,
				(aBox.r.getMinX()-bBox.r.getMaxX())/(bVel.x-aVel.x)
				),Math.min(
				(aBox.r.getMaxY()-bBox.r.getMinY())/(bVel.y-aVel.y)
				,
				(aBox.r.getMinY()-bBox.r.getMaxY())/(bVel.y-aVel.y)		
				)));
	}
	
	public static void doAcceleration(Box aBox, Velocity aVel, float x, float y, int delta) {
		aVel.x += x;
		aVel.y += y;
		aBox.r.setCenterX(
				aBox.r.getCenterX() + x*delta*delta/2.0f
			);
		aBox.r.setCenterY(
				aBox.r.getCenterY() + y*delta*delta/2.0f
			);
	}
	public static void doVelocity(Box aBox, Velocity aVel, float x, float y, int delta) {
		aVel.x += x;
		aVel.y += y;
		aBox.r.setCenterX(
				aBox.r.getCenterX() + aVel.x*delta
			);
		aBox.r.setCenterY(
				aBox.r.getCenterY() + aVel.y*delta
			);
	}
}
