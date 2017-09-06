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

package com.liferay.adaptive.media.image.internal.finder;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.finder.AMFinder;
import com.liferay.adaptive.media.finder.AMQuery;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.finder.AMImageQueryBuilder;
import com.liferay.adaptive.media.image.internal.configuration.AdaptiveMediaImageAttributeMapping;
import com.liferay.adaptive.media.image.internal.processor.AdaptiveMediaImage;
import com.liferay.adaptive.media.image.internal.util.ImageProcessor;
import com.liferay.adaptive.media.image.model.AdaptiveMediaImageEntry;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageAttribute;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageProcessor;
import com.liferay.adaptive.media.image.service.AdaptiveMediaImageEntryLocalService;
import com.liferay.adaptive.media.image.url.AdaptiveMediaImageURLFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.net.URI;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	immediate = true,
	property = "model.class.name=com.liferay.portal.kernel.repository.model.FileVersion",
	service = {AMFinder.class, AMImageFinder.class}
)
public class AMImageFinderImpl implements AMImageFinder {

	@Override
	public Stream<AdaptiveMedia<AdaptiveMediaImageProcessor>>
			getAdaptiveMediaStream(
				Function
					<AMImageQueryBuilder,
						AMQuery<FileVersion, AdaptiveMediaImageProcessor>>
							amImageQueryBuilderFunction)
		throws PortalException {

		if (amImageQueryBuilderFunction == null) {
			throw new IllegalArgumentException(
				"amImageQueryBuilder must be non null");
		}

		AMImageQueryBuilderImpl amImageQueryBuilderImpl =
			new AMImageQueryBuilderImpl();

		AMQuery<FileVersion, AdaptiveMediaImageProcessor> amQuery =
			amImageQueryBuilderFunction.apply(amImageQueryBuilderImpl);

		if (amQuery != AMImageQueryBuilderImpl.AM_QUERY) {
			throw new IllegalArgumentException(
				"Only queries built by the provided query builder are valid.");
		}

		FileVersion fileVersion = amImageQueryBuilderImpl.getFileVersion();

		if (!_imageProcessor.isMimeTypeSupported(fileVersion.getMimeType())) {
			return Stream.empty();
		}

		BiFunction<FileVersion, AMImageConfigurationEntry, URI> uriFactory =
			_getURIFactory(amImageQueryBuilderImpl);

		AMImageQueryBuilder.ConfigurationStatus configurationStatus =
			amImageQueryBuilderImpl.getConfigurationStatus();

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				fileVersion.getCompanyId(), configurationStatus.getPredicate());

		Predicate<AMImageConfigurationEntry> filter =
			amImageQueryBuilderImpl.getConfigurationEntryFilter();

		Stream<AMImageConfigurationEntry> amImageConfigurationEntryStream =
			amImageConfigurationEntries.stream();

		return amImageConfigurationEntryStream.filter(
			amImageConfigurationEntry ->
				filter.test(amImageConfigurationEntry) &&
				_hasAdaptiveMedia(fileVersion, amImageConfigurationEntry)
		).map(
			amImageConfigurationEntry ->
				_createMedia(fileVersion, uriFactory, amImageConfigurationEntry)
		).sorted(
			amImageQueryBuilderImpl.getComparator()
		);
	}

	@Reference(unbind = "-")
	public void setAdaptiveMediaImageEntryLocalService(
		AdaptiveMediaImageEntryLocalService imageEntryLocalService) {

		_imageEntryLocalService = imageEntryLocalService;
	}

	@Reference(unbind = "-")
	public void setAdaptiveMediaImageURLFactory(
		AdaptiveMediaImageURLFactory adaptiveMediaImageURLFactory) {

		_adaptiveMediaImageURLFactory = adaptiveMediaImageURLFactory;
	}

	@Reference(unbind = "-")
	public void setAMImageConfigurationHelper(
		AMImageConfigurationHelper amImageConfigurationHelper) {

		_amImageConfigurationHelper = amImageConfigurationHelper;
	}

	@Reference(unbind = "-")
	public void setImageProcessor(ImageProcessor imageProcessor) {
		_imageProcessor = imageProcessor;
	}

	private AdaptiveMedia<AdaptiveMediaImageProcessor> _createMedia(
		FileVersion fileVersion,
		BiFunction<FileVersion, AMImageConfigurationEntry, URI> uriFactory,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		Map<String, String> properties =
			amImageConfigurationEntry.getProperties();

		AMAttribute<Object, String> configurationUuidAMAttribute =
			AMAttribute.getConfigurationUuidAMAttribute();

		properties.put(
			configurationUuidAMAttribute.getName(),
			amImageConfigurationEntry.getUUID());

		AMAttribute<Object, String> fileNameAMAttribute =
			AMAttribute.getFileNameAMAttribute();

		properties.put(
			fileNameAMAttribute.getName(), fileVersion.getFileName());

		AdaptiveMediaImageEntry imageEntry =
			_imageEntryLocalService.fetchAdaptiveMediaImageEntry(
				amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId());

		if (imageEntry != null) {
			AMAttribute<AdaptiveMediaImageProcessor, Integer>
				imageHeightAMAttribute =
					AdaptiveMediaImageAttribute.IMAGE_HEIGHT;

			properties.put(
				imageHeightAMAttribute.getName(),
				String.valueOf(imageEntry.getHeight()));

			AMAttribute<AdaptiveMediaImageProcessor, Integer>
				imageWidthAMAttribute = AdaptiveMediaImageAttribute.IMAGE_WIDTH;

			properties.put(
				imageWidthAMAttribute.getName(),
				String.valueOf(imageEntry.getWidth()));

			AMAttribute<Object, String> contentTypeAMAttribute =
				AMAttribute.getContentTypeAMAttribute();

			properties.put(
				contentTypeAMAttribute.getName(), imageEntry.getMimeType());

			AMAttribute<Object, Integer> contentLengthAMAttribute =
				AMAttribute.getContentLengthAMAttribute();

			properties.put(
				contentLengthAMAttribute.getName(),
				String.valueOf(imageEntry.getSize()));
		}

		AdaptiveMediaImageAttributeMapping attributeMapping =
			AdaptiveMediaImageAttributeMapping.fromProperties(properties);

		return new AdaptiveMediaImage(
			() ->
				_imageEntryLocalService.getAdaptiveMediaImageEntryContentStream(
					amImageConfigurationEntry, fileVersion),
			attributeMapping,
			uriFactory.apply(fileVersion, amImageConfigurationEntry));
	}

	private BiFunction<FileVersion, AMImageConfigurationEntry, URI>
		_getURIFactory(AMImageQueryBuilderImpl amImageQueryBuilderImpl) {

		if (amImageQueryBuilderImpl.hasFileVersion()) {
			return _adaptiveMediaImageURLFactory::createFileVersionURL;
		}

		return _adaptiveMediaImageURLFactory::createFileEntryURL;
	}

	private boolean _hasAdaptiveMedia(
		FileVersion fileVersion,
		AMImageConfigurationEntry amImageConfigurationEntry) {

		AdaptiveMediaImageEntry imageEntry =
			_imageEntryLocalService.fetchAdaptiveMediaImageEntry(
				amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId());

		if (imageEntry == null) {
			return false;
		}

		return true;
	}

	private AdaptiveMediaImageURLFactory _adaptiveMediaImageURLFactory;
	private AMImageConfigurationHelper _amImageConfigurationHelper;
	private AdaptiveMediaImageEntryLocalService _imageEntryLocalService;
	private ImageProcessor _imageProcessor;

}