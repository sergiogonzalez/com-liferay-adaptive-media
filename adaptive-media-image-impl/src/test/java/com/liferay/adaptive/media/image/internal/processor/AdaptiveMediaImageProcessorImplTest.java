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

package com.liferay.adaptive.media.image.internal.processor;

import com.liferay.adaptive.media.exception.AdaptiveMediaRuntimeException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.exception.DuplicateAdaptiveMediaImageEntryException;
import com.liferay.adaptive.media.image.internal.configuration.AMImageConfigurationEntryImpl;
import com.liferay.adaptive.media.image.internal.util.ImageProcessor;
import com.liferay.adaptive.media.image.model.AdaptiveMediaImageEntry;
import com.liferay.adaptive.media.image.service.AdaptiveMediaImageEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.image.ImageTool;
import com.liferay.portal.kernel.image.ImageToolUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.StringUtil;

import java.awt.image.RenderedImage;

import java.io.InputStream;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class AdaptiveMediaImageProcessorImplTest {

	@Before
	public void setUp() {
		_processor.setImageEntryLocalService(
			_adaptiveMediaImageEntryLocalService);
		_processor.setImageProcessor(_imageProcessor);
		_processor.setAMImageConfigurationHelper(_amImageConfigurationHelper);

		ImageToolUtil imageToolUtil = new ImageToolUtil();

		imageToolUtil.setImageTool(_imageTool);
	}

	@Test
	public void testCleanUpFileVersion() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		_processor.cleanUp(_fileVersion);

		Mockito.verify(
			_adaptiveMediaImageEntryLocalService
		).deleteAdaptiveMediaImageEntryFileVersion(
			_fileVersion
		);
	}

	@Test(expected = AdaptiveMediaRuntimeException.IOException.class)
	public void testCleanUpIOException() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.doThrow(
			AdaptiveMediaRuntimeException.IOException.class
		).when(
			_adaptiveMediaImageEntryLocalService
		).deleteAdaptiveMediaImageEntryFileVersion(
			Mockito.any(FileVersion.class)
		);

		_processor.cleanUp(_fileVersion);
	}

	@Test(expected = AdaptiveMediaRuntimeException.IOException.class)
	public void testCleanUpPortalException() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.doThrow(
			PortalException.class
		).when(
			_adaptiveMediaImageEntryLocalService
		).deleteAdaptiveMediaImageEntryFileVersion(
			Mockito.any(FileVersion.class)
		);

		_processor.cleanUp(_fileVersion);
	}

	@Test
	public void testCleanUpWhenNotSupported() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_processor.cleanUp(_fileVersion);

		Mockito.verify(
			_adaptiveMediaImageEntryLocalService, Mockito.never()
		).deleteAdaptiveMediaImageEntryFileVersion(
			_fileVersion
		);
	}

	@Test
	public void testProcessConfigurationWhenAdaptiveMediaImageEntryAlreadyExists()
		throws Exception {

		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Optional.of(
				new AMImageConfigurationEntryImpl(
					StringUtil.randomString(), StringUtil.randomString(),
					Collections.emptyMap()))
		);

		Mockito.when(
			_adaptiveMediaImageEntryLocalService.fetchAdaptiveMediaImageEntry(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			Mockito.mock(AdaptiveMediaImageEntry.class)
		);

		_processor.process(_fileVersion, StringUtil.randomString());

		Mockito.verify(
			_imageProcessor, Mockito.never()
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);
	}

	@Test
	public void testProcessConfigurationWhenNoConfigurationEntry()
		throws Exception {

		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Optional.empty()
		);

		_processor.process(_fileVersion, StringUtil.randomString());

		Mockito.verify(
			_adaptiveMediaImageEntryLocalService, Mockito.never()
		).fetchAdaptiveMediaImageEntry(
			Mockito.anyString(), Mockito.anyLong()
		);
	}

	@Test
	public void testProcessConfigurationWhenNotSupported() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_processor.process(_fileVersion, StringUtil.randomString());

		Mockito.verify(
			_amImageConfigurationHelper, Mockito.never()
		).getAMImageConfigurationEntry(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	@Test(expected = AdaptiveMediaRuntimeException.IOException.class)
	public void testProcessDuplicateAdaptiveMediaImageEntryExceptionInImageService()
		throws Exception {

		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				StringUtil.randomString(), StringUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Optional.of(amImageConfigurationEntry)
		);

		RenderedImage renderedImage = Mockito.mock(RenderedImage.class);

		Mockito.when(
			_imageProcessor.scaleImage(_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			renderedImage
		);

		Mockito.doThrow(
			DuplicateAdaptiveMediaImageEntryException.class
		).when(
			_adaptiveMediaImageEntryLocalService
		).addAdaptiveMediaImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any(InputStream.class), Mockito.anyInt()
		);

		_processor.process(_fileVersion);
	}

	@Test
	public void testProcessFileVersion() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				StringUtil.randomString(), StringUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Optional.of(amImageConfigurationEntry)
		);

		RenderedImage renderedImage = Mockito.mock(RenderedImage.class);

		Mockito.when(
			_imageProcessor.scaleImage(_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			renderedImage
		);

		_processor.process(_fileVersion);

		Mockito.verify(
			_imageProcessor
		).scaleImage(
			_fileVersion, amImageConfigurationEntry
		);

		Mockito.verify(
			_adaptiveMediaImageEntryLocalService
		).addAdaptiveMediaImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any(InputStream.class), Mockito.anyInt()
		);
	}

	@Test(expected = AdaptiveMediaRuntimeException.InvalidConfiguration.class)
	public void testProcessInvalidConfigurationException() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.doThrow(
			AdaptiveMediaRuntimeException.InvalidConfiguration.class
		).when(
			_amImageConfigurationHelper
		).getAMImageConfigurationEntries(
			Mockito.anyLong()
		);

		_processor.process(_fileVersion);
	}

	@Test(expected = AdaptiveMediaRuntimeException.IOException.class)
	public void testProcessIOExceptionInImageProcessor() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				StringUtil.randomString(), StringUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Optional.of(amImageConfigurationEntry)
		);

		Mockito.doThrow(
			AdaptiveMediaRuntimeException.IOException.class
		).when(
			_imageProcessor
		).scaleImage(
			_fileVersion, amImageConfigurationEntry
		);

		_processor.process(_fileVersion);
	}

	@Test(expected = AdaptiveMediaRuntimeException.IOException.class)
	public void testProcessIOExceptionInStorage() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		AMImageConfigurationEntry amImageConfigurationEntry =
			new AMImageConfigurationEntryImpl(
				StringUtil.randomString(), StringUtil.randomString(),
				Collections.emptyMap());

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.singleton(amImageConfigurationEntry)
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntry(
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			Optional.of(amImageConfigurationEntry)
		);

		RenderedImage renderedImage = Mockito.mock(RenderedImage.class);

		Mockito.when(
			_imageProcessor.scaleImage(_fileVersion, amImageConfigurationEntry)
		).thenReturn(
			renderedImage
		);

		Mockito.doThrow(
			AdaptiveMediaRuntimeException.IOException.class
		).when(
			_adaptiveMediaImageEntryLocalService
		).addAdaptiveMediaImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any(InputStream.class), Mockito.anyInt()
		);

		_processor.process(_fileVersion);
	}

	@Test
	public void testProcessWhenNoConfigurationEntries() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_amImageConfigurationHelper.getAMImageConfigurationEntries(
				Mockito.anyLong())
		).thenReturn(
			Collections.emptyList()
		);

		_processor.process(_fileVersion);

		Mockito.verify(
			_imageProcessor, Mockito.never()
		).scaleImage(
			Mockito.any(FileVersion.class),
			Mockito.any(AMImageConfigurationEntry.class)
		);

		Mockito.verify(
			_adaptiveMediaImageEntryLocalService, Mockito.never()
		).addAdaptiveMediaImageEntry(
			Mockito.any(AMImageConfigurationEntry.class),
			Mockito.any(FileVersion.class), Mockito.anyInt(),
			Mockito.any(Integer.class), Mockito.any(InputStream.class),
			Mockito.any(Integer.class)
		);
	}

	@Test
	public void testProcessWhenNotSupported() throws Exception {
		Mockito.when(
			_imageProcessor.isMimeTypeSupported(Mockito.anyString())
		).thenReturn(
			false
		);

		_processor.process(_fileVersion);

		Mockito.verify(
			_amImageConfigurationHelper, Mockito.never()
		).getAMImageConfigurationEntries(
			Mockito.anyLong()
		);
	}

	private final AdaptiveMediaImageEntryLocalService
		_adaptiveMediaImageEntryLocalService = Mockito.mock(
			AdaptiveMediaImageEntryLocalService.class);
	private final AMImageConfigurationHelper _amImageConfigurationHelper =
		Mockito.mock(AMImageConfigurationHelper.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);
	private final ImageProcessor _imageProcessor = Mockito.mock(
		ImageProcessor.class);
	private final ImageTool _imageTool = Mockito.mock(ImageTool.class);
	private final AdaptiveMediaImageProcessorImpl _processor =
		new AdaptiveMediaImageProcessorImpl();

}