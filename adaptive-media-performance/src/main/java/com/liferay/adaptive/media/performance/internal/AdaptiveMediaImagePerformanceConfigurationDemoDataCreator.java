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

import com.liferay.adaptive.media.exception.AdaptiveMediaImageConfigurationException;
import com.liferay.adaptive.media.image.configuration.AdaptiveMediaImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AdaptiveMediaImageConfigurationHelper;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	service = AdaptiveMediaImagePerformanceConfigurationDemoDataCreator.class
)
public class AdaptiveMediaImagePerformanceConfigurationDemoDataCreator {

	public Collection<AdaptiveMediaImageConfigurationEntry> create(
			long companyId)
		throws Exception {

		_processOriginalEntries(companyId);

		return Arrays.asList(
			_create(companyId, "Thumbnails", 300, 300),
			_create(companyId, "Mobile", 320, 480),
			_create(companyId, "Mobile 2", 640, 960),
			_create(companyId, "Tablet", 640, 360),
			_create(companyId, "Tablet HD", 1280, 720),
			_create(companyId, "Desktop", 1920, 1080),
			_create(companyId, "HD", 2560, 1440));
	}

	public void delete() throws Exception {
		for (Long companyId : _configurationIds.keySet()) {
			List<String> uuids = _configurationIds.get(companyId);

			for (String uuid : uuids) {
				_adaptiveMediaImageConfigurationHelper.
					forceDeleteAdaptiveMediaImageConfigurationEntry(
						companyId, uuid);

				uuids.remove(uuid);
			}
		}

		_restoreOriginalEntries();
	}

	private void _addConfigurationUuid(long companyId, String uuid) {
		_configurationIds.computeIfAbsent(
			companyId, k -> new CopyOnWriteArrayList<>());

		List<String> uuids = _configurationIds.get(companyId);

		uuids.add(uuid);
	}

	private AdaptiveMediaImageConfigurationEntry _create(
			long companyId, String name, int maxWidth, int maxHeight)
		throws Exception {

		AdaptiveMediaImageConfigurationEntry configurationEntry = null;

		Map<String, String> properties = new HashMap<>();

		properties.put("max-height", String.valueOf(maxHeight));
		properties.put("max-width", String.valueOf(maxWidth));

		configurationEntry =
			_adaptiveMediaImageConfigurationHelper.
				addAdaptiveMediaImageConfigurationEntry(
					companyId, name, "For performance testing",
					UUID.randomUUID().toString(), properties);

		_addConfigurationUuid(companyId, configurationEntry.getUUID());

		return configurationEntry;
	}

	private void _processOriginalEntries(long companyId) throws IOException {
		Collection<AdaptiveMediaImageConfigurationEntry> originalEntries =
			_adaptiveMediaImageConfigurationHelper.
				getAdaptiveMediaImageConfigurationEntries(companyId);

		for (AdaptiveMediaImageConfigurationEntry entry : originalEntries) {
			_adaptiveMediaImageConfigurationHelper.
				forceDeleteAdaptiveMediaImageConfigurationEntry(
					companyId, entry.getUUID());
		}

		_originalEntriesMap.put(companyId, originalEntries);
	}

	private void _restoreOriginalEntries()
		throws AdaptiveMediaImageConfigurationException, IOException {

		for (Long companyId : _originalEntriesMap.keySet()) {
			Collection<AdaptiveMediaImageConfigurationEntry> entries =
				_originalEntriesMap.get(companyId);

			for (AdaptiveMediaImageConfigurationEntry entry : entries) {
				_adaptiveMediaImageConfigurationHelper.
					addAdaptiveMediaImageConfigurationEntry(
						companyId, entry.getName(), entry.getDescription(),
						entry.getUUID(), entry.getProperties());
			}

			_originalEntriesMap.remove(companyId);
		}
	}

	@Reference
	private AdaptiveMediaImageConfigurationHelper
		_adaptiveMediaImageConfigurationHelper;

	private final Map<Long, List<String>> _configurationIds = new HashMap<>();
	private final Map<Long, Collection<AdaptiveMediaImageConfigurationEntry>>
		_originalEntriesMap = new HashMap<>();

}