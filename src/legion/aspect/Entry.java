package legion.aspect;

public interface Entry {
	boolean filter(AspectBus _bus);

//	AspectHandler getHandler(); // FIXME AspectHandler

	String getId();

//	void setAttribute(ConfigSource _source) throws AspectException; // FIXME ConfigSource
	
//	void setHandler(AspectHandler handler); // FIXME AspectHandler
	void setId(String _id);
}
