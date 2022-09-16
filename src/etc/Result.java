package etc;

public class Result<T,U extends Throwable> {
	private T successVal;
	private U failVal;
	private boolean didSucceed;
	private boolean didHandle;
	public Result(T val) {
		successVal = val;
		didSucceed = true;
		didHandle = false;
	}
	public Result(U val) {
		failVal = val;
		didSucceed = false;
		didHandle = false;
	}
	
	public boolean is_ok() {
		didHandle = true;
		return didSucceed;
	}
	public boolean is_err() {
		didHandle = true;
		return !didSucceed;
	}
	
	public T unwrap() {
		didHandle = true;
		if(didSucceed) {
			return successVal;
		} else {
			throw new Error("Unwrapped a failure", failVal);
		}
	}
	
	public T unwrap_or_else(T defaultVal) {
		didHandle = true;
		if(didSucceed) {
			return successVal;
		} else {
			return defaultVal;
		}
	}
	
	public U handle() {
		didHandle = true;
		if(!didSucceed) {
			return failVal;
		} else {
			throw new Error("Did not fail");
		}
	}
	
	@Override
	public void finalize() {
		if(!didHandle) {
			throw new Error("Didn't handle failure", failVal);
		}
	}
}
