package legion.aspect.menuHandler;

import legion.aspect.AspectBus;

public class MenuDenyHandler implements MenuHandler {

	@Override
	public boolean doCheck(AspectBus _bus) {
		return deny(_bus);
	}

	public boolean deny(AspectBus _bus) {
		return false;
	}

}
