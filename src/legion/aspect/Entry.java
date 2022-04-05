package legion.aspect;

public interface Entry {
	boolean filter(AspectBus _bus);

	AspectHandler getHandler();

	String getId();

	void setAttribute(ConfigSource _source) throws AspectException;
	
	void setHandler(AspectHandler handler);
	void setId(String _id);
}
