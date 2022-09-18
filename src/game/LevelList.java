package game;

public class LevelList {
	public static final int levelMax = 3;
	public static String get(int level) {
		switch(level) {
			case 1:
				return "levels/level1";
			case 2:
				return "levels/box";
			case 3:
				return "levels/vault";
			default:
				return "levels/empty";
		}
		
	}
}
