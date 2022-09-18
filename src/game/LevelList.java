package game;

public class LevelList {
	public static final int levelMax = 1;
	public static String get(int level) {
		switch(level) {
			case 1:
				return "levels/level1";
			default:
				return "levels/empty";
		}
		
	}
}
