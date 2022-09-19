package components;

public class EchoAI extends AI{

	int[] data;
	public EchoAI(int[] data) {
		this.data = data;
	}
	@Override
	public void update(Entity self, int delta) {
		
	}

	@Override
	public int[] getAnimNum() {
		return data;
	}

	@Override
	public Component clone() {
		return new EchoAI(data.clone());
	}

}
