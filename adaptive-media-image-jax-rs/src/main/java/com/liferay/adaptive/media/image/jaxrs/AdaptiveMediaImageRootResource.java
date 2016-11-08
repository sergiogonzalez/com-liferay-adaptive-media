package com.liferay.adaptive.media.image.jaxrs;

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
@Component(immediate = true, service = AdaptiveMediaImageRootResource.class)
@Path("/images")
public class AdaptiveMediaImageRootResource {

	@Path("/configuration/{companyId}")
	public AdaptiveMediaImageConfigResource getConfiguration(
		@PathParam("companyId") long companyId) {

		return new AdaptiveMediaImageConfigResource(
			companyId, imageAdaptiveMediaConfigurationHelper);
	}

	@Path("/content/last/{fileEntryId}")
	public AdaptiveMediaImageFileVersionResource getLastVersion(
			@PathParam("fileEntryId") long fileEntryId)
		throws PortalException {

		FileEntry fileEntry;

		try {
			fileEntry = dlAppService.getFileEntry(fileEntryId);
		}
		catch (NoSuchFileEntryException nsfee) {
			throw new NotFoundException();
		}

		return new AdaptiveMediaImageFileVersionResource(
			fileEntry.getLatestFileVersion(), finder);
	}

	@Path("/content/version/{fileVersionId}")
	public AdaptiveMediaImageFileVersionResource getVersion(
			@PathParam("fileVersionId") long fileVersionId)
		throws PortalException {

		FileVersion fileVersion;

		try {
			fileVersion = dlAppService.getFileVersion(fileVersionId);
		}
		catch (NoSuchFileVersionException nsfve) {
			throw new NotFoundException();
		}

		return new AdaptiveMediaImageFileVersionResource(fileVersion, finder);
	}

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected ImageAdaptiveMediaFinder finder;

	@Reference
	protected ImageAdaptiveMediaConfigurationHelper
		imageAdaptiveMediaConfigurationHelper;

}