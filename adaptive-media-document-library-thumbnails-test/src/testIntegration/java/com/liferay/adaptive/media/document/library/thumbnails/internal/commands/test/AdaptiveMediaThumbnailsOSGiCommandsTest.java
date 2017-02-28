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

package com.liferay.adaptive.media.document.library.thumbnails.internal.commands.test;

import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.document.library.thumbnails.internal.test.util.DestinationReplacer;
import com.liferay.adaptive.media.image.configuration.ImageAdaptiveMediaConfigurationHelper;
import com.liferay.adaptive.media.image.finder.ImageAdaptiveMediaFinder;
import com.liferay.adaptive.media.image.processor.ImageAdaptiveMediaProcessor;
import com.liferay.adaptive.media.image.service.AdaptiveMediaImageLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.util.DLPreviewableProcessor;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.util.promise.Promise;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
@Sync
public class AdaptiveMediaThumbnailsOSGiCommandsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		_configurationHelperServiceReference = registry.getServiceReference(
			ImageAdaptiveMediaConfigurationHelper.class);
		_finderServiceReference = registry.getServiceReference(
			ImageAdaptiveMediaFinder.class);

		_configurationHelper = registry.getService(
			_configurationHelperServiceReference);
		_finder = registry.getService(_finderServiceReference);

		_disableAdaptiveMediaThumbnails();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		registry.ungetService(_configurationHelperServiceReference);
		registry.ungetService(_finderServiceReference);

		_enableAdaptiveMediaThumbnails();
	}

	@Before
	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addCompanyAdminUser(_company);

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), _user.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", "100");
		properties.put("max-width", "100");

		_configurationHelper.addImageAdaptiveMediaConfigurationEntry(
			_company.getCompanyId(), _THUMBNAIL_CONFIGURATION,
			_THUMBNAIL_CONFIGURATION, properties);
	}

	@After
	public void tearDown() throws Exception {
		_configurationHelper.forceDeleteImageAdaptiveMediaConfigurationEntry(
			_company.getCompanyId(), _THUMBNAIL_CONFIGURATION);

		FileVersion latestFileVersion = _pngFileEntry.getLatestFileVersion(
			true);

		AdaptiveMediaImageLocalServiceUtil.deleteAdaptiveMediaImageFileVersion(
			latestFileVersion.getFileVersionId());

		GroupLocalServiceUtil.deleteGroup(_group);

		CompanyLocalServiceUtil.deleteCompany(_company);
	}

	@Test
	public void testCleanUpDeletesImageThumbnails() throws Exception {
		try (DestinationReplacer destinationReplacer = new DestinationReplacer(
				DestinationNames.DOCUMENT_LIBRARY_IMAGE_PROCESSOR,
				_ADAPTIVE_MEDIA_PROCESSOR)) {

			int count = _getThumbnailCount();

			_addPNGFileEntry();

			Assert.assertEquals(count + 1, _getThumbnailCount());

			_cleanUp();

			Assert.assertEquals(count, _getThumbnailCount());
		}
	}

	@Test
	public void testCleanUpDeletesOnlyImageThumbnails() throws Exception {
		try (DestinationReplacer destinationReplacer = new DestinationReplacer(
				DestinationNames.DOCUMENT_LIBRARY_IMAGE_PROCESSOR,
				_ADAPTIVE_MEDIA_PROCESSOR)) {

			int count = _getThumbnailCount();

			_addPDFFileEntry();
			_addPNGFileEntry();

			Assert.assertEquals(count + 2, _getThumbnailCount());

			_cleanUp();

			Assert.assertEquals(count + 1, _getThumbnailCount());
		}
	}

	@Test
	public void testMigrateDoesNotRemoveThumbnails() throws Exception {
		try (DestinationReplacer destinationReplacer = new DestinationReplacer(
				DestinationNames.DOCUMENT_LIBRARY_IMAGE_PROCESSOR,
				_ADAPTIVE_MEDIA_PROCESSOR)) {

			int count = _getThumbnailCount();

			_addPDFFileEntry();
			_addPNGFileEntry();

			Assert.assertEquals(count + 2, _getThumbnailCount());

			_migrate();

			Assert.assertEquals(count + 2, _getThumbnailCount());
		}
	}

	@Test
	public void testMigrateOnlyProcessesImages() throws Exception {
		try (DestinationReplacer destinationReplacer = new DestinationReplacer(
				DestinationNames.DOCUMENT_LIBRARY_IMAGE_PROCESSOR,
				_ADAPTIVE_MEDIA_PROCESSOR)) {

			FileEntry pdfFileEntry = _addPDFFileEntry();
			FileEntry pngFileEntry = _addPNGFileEntry();

			_migrate();

			Assert.assertEquals(0, _getAdaptiveMediaCount(pdfFileEntry));
			Assert.assertEquals(1, _getAdaptiveMediaCount(pngFileEntry));
		}
	}

	private static void _disableAdaptiveMediaThumbnails() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		ServiceComponentRuntime serviceComponentRuntime = registry.getService(
			ServiceComponentRuntime.class);

		Object service = registry.getService(_PROCESSOR_CLASS_NAME);

		if (service == null) {
			return;
		}

		Bundle bundle = FrameworkUtil.getBundle(service.getClass());

		ComponentDescriptionDTO componentDescriptionDTO =
			serviceComponentRuntime.getComponentDescriptionDTO(
				bundle, _PROCESSOR_CLASS_NAME);

		if (componentDescriptionDTO == null) {
			return;
		}

		Promise<Void> promise = serviceComponentRuntime.disableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private static void _enableAdaptiveMediaThumbnails() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		ServiceComponentRuntime serviceComponentRuntime = registry.getService(
			ServiceComponentRuntime.class);

		Object service = registry.getService(_COMMAND_CLASS_NAME);

		Bundle bundle = FrameworkUtil.getBundle(service.getClass());

		ComponentDescriptionDTO componentDescriptionDTO =
			serviceComponentRuntime.getComponentDescriptionDTO(
				bundle, _PROCESSOR_CLASS_NAME);

		if (componentDescriptionDTO == null) {
			return;
		}

		Promise<Void> promise = serviceComponentRuntime.enableComponent(
			componentDescriptionDTO);

		promise.getValue();
	}

	private FileEntry _addPDFFileEntry() throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			_user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".pdf", ContentTypes.APPLICATION_PDF,
			_getFileContents("sample.pdf"), new ServiceContext());
	}

	private FileEntry _addPNGFileEntry() throws Exception {
		_pngFileEntry = DLAppLocalServiceUtil.addFileEntry(
			_user.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".png", ContentTypes.IMAGE_PNG,
			_getFileContents("sample.png"), new ServiceContext());

		return _pngFileEntry;
	}

	private void _cleanUp() throws Exception {
		_run("cleanUp");
	}

	private long _getAdaptiveMediaCount(FileEntry fileEntry) throws Exception {
		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream =
			_finder.getAdaptiveMedia(
				queryBuilder -> queryBuilder.forFileEntry(fileEntry).
					forConfiguration(_THUMBNAIL_CONFIGURATION).done());

		return stream.count();
	}

	private byte[] _getFileContents(String fileName) throws Exception {
		return FileUtil.getBytes(
			AdaptiveMediaThumbnailsOSGiCommandsTest.class,
			"/com/liferay/adaptive/media/document/library/thumbnails/internal" +
				"/commands/test/dependencies/" + fileName);
	}

	private int _getThumbnailCount() throws Exception {
		String[] fileNames = DLStoreUtil.getFileNames(
			_company.getCompanyId(), DLPreviewableProcessor.REPOSITORY_ID,
			DLPreviewableProcessor.THUMBNAIL_PATH);

		return fileNames.length;
	}

	private void _migrate() throws Exception {
		_run("migrate");
	}

	private void _run(String functionName) throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		Object service = registry.getService(_COMMAND_CLASS_NAME);

		Class<?> clazz = service.getClass();

		Method method = clazz.getMethod(functionName, String[].class);

		method.invoke(
			service,
			(Object)new String[] {String.valueOf(_company.getCompanyId())});
	}

	private static final String _ADAPTIVE_MEDIA_PROCESSOR =
		"liferay/adaptive_media_processor";

	private static final String _COMMAND_CLASS_NAME =
		"com.liferay.adaptive.media.document.library.thumbnails.internal." +
			"commands.AdaptiveMediaThumbnailsOSGiCommands";

	private static final String _PROCESSOR_CLASS_NAME =
		"com.liferay.adaptive.media.document.library.thumbnails.internal." +
			"AdaptiveMediaImageProcessor";

	private static final String _THUMBNAIL_CONFIGURATION = "thumbnail";

	private static ImageAdaptiveMediaConfigurationHelper _configurationHelper;
	private static ServiceReference<ImageAdaptiveMediaConfigurationHelper>
		_configurationHelperServiceReference;
	private static ImageAdaptiveMediaFinder _finder;
	private static ServiceReference<ImageAdaptiveMediaFinder>
		_finderServiceReference;

	private Company _company;
	private Group _group;
	private FileEntry _pngFileEntry;
	private User _user;

}