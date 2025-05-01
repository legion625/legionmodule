package legion.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Min-Hua Chao
 * 在Render上部署時，用來回應Health Check的Java servlet(伺服器端程式)。
 *
 */
public class HealthCheckServlet extends HttpServlet {
	private Logger log  = LoggerFactory.getLogger(HealthCheckServlet.class);
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.debug("HealthCheckServlet.doGet 1");
		// 設定回應內容為純文字格式
		response.setContentType("text/plain");
		// 寫入回應訊息 "OK"
		response.getWriter().write("OK");
		log.debug("HealthCheckServlet.doGet 2");
	}
}
