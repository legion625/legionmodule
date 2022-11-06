package legion.web.control.zk.legionmodule.pageTemplate.stepbar;

public interface Stepbar {

	public int getCurrentIndex();

	public int getStepSize();

	public void back();

	public void next();

	public void navigateTo(int _index);

}
