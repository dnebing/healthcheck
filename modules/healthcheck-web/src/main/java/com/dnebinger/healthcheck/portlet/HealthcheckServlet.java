
package com.dnebinger.healthcheck.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * class HealthcheckServlet: This servlet class is registered to allow for responding to /o/healthcheck/status queries.
 *
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"osgi.http.whiteboard.context.path=/healthcheck",
		"osgi.http.whiteboard.servlet.name=com.dnebinger.healthcheck.portlet.HealthcheckServlet",
		"osgi.http.whiteboard.servlet.pattern=/healthcheck/status"
	},
	service = Servlet.class
)
public class HealthcheckServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		String format = ParamUtil.getString(request, "format", "html");

		if (_log.isDebugEnabled()) {
			_log.debug("Asked for status using format " + format);
		}

		boolean json = "json".equalsIgnoreCase(format);

		Map<String,String> statuses = _sensorManager.getHealthStatus();

		if (_log.isDebugEnabled()) {
			_log.debug("Have " + statuses.size() + " sensor statuses to report.");
		}

		if (json) {
			serveJsonResource(request, response, statuses);

			return;
		}

		serveHtmlResource(request, response, statuses);
	}

	/**
	 * serveJsonResource: Serves up the json status response.
	 * @param request
	 * @param response
	 * @param statuses
	 * @return boolean <code>true</code> if the response fails, otherwise okay.
	 * @throws ServletException
	 */
	protected boolean serveJsonResource(HttpServletRequest request, HttpServletResponse response, Map<String,String> statuses) throws ServletException {
		return writeResponse(HealthcheckStatusContentGenerator.generateJson(statuses), "application/json", response);
	}

	/**
	 * serveHtmlResource: Serves up the html status response.
	 * @param request
	 * @param response
	 * @param statuses
	 * @return boolean <code>true</code> if the response fails, otherwise okay.
	 * @throws ServletException
	 */
	protected boolean serveHtmlResource(HttpServletRequest request, HttpServletResponse response, Map<String,String> statuses) throws ServletException {
		return writeResponse(HealthcheckStatusContentGenerator.generateHtml(statuses), "text/html", response);
	}

	/**
	 * writeResponse: Writes out the response string and content type.
	 * @param data
	 * @param contentType
	 * @param response
	 * @return boolean <code>true</code> if the response fails, otherwise okay.
	 * @throws ServletException
	 */
	protected boolean writeResponse(final String data, final String contentType, final HttpServletResponse response) throws ServletException {

		response.setContentType(contentType);

		try {
			ServletResponseUtil.write(response, data);
		} catch (IOException e) {
			throw new ServletException("Failed to write response.", e);
		}

		return false;
	}

	@Reference(unbind = "-")
	protected void setSensorManager(final SensorManager sensorManager) {
		_sensorManager = sensorManager;
	}

	private SensorManager _sensorManager;
	private static final Log _log = LogFactoryUtil.getLog(HealthcheckServlet.class);


}