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

package com.liferay.adaptive.media.document.library.thumbnails.internal.upgrade.v1_0_0;

import com.liferay.adaptive.media.document.library.thumbnails.internal.util.AMCompanyThumbnailConfigurationInitializer;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

/**
 * @author Roberto Díaz
 */
public class UpgradeDocumentLibraryThumbnailsConfiguration
	extends UpgradeProcess {

	public UpgradeDocumentLibraryThumbnailsConfiguration(
		AMImageConfigurationHelper
			amImageConfigurationHelper,
		CompanyLocalService companyLocalService) {

		_amImageConfigurationHelper = amImageConfigurationHelper;
		_companyLocalService = companyLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			AMCompanyThumbnailConfigurationInitializer
				amCompanyThumbnailConfigurationInitializer =
					new AMCompanyThumbnailConfigurationInitializer(
						_amImageConfigurationHelper);

			for (Company company : _companyLocalService.getCompanies()) {
				amCompanyThumbnailConfigurationInitializer.initializeCompany(
					company);
			}
		}
	}

	private final AMImageConfigurationHelper _amImageConfigurationHelper;
	private final CompanyLocalService _companyLocalService;

}