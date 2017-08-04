/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.adaptive.media.performance.internal;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class AdaptiveMediaPerformanceDemo
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		_adaptiveMediaImagePerformanceConfigurationDemoDataCreator.create(
			company.getCompanyId());

		_adaptiveMediaImagePerformanceBlogsEntryDemoDataCreator.create(
			company.getCompanyId());
	}

	@Deactivate
	protected void deactivate() throws Exception {
		_adaptiveMediaImagePerformanceConfigurationDemoDataCreator.delete();
		_adaptiveMediaImagePerformanceBlogsEntryDemoDataCreator.delete();
	}

	@Reference
	private AdaptiveMediaImagePerformanceBlogsEntryDemoDataCreator
		_adaptiveMediaImagePerformanceBlogsEntryDemoDataCreator;

	@Reference
	private AdaptiveMediaImagePerformanceConfigurationDemoDataCreator
		_adaptiveMediaImagePerformanceConfigurationDemoDataCreator;

}