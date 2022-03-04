package legion.web.control.zk.gaStaffShiftDemo;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

import legion.web.control.zk.legionmodule.pageTemplate.FnCntProxy;

public class FnPageComposer extends SelectorComposer<Component>{
	public final static String URI = "gaStaffShiftDemo/fnPage.zul";
	
	@Override
	public void doAfterCompose(Component comp) {
		try {
			super.doAfterCompose(comp);
			System.out.println(this.getClass().getSimpleName() + ".doAfterCompose");
			FnCntProxy.register();

			/**/
//			String value2 = (String) Executions.getCurrent().getAttribute("key2");
//			System.out.println("value2: " + value2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void init() {

	}
	
	
	@Listen(Events.ON_UPLOAD + "=#btnUpload")
	public void btnUpload_uploaded(UploadEvent _evt) {
		Media media = _evt.getMedia();
		
		
	}
}
