package com.liferay.adaptive.media.image.jaxrs;

import com.liferay.adaptive.media.image.finder.ImageAdaptiveMediaFinder;
import com.liferay.portal.kernel.repository.model.FileVersion;

/**
 * @author Alejandro Hernández
 */
public class AdaptiveMediaImageFileVersionResource {

	private final ImageAdaptiveMediaFinder _finder;

	public AdaptiveMediaImageFileVersionResource(
		FileVersion fileVersion, ImageAdaptiveMediaFinder finder) {
		_fileVersion = fileVersion;
		_finder = finder;
	}

	private final FileVersion _fileVersion;

}