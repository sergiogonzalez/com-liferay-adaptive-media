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

package com.liferay.adaptive.media.image.jaxrs.internal;

import com.liferay.adaptive.media.image.configuration.ImageAdaptiveMediaConfigurationHelper;
import com.liferay.adaptive.media.image.finder.ImageAdaptiveMediaFinder;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileVersionException;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true, service = ImageAdaptiveMediaRootResource.class)
@Path("/images")
public class ImageAdaptiveMediaRootResource {

	@Path("/configuration/{companyId}")
	public ImageAdaptiveMediaConfigResource getConfiguration(
		@PathParam("companyId") long companyId) {

		return new ImageAdaptiveMediaConfigResource(
			companyId, imageAdaptiveMediaConfigurationHelper);
	}

	@Path("/content/last/{fileEntryId}")
	public ImageAdaptiveMediaFileVersionResource getLastVersion(
			@PathParam("fileEntryId") long fileEntryId)
		throws PortalException {

		FileEntry fileEntry;

		try {
			fileEntry = dlAppService.getFileEntry(fileEntryId);
		}
		catch (NoSuchFileEntryException nsfee) {
			throw new NotFoundException();
		}

		return new ImageAdaptiveMediaFileVersionResource(
			fileEntry.getLatestFileVersion(), finder);
	}

	@Path("/content/version/{fileVersionId}")
	public ImageAdaptiveMediaFileVersionResource getVersion(
			@PathParam("fileVersionId") long fileVersionId)
		throws PortalException {

		FileVersion fileVersion;

		try {
			fileVersion = dlAppService.getFileVersion(fileVersionId);
		}
		catch (NoSuchFileVersionException nsfve) {
			throw new NotFoundException();
		}

		return new ImageAdaptiveMediaFileVersionResource(fileVersion, finder);
	}

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected ImageAdaptiveMediaFinder finder;

	@Reference
	protected ImageAdaptiveMediaConfigurationHelper
		imageAdaptiveMediaConfigurationHelper;

}