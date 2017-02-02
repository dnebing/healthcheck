package com.dnebinger.healthcheck.portlet;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * class HealthcheckStatusContentGenerator: Utility class to generate status content.
 *
 * @author dnebinger
 */
public class HealthcheckStatusContentGenerator {

	public static String generateHtml(final Map<String, String> statuses) {
		StringBuilder sb = new StringBuilder("<!DOCTYPE html>");

		sb.append("\n<html><head><title>Healthcheck Status</title></head><body><table>");
		sb.append("<thead><tr><th>Sensor</th><th>Status</th></tr></thead><tbody>");

		List<String> names = new ArrayList<>(statuses.keySet());
		Collections.sort(names);

		for (String sensor : names) {
			sb.append("<tr><td>").append(sensor).append("</td><td>").append(statuses.get(sensor)).append("</td></tr>");
		}

		sb.append("</tbody></table></body></html>\n\n");

		String html = sb.toString();

		return html;
	}

	public static String generateJson(final Map<String, String> statuses) {
		JSONObject status = JSONFactoryUtil.createJSONObject();

		for (String sensor : statuses.keySet()) {
			status.put(sensor, statuses.get(sensor));
		}

		String json = status.toString();

		return json;
	}
}
