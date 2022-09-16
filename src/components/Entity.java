package components;

import java.util.NoSuchElementException;

import etc.Result;

public class Entity {
	Component[] traits;
	public Entity(Component[] traits) {
		this.traits = traits;
	}
	
	public Result<Component,NoSuchElementException> getTraitByID(TRAIT ID) {
		for(Component c: traits) {
			if(c.ID() == ID) {
				return new Result<Component,NoSuchElementException>(c);
			}
		}
		return new Result<Component,NoSuchElementException>(new NoSuchElementException());
	}
}
