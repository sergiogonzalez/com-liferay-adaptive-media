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

import com.liferay.blogs.web.constants.BlogsPortletKeys;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.DefaultLayoutPrototypesUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = AdaptiveMediaImagePerformanceLayoutDemoDataCreator.class)
public class AdaptiveMediaImagePerformanceLayoutDemoDataCreator {

	public Layout create(ServiceContext serviceContext) throws Exception {
		Layout layout = _layoutLocalService.addLayout(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, "Home", "Home Page",
			"Adaptive Media Performance Testing Page",
			LayoutConstants.TYPE_PORTLET, false, "/home", serviceContext);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.setLayoutTemplateId(
			serviceContext.getUserId(), "1_column", false);

		DefaultLayoutPrototypesUtil.addPortletId(
			layout, BlogsPortletKeys.BLOGS, "column-1");

		_layouts.add(layout);

		return layout;
	}

	public void delete() throws Exception {
		for (Layout layout : _layouts) {
			_layoutLocalService.deleteLayout(layout);
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	private final List<Layout> _layouts = new ArrayList<>();

}