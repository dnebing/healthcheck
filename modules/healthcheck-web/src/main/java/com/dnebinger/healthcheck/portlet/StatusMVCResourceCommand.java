package com.dnebinger.healthcheck.portlet;

import com.liferay.portal.kernel.image.ImageToolUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * class StatusMVCResourceCommand: The resource command handler for serving status details.
 *
 * @author dnebinger
 */
@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + HealthcheckPortletKeys.HEALTHCHECK,
				"mvc.command.name=/status"
		},
		service = MVCResourceCommand.class
)
public class StatusMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(ResourceRequest request, ResourceResponse response) throws Exception {
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
	 * @throws PortletException
	 */
	protected boolean serveJsonResource(ResourceRequest request, MimeResponse response, Map<String,String> statuses) throws PortletException {
		return writeResponse(HealthcheckStatusContentGenerator.generateJson(statuses), "application/json", response);
	}

	/**
	 * serveHtmlResource: Serves up the html status response.
	 * @param request
	 * @param response
	 * @param statuses
	 * @return boolean <code>true</code> if the response fails, otherwise okay.
	 * @throws PortletException
	 */
	protected boolean serveHtmlResource(ResourceRequest request, MimeResponse response, Map<String,String> statuses) throws PortletException {
		return writeResponse(HealthcheckStatusContentGenerator.generateHtml(statuses), "application/json", response);
	}

	/**
	 * writeResponse: Writes out the response string and content type.
	 * @param data
	 * @param contentType
	 * @param response
	 * @return boolean <code>true</code> if the response fails, otherwise okay.
	 * @throws PortletException
	 */
	protected boolean writeResponse(final String data, final String contentType, final MimeResponse response) throws PortletException {

		response.setContentType(contentType);

		try {
			PortletResponseUtil.write(response, data);
		} catch (IOException e) {
			throw new PortletException("Failed to write response.", e);
		}

		return false;
	}

	@Reference(unbind = "-")
	protected void setSensorManager(final SensorManager sensorManager) {
		_sensorManager = sensorManager;
	}

	private SensorManager _sensorManager;
	private static final Log _log = LogFactoryUtil.getLog(StatusMVCResourceCommand.class);
}
