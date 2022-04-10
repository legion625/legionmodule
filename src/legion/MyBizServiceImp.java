package legion;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBizServiceImp implements MyBizService{

	private Logger log = LoggerFactory.getLogger(MyBizServiceImp.class);
	
	@Override
	public void register(Map<String, String> _params) {
		// TODO Auto-generated method stub
		
		System.out.println("MyBizServiceImp.register");
		log.debug("MyBizServiceImp.register");
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
