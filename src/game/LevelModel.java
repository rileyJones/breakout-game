package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import javax.xml.bind.ValidationException;

import org.newdawn.slick.Color;
import components.Box;
import components.Component;
import components.Entity;
import components.Mass;
import components.TRAIT;
import components.Velocity;
import etc.Result;
import physics.Physics;

public class LevelModel {
	public int tileWidth, tileHeight;
	public int maxX, maxY;
	public int posX=3*8, posY=3*8*5;
	ArrayList<ArrayList<Tile>> tiles;
	Box oldBallBox;
	
	public LevelModel(String ref) throws ValidationException {
		tiles = new ArrayList<ArrayList<Tile>>();
		try {
			try (BufferedReader br = new BufferedReader(new FileReader(ref))) {
				String line = br.readLine();
				int lineNum = 0;
				int lineIndex = 0;
				int temp = 0;
				StringCharacterIterator lineIterator = new StringCharacterIterator(line);
				while(line != null) {
					char thisChar = lineIterator.current();
					while(thisChar != CharacterIterator.DONE) {
						if(lineNum == 0) {
							if(isNumeric(thisChar)) {
								temp = temp*10 + number(thisChar);
							} else if(isSpace(thisChar)) {
								tileWidth = temp;
								temp = 0;
							} else {
								throw new ValidationException("Bad input file");
							}
						} else {
							if(thisChar == '#') {
								break;
							} else {
								if(lineIndex == 0) {
									tiles.add(new ArrayList<Tile>());
								}
								
								switch(lineIndex%3) {
									case 0:
										if(isAlpha(thisChar)) {
											tiles.get(tiles.size()-1).add(new Tile(thisChar));
										} else {
											throw new ValidationException("Bad input file");
										}
										break;
									case 1:
										if(isNumeric(thisChar)) {
											tiles.get(tiles.size()-1).get(tiles.get(tiles.size()-1).size()-1).setNumHits(number(thisChar));
										} else {
											throw new ValidationException("Bad input file");
										}
										break;
									case 2:
										if(isSpace(thisChar)) {
											
										} else {
											throw new ValidationException("Bad input file");	
										}
										break;
								}
							}
						}
						if(lineNum == 0) {
							tileHeight = temp;
						}
						lineIndex++;
						thisChar = lineIterator.next();
					}
					lineNum++;
					lineIndex = 0;
					line = br.readLine();
					if(line != null) {
						lineIterator.setText(line);
					}
				}
				maxY = tiles.size();
				int maxSize = 0;
				for(ArrayList<Tile> list: tiles) {
					maxSize = Math.max(list.size(),maxSize);
				}
				maxX = maxSize;
				for(ArrayList<Tile> list: tiles) {
					int size = maxX - list.size();
					for(int i = 0; i < size; i++) {
						Tile newTile = new Tile('E');
						newTile.setNumHits(0);
						list.add(newTile);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	boolean isNumeric(char c) {
		return c >= '0' && c <= '9';
	}
	boolean isAlpha(char c) {
		return c >= 'A' && c <= 'Z' || c == '0';
	}
	boolean isSpace(char c) {
		return c == ' ' || c == ',' || c == 'x';
	}
	int number(char c) {
		return c - '0';
	}
	
	public void update(Entity ball, int delta) {
		Result<Component, NoSuchElementException> ballBox_R = ball.getTraitByID(TRAIT.BOX);
		if(ballBox_R.is_err()) return;
		Box ballBox = ((Box)ballBox_R.unwrap());
		Result<Component, NoSuchElementException> ballVel_R = ball.getTraitByID(TRAIT.VELOCITY);
		if(ballVel_R.is_err()) return;
		Velocity ballVel = ((Velocity)ballVel_R.unwrap());
		Result<Component, NoSuchElementException> ballMass_R = ball.getTraitByID(TRAIT.MASS);
		if(ballMass_R.is_err()) return;
		Mass ballMass = ((Mass)ballMass_R.unwrap());
		if(oldBallBox == null) {
			oldBallBox = (Box) ballBox.clone();
			return;
		}
		int[] oldCollisions = oldTileCollisions(oldBallBox);
		int[] newCollisions = oldTileCollisions(ballBox);
		//System.out.println(newCollisions[0]+", "+newCollisions[1]+", "+newCollisions[2]+", "+newCollisions[3]);
		if(newCollisions[0] >= maxX || newCollisions[1] < 0 || newCollisions[2] >= maxY || newCollisions[3] < 0) {
			oldBallBox = (Box) ballBox.clone();
			return;
		}
		int[] oldCollisionsR = tileCollisions(oldBallBox);
		newCollisions = tileCollisions(ballBox);
		boolean foundReflectionX = false;
		boolean foundReflectionY = false;
		if(newCollisions[0] < oldCollisions[0]) {
			for(int x = oldCollisions[0] - 1; x >= newCollisions[0]; x--) {
				for(int y = Math.max(newCollisions[2],oldCollisionsR[2]); y <= Math.min(newCollisions[3],oldCollisionsR[3]); y++) {
					TILEACTION act = tiles.get(y).get(x).onHit(this, x, y);
					if(act == TILEACTION.BOUNCE) {
						if(!foundReflectionX) {
							foundReflectionX = true;
							Box tileBox = new Box(posX+x*tileWidth, posY+y*tileHeight, tileWidth, tileHeight);
							Velocity tileVel = new Velocity(0,0);
							Mass tileMass = new Mass(-1);
							Physics.doInelasticCollision(tileBox, tileVel, tileMass, ballBox, ballVel, ballMass, delta, 0.98f);
						} 
					}
				}
				if(foundReflectionX) {
					break;
				}
			}
		}
		if(newCollisions[1] > oldCollisions[1]) {
			for(int x = oldCollisions[1] + 1; x <= newCollisions[1]; x++) {
				for(int y = Math.max(newCollisions[2],oldCollisionsR[2]); y <= Math.min(newCollisions[3],oldCollisionsR[3]); y++) {
					TILEACTION act = tiles.get(y).get(x).onHit(this, x, y);
					if(act == TILEACTION.BOUNCE) {
						if(!foundReflectionX) {
							foundReflectionX = true;
							Box tileBox = new Box(posX+x*tileWidth, posY+y*tileHeight, tileWidth, tileHeight);
							Velocity tileVel = new Velocity(0,0);
							Mass tileMass = new Mass(-1);
							Physics.doInelasticCollision(ballBox, ballVel, ballMass, tileBox, tileVel, tileMass, delta, 0.98f);
						} 
					}
				}
				if(foundReflectionX) {
					break;
				}
			}
		}
		
		if(newCollisions[2] < oldCollisions[2]) {
			for(int y = oldCollisions[2] - 1; y >= newCollisions[2]; y--) {
				for(int x = Math.max(newCollisions[0],oldCollisionsR[0]); x <= Math.min(newCollisions[1],oldCollisionsR[1]); x++) {
					TILEACTION act = tiles.get(y).get(x).onHit(this, x, y);
					if(act == TILEACTION.BOUNCE) {
						if(!foundReflectionY) {
							foundReflectionY = true;
							Box tileBox = new Box(posX+x*tileWidth, posY+y*tileHeight, tileWidth, tileHeight);
							Velocity tileVel = new Velocity(0,0);
							Mass tileMass = new Mass(-1);
							Physics.doInelasticCollision(ballBox, ballVel, ballMass, tileBox, tileVel, tileMass, delta, 0.98f);
						} 
					}
				}
				if(foundReflectionX) {
					break;
				}
			}
		}
		if(newCollisions[3] > oldCollisions[3]) {
			for(int y = oldCollisions[3] + 1; y <= newCollisions[3]; y++) {
				for(int x = Math.max(newCollisions[0],oldCollisionsR[0]); x <= Math.min(newCollisions[1],oldCollisionsR[1]); x++) {
					TILEACTION act = tiles.get(y).get(x).onHit(this, x, y);
					if(act == TILEACTION.BOUNCE) {
						if(!foundReflectionY) {
							foundReflectionY = true;
							Box tileBox = new Box(posX+x*tileWidth, posY+y*tileHeight, tileWidth, tileHeight);
							Velocity tileVel = new Velocity(0,0);
							Mass tileMass = new Mass(-1);
							Physics.doInelasticCollision(ballBox, ballVel, ballMass, tileBox, tileVel, tileMass, delta, 0.98f);
						} 
					}
				}
				if(foundReflectionY) {
					break;
				}
			}
		}
		if(!foundReflectionX && !foundReflectionY) {
			int x,y;
			if(newCollisions[0] < oldCollisions[0]) {
				x = newCollisions[0];
			} else if(newCollisions[1] > oldCollisions[1]) {
				x = newCollisions[1];
			} else {
				x = -1;
			}
			if(newCollisions[2] < oldCollisions[2]) {
				y = newCollisions[2];
			} else if(newCollisions[3] > oldCollisions[3]) {
				y = newCollisions[3];
			} else {
				y = -1;
			}
			if(x != -1 && y != -1) {
				TILEACTION act = tiles.get(y).get(x).onHit(this, x, y);
				if(act == TILEACTION.BOUNCE) {
					if(!foundReflectionY) {
						foundReflectionY = true;
						Box tileBox = new Box(posX+x*tileWidth, posY+y*tileHeight, tileWidth, tileHeight);
						Velocity tileVel = new Velocity(0,0);
						Mass tileMass = new Mass(-1);
						Physics.doInelasticCollision(ballBox, ballVel, ballMass, tileBox, tileVel, tileMass, delta, 0.9f);
					} 
				}
			}
		}
		oldBallBox = (Box) ballBox.clone();
	}
	int[] tileCollisions(Box b) {
		int[] answer = new int[6];
		
		answer[0] = (((int)(b.r.getMinX())) - posX)/tileWidth;
		answer[1] = (((int)(b.r.getMaxX())) - posX)/tileWidth;
		answer[2] = (((int)(b.r.getMinY())) - posY)/tileHeight;
		answer[3] = (((int)(b.r.getMaxY())) - posY)/tileHeight;
		answer[4] = (((int)(b.r.getCenterX())) - posX)/tileWidth;
		answer[5] = (((int)(b.r.getCenterY())) - posY)/tileHeight;
		
		answer[0] = Math.min(maxX-1, Math.max(0, answer[0]));
		answer[2] = Math.min(maxY-1, Math.max(0, answer[2]));
		answer[1] = Math.max(0, Math.min(maxX-1, answer[1]));
		answer[3] = Math.max(0, Math.min(maxY-1, answer[3]));
		
		return answer;
	}
	int[] oldTileCollisions(Box b) {
		int[] answer = new int[6];
		
		answer[0] = (((int)(b.r.getMinX())) - posX)/tileWidth;
		answer[1] = (((int)(b.r.getMaxX())) - posX)/tileWidth;
		answer[2] = (((int)(b.r.getMinY())) - posY)/tileHeight;
		answer[3] = (((int)(b.r.getMaxY())) - posY)/tileHeight;
		answer[4] = (((int)(b.r.getCenterX())) - posX)/tileWidth;
		answer[5] = (((int)(b.r.getCenterY())) - posY)/tileHeight;
		
		answer[0] = Math.min(maxX, Math.max(-1, answer[0]));
		answer[2] = Math.min(maxY, Math.max(-1, answer[2]));
		answer[1] = Math.max(-1, Math.min(maxX, answer[1]));
		answer[3] = Math.max(-1, Math.min(maxY, answer[3]));
		
		return answer;
	}
	public Color getColor(int x, int y) {
		return tiles.get(y).get(x).getColor(this, x, y);
	}
	
	public int getTileCount() {
		int sum = 0;
		for(ArrayList<Tile> list: tiles) {
			for(Tile t: list) {
				sum += t.getScore();
			}
		}
		return sum;
	}
	
}

class Tile {
	TYPE tileType;
	int numHits;
	enum TYPE {
		SOLID,
		BREAKABLE,
		PASSTHROUGH,
		UP,
		LEFT,
		EMPTY
	}
	Tile(char TileType) {
		switch(TileType) {
			case 'S':
				this.tileType = TYPE.SOLID;
				break;
			case 'B':
				this.tileType = TYPE.BREAKABLE;
				break;
			case 'P':
				this.tileType = TYPE.PASSTHROUGH;
				break;
			case 'U':
				this.tileType = TYPE.UP;
				break;
			case 'L':
				this.tileType = TYPE.LEFT;
				break;
			case '0':
			case 'E':
				this.tileType = TYPE.EMPTY;
				break;
		}
	}
	void setNumHits(int numHits) {
		this.numHits = numHits;
	}
	
	TILEACTION onHit(LevelModel lm, int x, int y) {
		switch(tileType) {
			case BREAKABLE:
				numHits--;
				if(numHits <= 0) {
					tileType = TYPE.EMPTY;
				}
				return TILEACTION.BOUNCE;
			case EMPTY:
				return TILEACTION.PASS;
			case LEFT:
				if(x == 0) {
					return TILEACTION.PASS;
				}
				return lm.tiles.get(y).get(x-1).onHit(lm, x-1, y);
			case PASSTHROUGH:
				numHits--;
				if(numHits <= 0) {
					tileType = TYPE.EMPTY;
				}
				return TILEACTION.PASS;
			case SOLID:
				return TILEACTION.BOUNCE;
			case UP:
				if(y == 0) {
					return TILEACTION.PASS;
				}
				return lm.tiles.get(y-1).get(x).onHit(lm, x, y-1);
			default:
				return TILEACTION.PASS;
		}
	}
	Color getColor(LevelModel lm, int x, int y) {
		switch(tileType) {
			case BREAKABLE:
				return new Color(0x00 + (int)(0xff*(numHits-1f)/numHits), 0xff + (int)(0xff*(numHits-1f)/numHits),0x00 + (int)(0xff*(numHits-1f)/numHits));
			case EMPTY:
				return null;
			case LEFT:
				if(x == 0) {
					return null;
				}
				return lm.tiles.get(y).get(x-1).getColor(lm, x-1, y);
			case PASSTHROUGH:
				return new Color(0x00 + (int)(0xff*(numHits-1f)/numHits), 0x00 + (int)(0xff*(numHits-1f)/numHits), 0xff + (int)(0xff*(numHits-1f)/numHits));
			case SOLID:
				return Color.white;
			case UP:
				if(y == 0) {
					return null;
				}
				return lm.tiles.get(y-1).get(x).getColor(lm, x, y-1);
			default:
				return null;
		}
	}
	int getScore() {
		switch(tileType) {
			case BREAKABLE:
				return numHits;
			case EMPTY:
				return 0;
			case LEFT:
				return 0;
			case PASSTHROUGH:
				return numHits;
			case SOLID:
				return 0;
			case UP:
				return 0;
			default:
				return 0;
		}
	}
}
