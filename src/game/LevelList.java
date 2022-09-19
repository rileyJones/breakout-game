package game;

public class LevelList {
	public static final int levelMax = 5;
	public static String get(int level) {
		switch(level) {
			case 1:
				return "levels/bricks";
			case 2:
				return "levels/box";
			case 3:
				return "levels/vault";
			case 4:
				return "levels/wall";
			case 5:
				return "levels/platform";
			default:
				return "levels/credits";
		}
	}
	public static String getName(int level) {
		return get(level).substring(get(level).indexOf('/')+1);
	}
	
	
}
