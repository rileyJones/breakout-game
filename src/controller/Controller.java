package controller;

import org.newdawn.slick.Input;

public class Controller {
	
	private int controllerNum;
	private int numButtons;
	private int[] keysToListen;
	private boolean[] input;
	private boolean[] currentInput;
	private boolean[] previousInput;
	private int[] lastPressed;
	private int[] timeHeld;
		
	public boolean buttonPressed(int button) {
		return currentInput[button] && !previousInput[button];
	}
	
	public boolean buttonReleased(int button) {
		return !currentInput[button] && previousInput[button];
	}
	
	public boolean buttonHeld(int button) {
		return currentInput[button];
	}
	
	public boolean buttonRepressed(int button, int delay) {
		return buttonPressed(button) && lastPressed[button] < delay && lastPressed[button] > 0;
	}
	// EVENTS
		public Controller(int numButtons) {
			controllerNum = -1;
			this.keysToListen = new int[numButtons];
			for(int i = 0; i < numButtons; i++) {
				keysToListen[i] = -1;
			}
			this.numButtons = numButtons;
			input = new boolean[numButtons];
			currentInput = new boolean[numButtons];
			previousInput = new boolean[numButtons];
			lastPressed = new int[numButtons];
			timeHeld = new int[numButtons];
			for(int i = 0; i < numButtons; i++) {
				lastPressed[i] = 99999;
			}
		}
		
		public void press(int button) {
			input[button] = true;
		}
		
		public void release(int button) {
			input[button] = false;
		}
		
		public void update(int delta) {
			for(int i = 0; i < numButtons; i++) {
				previousInput[i] = currentInput[i];
				currentInput[i] = input[i];
				lastPressed[i]+=delta;
				timeHeld[i]+=delta;
				if(buttonPressed(i)) {
					timeHeld[i] = 0;
				} 
				if(buttonReleased(i)) {
					lastPressed[i] = timeHeld[i];
				}
			}
		}
	
	// AUTO-POLLING
		public Controller(int[] keysToListen) {
			controllerNum = -1;
			this.keysToListen = keysToListen.clone();
			numButtons = keysToListen.length;
			input = new boolean[numButtons];
			currentInput = new boolean[numButtons];
			previousInput = new boolean[numButtons];
			lastPressed = new int[numButtons];
			timeHeld = new int[numButtons];
			for(int i = 0; i < numButtons; i++) {
				lastPressed[i] = 99999;
			}
		}
		public Controller(int[] keysToListen, int controllerNum) {
			this.controllerNum = controllerNum;
			this.keysToListen = keysToListen.clone();
			numButtons = keysToListen.length;
			input = new boolean[numButtons];
			currentInput = new boolean[numButtons];
			previousInput = new boolean[numButtons];
			lastPressed = new int[numButtons];
			timeHeld = new int[numButtons];
			for(int i = 0; i < numButtons; i++) {
				lastPressed[i] = 99999;
			}
		}
		
		public void update(Input I, int delta) {
			for(int i = 0; i < input.length; i++) {
				if(keysToListen[i] < -1 && controllerNum >= 0 && controllerNum < I.getControllerCount()) {
					input[i] = I.isButtonPressed(-keysToListen[i]-2,controllerNum);
				} else if(keysToListen[i] > -1) {
					input[i] = I.isKeyPressed(keysToListen[i]) || I.isKeyDown(keysToListen[i]);
				}
			}
			update(delta);
		}
		
		public boolean setButtonListener(int button, int code) {
			if(button >= numButtons) {
				return false;
			}
			keysToListen[button] = -code-2;
			return true;
		}
		
		public boolean setKeyListener(int button, int code) {
			if(button >= numButtons) {
				return false;
			}
			keysToListen[button] = code;
			return true;
		}
		
		public boolean deleteListener(int button) {
			if(button >= numButtons) {
				return false;
			}
			keysToListen[button] = -1;
			return true;
		}
		
		public boolean setControllerNum(int controllerNum) {
			this.controllerNum = controllerNum;
			return true;
		}
	
}
