package com.dnebinger.healthcheck.portlet;

import com.dnebinger.healthcheck.sensor.Sensor;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.tools",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=Healthcheck",
			"javax.portlet.name=" + HealthcheckPortletKeys.HEALTHCHECK,
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class HealthcheckPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		renderRequest.setAttribute("sensorManager", _sensorManager);

		super.render(renderRequest, renderResponse);
	}

	@Reference(unbind = "-")
	protected void setSensorManager(final SensorManager sensorManager) {
		_sensorManager = sensorManager;
	}

	private SensorManager _sensorManager;
}