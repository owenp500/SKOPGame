
public class BoidAnimation implements Animation {

	private static int universeCount = 0;
	
	public static int getUniverseCount() {
		return universeCount;
	}

	public static void setUniverseCount(int count) {
		BoidAnimation.universeCount = count;
	}

	public Universe getNextUniverse() {

		universeCount++;
		
		if (universeCount == 1) {
			return new BoidUniverse(500, 1500, 1000);
		}
		else {
			return null;
		}

	}
	
}
