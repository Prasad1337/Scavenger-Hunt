package csci567.scavengerhunt.enums;

/**
 * @author ccubukcu
 * */
public enum Level {
	
	LEVEL_0(0, 0),
	LEVEL_1(1, 100),
	LEVEL_2(2, 250),
	LEVEL_3(3, 500),
	LEVEL_4(4, 1000),
	LEVEL_5(5, 2600),
	LEVEL_6(6, 5150),
	LEVEL_7(7, 8350),
	LEVEL_8(8, 11500),
	LEVEL_9(9, 15750),
	LEVEL_10(10, 20000);
	
	int level;
	int xpToNextLevel;
	
	private Level(int _level, int _xpToNextLevel) {
		level = _level;
		xpToNextLevel = _xpToNextLevel;
	}
	
	public static int getXpForNextLevel(int totalXp) {
		int xpToNextLevel = 0;
		
		for (Level lvl : Level.values()) {
			if(xpToNextLevel <= totalXp) {
				xpToNextLevel += lvl.xpToNextLevel;
			} else {
				break;
			}
		}
		
		return xpToNextLevel - totalXp;
	}
	
	public static int getTotalXpForLevel(int level) {
		int totalXp = 0;
		for (Level lvl : Level.values()) {
			if(lvl.level <= level) {
				totalXp += lvl.xpToNextLevel;
			}
		}
		return totalXp;
	}
}
