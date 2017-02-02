package com.dnebinger.healthcheck.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * class SortingServiceTracker: Maintains a sorted list of services.
 *
 * @author dnebinger
 */
public class SortingServiceTracker<T> extends ServiceTracker {
	private int lastCount = -1;
	private int lastRefCount = -1;
	private List<T> sortedServiceCache;
	private List<ServiceReference> sortedReferences;
	private Comparator<T> comparator;
	private static final Log _log = LogFactoryUtil.getLog(SortingServiceTracker.class);

	/**
	 * Constructor
	 */
	public SortingServiceTracker(final BundleContext context, final String clazz, final Comparator<T> comparator) {
		super(context, clazz, null);

		this.comparator = comparator;
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removedService(ServiceReference reference, Object service) {
		if (_log.isDebugEnabled()) {
			_log.debug("Removing a service.");
		}

		sortedServiceCache = null;
		sortedReferences = null;
		context.ungetService(reference);
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		sortedServiceCache = null;
		sortedReferences = null;
		if (_log.isDebugEnabled()) {
			_log.debug("Service was modified.");
		}
	}

	/**
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	@Override
	public Object addingService(ServiceReference reference) {
		sortedServiceCache = null;
		sortedReferences = null;
		if (_log.isDebugEnabled()) {
			_log.debug("Adding a new service.");
		}

		return context.getService(reference);
	}

	/**
	 * Return a sorted list of the services.
	 */
	public List<T> getSortedServices() {
		if ( sortedServiceCache == null || lastCount < getTrackingCount() ) {
			if (_log.isDebugEnabled()) {
				_log.debug("Fetching the service array...");
			}

			lastCount = getTrackingCount();

			final ServiceReference[] references = getServiceReferences();

			if ( references == null || references.length == 0 ) {
				if (_log.isDebugEnabled()) {
					_log.debug("No services found!");
				}

				sortedServiceCache = Collections.emptyList();
			} else {
				if (comparator == null) {
					Arrays.sort(references);
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Have " + references.length + " service references to process.");
				}

				sortedServiceCache = new ArrayList<T>();

				for (int i=0;i<references.length;i++) {
					@SuppressWarnings("unchecked")
					final T service = (T) getService(references[references.length - 1 - i]);

					if ( service != null ) {
						sortedServiceCache.add(service);
					}
				}

				if (comparator != null) {
					Collections.sort(sortedServiceCache, comparator);
				}
			}
		}

		return sortedServiceCache;
	}

	/**
	 * Return a sorted list of the services references.
	 */
	public List<ServiceReference> getSortedServiceReferences() {
		if ( sortedReferences == null || lastRefCount < getTrackingCount() ) {
			lastRefCount = getTrackingCount();
			final ServiceReference[] references = getServiceReferences();

			if ( references == null || references.length == 0 ) {
				sortedReferences = Collections.emptyList();
			} else {
				Arrays.sort(references);
				sortedReferences = new ArrayList<ServiceReference>();

				for (int i=0;i<references.length;i++) {
					sortedReferences.add(references[references.length - 1 - i]);
				}
			}
		}

		return sortedReferences;
	}
}
