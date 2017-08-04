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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.site.demo.data.creator.SiteDemoDataCreator;

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
		Group group = _siteDemoDataCreator.create(
			company.getCompanyId(), "Adaptive Media Performance Testing");

		ServiceContext serviceContext = _getServiceContext(group);

		_adaptiveMediaImagePerformanceLayoutDemoDataCreator.create(
			serviceContext);

		_adaptiveMediaImagePerformanceBlogsEntryDemoDataCreator.create(
			serviceContext);

		_adaptiveMediaImagePerformanceConfigurationDemoDataCreator.create(
			company.getCompanyId());
	}

	@Deactivate
	protected void deactivate() throws Exception {
		_adaptiveMediaImagePerformanceConfigurationDemoDataCreator.delete();
		_adaptiveMediaImagePerformanceBlogsEntryDemoDataCreator.delete();
		_adaptiveMediaImagePerformanceLayoutDemoDataCreator.delete();
		_siteDemoDataCreator.delete();
	}

	private ServiceContext _getServiceContext(Group group)
		throws PortalException {

		User user = _userLocalService.getDefaultUser(group.getCompanyId());

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	@Reference
	private AdaptiveMediaImagePerformanceBlogsEntryDemoDataCreator
		_adaptiveMediaImagePerformanceBlogsEntryDemoDataCreator;

	@Reference
	private AdaptiveMediaImagePerformanceConfigurationDemoDataCreator
		_adaptiveMediaImagePerformanceConfigurationDemoDataCreator;

	@Reference
	private AdaptiveMediaImagePerformanceLayoutDemoDataCreator
		_adaptiveMediaImagePerformanceLayoutDemoDataCreator;

	@Reference
	private SiteDemoDataCreator _siteDemoDataCreator;

	@Reference
	private UserLocalService _userLocalService;

}