package legionLab.web.control.zk.gaStaffShiftDemo;

import org.slf4j.event.Level;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

import legion.util.LogUtil;
import legion.web.control.zk.legionmodule.pageTemplate.FnCntProxy;

public class FnPageComposer extends SelectorComposer<Component>{
	public final static String URI = "/legionLab/gaStaffShiftDemo/fnPage.zul";
	
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			FnCntProxy.register(this);

		} catch (Throwable e) {
			LogUtil.log(e, Level.ERROR);
		}
	}
	
	private void init() {

	}
	
	
	@Listen(Events.ON_UPLOAD + "=#btnUpload")
	public void btnUpload_uploaded(UploadEvent _evt) {
		Media media = _evt.getMedia();
		
		
	}
}
