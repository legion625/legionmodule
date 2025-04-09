package legion;

public interface BusinessFactoryListener {
	void registedResource(Class<?> _iClass, BusinessService _iService, BusinessServiceFactory _businessServiceFactory);
}
