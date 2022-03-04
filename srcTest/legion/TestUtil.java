package legion;


import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public class TestUtil {
	public static void assertObjEqual(Object _base, Object _obj) throws Throwable {
		try {
			Map<String, Object> map1 = PropertyUtils.describe(_base);
			Map<String, Object> map2 = PropertyUtils.describe(_obj);
			for (String key : map1.keySet()) {
				if (key.equals("class"))
					continue;

				if (!map1.get(key).equals(map2.get(key))) {
					System.out.println(
							"attribute[" + key + "] should be [" + map1.get(key) + "], not [" + map2.get(key) + "].");
					assert false;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw e;
		}
	}
}
