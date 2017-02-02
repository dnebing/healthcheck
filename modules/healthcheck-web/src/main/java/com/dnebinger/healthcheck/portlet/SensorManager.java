package com.dnebinger.healthcheck.portlet;

import com.dnebinger.healthcheck.sensor.Sensor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class SensorManager: Manages the list of healthcheck sensors and handles the status checks.
 *
 * @author dnebinger
 */
@Component(immediate = true, service = SensorManager.class)
public class SensorManager {

	/**
	 * getHealthStatuses: Returns the map of current health statuses.
	 * @return Map map of statuses, key is the sensor name and value is the sensor status.
	 */
	public Map<String, String> getHealthStatus() {
		StopWatch totalWatch = null;

		// time the total health check
		if (_log.isDebugEnabled()) {
			totalWatch = new StopWatch();

			totalWatch.start();
		}

		// grab the list of sensors from our service tracker
		List<Sensor> sensors = _serviceTracker.getSortedServices();

		// create a map to hold the sensor status results
		Map<String, String> statuses = new HashMap<>();

		// if we have at least one sensor
		if ((sensors != null) && (! sensors.isEmpty())) {
			String status;
			StopWatch sensorWatch = null;

			// create a stopwatch to time the sensors
			if (_log.isDebugEnabled()) {
				sensorWatch = new StopWatch();
			}

			// for each registered sensor
			for (Sensor sensor : sensors) {
				// reset the stopwatch for the run
				if (_log.isDebugEnabled()) {
					sensorWatch.reset();
					sensorWatch.start();
				}

				// get the status from the sensor
				status = sensor.getStatus();

				// add the sensor and status to the map
				statuses.put(sensor.getName(), status);

				// report sensor run time
				if (_log.isDebugEnabled()) {
					sensorWatch.stop();

					_log.debug("Sensor [" + sensor.getName() + "] run time: " + DurationFormatUtils.formatDurationWords(sensorWatch.getTime(), true, true));
				}
			}
		}

		// report health check run time
		if (_log.isDebugEnabled()) {
			totalWatch.stop();

			_log.debug("Health check run time: " + DurationFormatUtils.formatDurationWords(totalWatch.getTime(), true, true));
		}

		// return the status map
		return statuses;
	}

	@Activate
	protected void activate(BundleContext bundleContext, Map<String, Object> properties) {

		if (_log.isDebugEnabled()) {
			_log.debug("Activating the sensor manager...");
		}

		// if we have a current service tracker (likely not), let's close it.
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}

		// create a new sorting service tracker.
		_serviceTracker = new SortingServiceTracker(bundleContext, Sensor.class.getName(), new Comparator<Sensor>() {

			@Override
			public int compare(Sensor o1, Sensor o2) {
				// compare method to sort primarily on run order and secondarily on name.
				if ((o1 == null) && (o2 == null)) return 0;
				if (o1 == null) return -1;
				if (o2 == null) return 1;

				if (o1.getRunSortOrder() != o2.getRunSortOrder()) {
					return o1.getRunSortOrder() - o2.getRunSortOrder();
				}

				return o1.getName().compareTo(o2.getName());
			}
		});

		// start tracking all services
		_serviceTracker.open(true);
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private SortingServiceTracker<Sensor> _serviceTracker;
	private static final Log _log = LogFactoryUtil.getLog(SensorManager.class);
}
