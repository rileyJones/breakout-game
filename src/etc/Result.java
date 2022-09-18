package etc;

public class Result<T,U extends Throwable> {
	private T successVal;
	private U failVal;
	private boolean didSucceed;
	public Result(T val) {
		successVal = val;
		didSucceed = true;
	}
	public Result(U val) {
		failVal = val;
		didSucceed = false;
	}
	
	public boolean is_ok() {
		return didSucceed;
	}
	public boolean is_err() {
		return !didSucceed;
	}
	
	public T unwrap() {
		if(didSucceed) {
			return successVal;
		} else {
			throw new Error("Unwrapped a failure", failVal);
		}
	}
	
	public T unwrap_or_else(T defaultVal) {
		if(didSucceed) {
			return successVal;
		} else {
			return defaultVal;
		}
	}
	
	public U handle() {
		if(!didSucceed) {
			return failVal;
		} else {
			throw new Error("Did not fail");
		}
	}
	
}
