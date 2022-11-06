package legion.web.control.zk.legionmodule.pageTemplate;

import org.zkoss.zhtml.Span;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class Step {
	private String name, icon, uri;

	private StepStatus status;
	private Div div;

	// -------------------------------------------------------------------------------
	private Step(String name, String icon, String uri) {
		this.name = name;
		this.icon = icon;
		this.uri = uri;
		this.status = StepStatus.FOLLOWING;
		this.div = initDiv();
	}
	
	public static Step of(String name, String icon, String uri) {
		return new Step(name, icon, uri);
	}
	
	// -------------------------------------------------------------------------------
	public String getName() {
		return name;
	}


	public String getUri() {
		return uri;
	}


	public Div getDiv() {
		return div;
	}
	
	// -------------------------------------------------------------------------------
	private Div initDiv() {
		Div div = new Div();
		div.setSclass(status.divSclass);
		Span span = new Span();
		span.setSclass("step-icon " + icon);
		Label label = new Label(name);
		label.setSclass("step-label");
		div.appendChild(span);
		div.appendChild(label);
		return div;
	}
	
	void updateStatus(StepStatus status) {
		this.status = status;
		if(div!=null)
			div.setSclass(status.divSclass);
	}

	// -------------------------------------------------------------------------------
	public enum StepStatus {
		PREVIOUS("step previous"), CURRENT("step current"), FOLLOWING("step following");

		private String divSclass;

		private StepStatus(String divSclass) {
			this.divSclass = divSclass;
		}

	}
}
