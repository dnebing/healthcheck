package com.dnebinger.healthcheck.sensor;

/**
 * class Sensor: Defines the sensor interface for all health check sensors.
 *
 * @author dnebinger
 */
public interface Sensor {
	public static final String STATUS_GREEN = "GREEN";
	public static final String STATUS_RED = "RED";
	public static final String STATUS_YELLOW = "YELLOW";

	/**
	 * getRunSortOrder: Returns the order that the sensor should run.  Lower numbers
	 * run before higher numbers.  When two sensors have the same run sort order, they
	 * are subsequently ordered by name.
	 * @return int The run sort order, lower numbers run before higher numbers.
	 */
	public int getRunSortOrder();

	/**
	 * getName: Returns the name of the sensor.  The name is also displayed in the HTML
	 * for the health check report, so using human-readable names is recommended.
	 * @return String The sensor display name.
	 */
	public String getName();

	/**
	 * getStatus: This is the meat of the sensor, this method is called to actually take
	 * a sensor reading and return one of the status codes listed above.
	 * @return String The sensor status.
	 */
	public String getStatus();
}
