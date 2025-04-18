package legion.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {
	private static Logger log = LoggerFactory.getLogger(BeanUtil.class);

	public static Object getProperty(Object bean, String property) {
		Object object = bean;
		if (property != null) {
			try {
				object = PropertyUtils.getProperty(bean, property);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				log.error(e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		return object;
	}

	public static Class serviceClass(String className) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
			classLoader = Class.class.getClassLoader();
		try {
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T serviceInstance(String className) {
		try {
			return (T) serviceClass(className).getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
