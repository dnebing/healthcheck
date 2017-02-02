package com.dnebinger.heathcheck.sensor.memory;

import com.dnebinger.healthcheck.sensor.Sensor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Component;

/**
 * class MemorySensor: This is a simple sensor that tests available memory.
 * &lt;= 60% is green, 61-80% is yellow, and &gt;80% is red.
 *
 * @author dnebinger
 */
@Component(immediate = true,service = Sensor.class)
public class MemorySensor implements Sensor {
	public static final String NAME = "JVM Memory";

	@Override
	public int getRunSortOrder() {
		// This can run at any time, it's not dependent on others.
		return 5;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getStatus() {
		_log.warn("Checking memory...");

		// need the percent used
		int pct = getPercentUsed();

		if (_log.isDebugEnabled()) {
			_log.debug("Current memory usage percent: " + pct);
		}

		// if we are 60% or less, we are green.
		if (pct <= 60) {
			_log.warn("Memory is GREEN");
			return STATUS_GREEN;
		}
		// if we are 61-80%, we are yellow
		if (pct <= 80) {
			_log.warn("Memory is YELLOW");
			return STATUS_YELLOW;
		}

		// if we are above 80%, we are red.
		_log.warn("Memory is RED");
		return STATUS_RED;
	}

	protected double getTotalMemory() {
		double mem = Runtime.getRuntime().totalMemory();

		return mem;
	}

	protected double getFreeMemory() {
		double mem = Runtime.getRuntime().freeMemory();

		return mem;
	}

	protected double getUsedMemory() {
		return getTotalMemory() - getFreeMemory();
	}

	protected int getPercentUsed() {
		double used = getUsedMemory();
		double pct = (used / getTotalMemory()) * 100.0;

		return (int) Math.round(pct);
	}

	protected int getPercentAvailable() {
		double pct = (getFreeMemory() / getTotalMemory()) * 100.0;

		return (int) Math.round(pct);
	}

	private static final Log _log = LogFactoryUtil.getLog(MemorySensor.class);
}