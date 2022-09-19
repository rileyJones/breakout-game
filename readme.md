# It is a breakout game!
### Made by Riley Jones

## Features:

1. Level select can be reached by pressing the '/' key in a level, simply type the level number then press enter to go to it. You can press '/' again to cancel

2. The paddle is controlled with the arrow keys

3. Bricks are on the top of the screen.

4. Five levels exist with unique block layouts

5. Collision response does not clip, except downwards in order to ensure the ball never gets stuck(it makes sense given the different physics).

6. The number of lives is displayed at the top of the screen. Lives function very differently, see the bonus features below.

7. After you lose the 0 life, you are taken to a game over screen, you can press enter to continue the level as you left it.

8. The title screen sure is customized

## Bonus

* The ball is a physics object, obeying gravity and bouncing inelasticly off of objects.

* The bricks can have different numbers of hits, can be pass-through instead of solid, or even unbreakable.

* The game allows for custom levels in the form of correctly formatted text files, all base levels are in the levels folder.

* There exists a timer displayed at the top left of the screen. Until the timer reaches 0, the bottom of the screen is covered and the ball cannot fall.

* Rather than operating as a regular paddle, when the ball contacts the paddle, you lose a life. Instead, you must press the 'X' button to do an attack, which grants invulnerability and launches the ball on contact.

* There are various forms of attack::
	Stationary Attack: attack while not moving, 
	Dash Attack: attack while moving,
	Extended Dash Attack: attack while doing a dash attack,
	Dash Cancel Attack: attack while hold the opposite direction while doing a Dash Attack,
	
* You can also press the 'Z' button to throw a projectile that slightly influences the movement of the ball. If you mash 'Z' while doing a 'Dash Attack', you will throw projectiles at a much faster rate, but are locked into place while doing so.


