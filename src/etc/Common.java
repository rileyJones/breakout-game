package etc;

public class Common {
	public static float sign(float a) {
		if(a > 0) {
			return 1;
		} else if(a < 0) {
			return -1;
		} else {
			return 0;
		}
	}
}
