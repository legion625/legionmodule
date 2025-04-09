package legion.web.zk;

import org.zkoss.zul.Messagebox;

public class ZkMsgBox {

	public static String title = "LEGION";

	public static void info(String _msg) {
		Messagebox.show(_msg, title, Messagebox.OK, Messagebox.INFORMATION);
	}

	public static void exclamation(String _msg) {
		Messagebox.show(_msg, title, Messagebox.OK, Messagebox.EXCLAMATION);
	}

	public static void exclamation(String _msg, Runnable _runAction) {
		Messagebox.show(_msg, title, Messagebox.OK, Messagebox.EXCLAMATION, e -> {
			if ((int) e.getData() == Messagebox.OK)
				_runAction.run();
		});
	}

	public static void error(String _msg) {
		Messagebox.show(_msg, title, Messagebox.OK, Messagebox.ERROR);
	}

	public static void confirm(String _msg, Runnable _runAction) {
		Messagebox.show(_msg, title, Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, e -> {
			if ((int) e.getData() == Messagebox.OK)
				_runAction.run();
		});
	}
}
