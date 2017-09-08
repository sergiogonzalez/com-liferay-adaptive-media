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

package com.liferay.adaptive.media.image.finder;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.finder.AMQuery;
import com.liferay.adaptive.media.finder.AMQueryBuilder;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.processor.AMImageProcessor;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Adolfo Pérez
 */
public interface AMImageQueryBuilder
	extends AMQueryBuilder<FileVersion, AMImageProcessor> {

	public InitialStep forFileEntry(FileEntry fileEntry);

	public InitialStep forFileVersion(FileVersion fileVersion);

	public enum ConfigurationStatus {

		ANY(amImageConfigurationEntry -> true),
		ENABLED(AMImageConfigurationEntry::isEnabled),
		DISABLED(
			amImageConfigurationEntry ->
				!amImageConfigurationEntry.isEnabled());

		public Predicate<AMImageConfigurationEntry> getPredicate() {
			return _predicate;
		}

		private ConfigurationStatus(
			Predicate<AMImageConfigurationEntry> predicate) {

			_predicate = predicate;
		}

		private final Predicate<AMImageConfigurationEntry> _predicate;

	}

	public interface ConfigurationStep {

		public FinalStep forConfiguration(String configurationUuid);

		public InitialStep withConfigurationStatus(
			ConfigurationStatus configurationStatus);

	}

	public interface FinalStep {

		public AMQuery<FileVersion, AMImageProcessor> done();

	}

	public interface FuzzySortStep extends FinalStep {

		public <V> FuzzySortStep with(
			AMAttribute<AMImageProcessor, V> amAttribute,
			Optional<V> valueOptional);

		public <V> FuzzySortStep with(
			AMAttribute<AMImageProcessor, V> amAttribute, V value);

	}

	public interface InitialStep
		extends ConfigurationStep, FuzzySortStep, StrictSortStep {
	}

	public enum SortOrder {

		ASC {

			@Override
			public int getSortValue(int value) {
				return value;
			}

		},

		DESC {

			@Override
			public int getSortValue(int value) {
				return -value;
			}

		};

		public abstract int getSortValue(int value);

	}

	public interface StrictSortStep extends FinalStep {

		public <V> StrictSortStep orderBy(
			AMAttribute<AMImageProcessor, V> amAttribute, SortOrder sortOrder);

	}

}