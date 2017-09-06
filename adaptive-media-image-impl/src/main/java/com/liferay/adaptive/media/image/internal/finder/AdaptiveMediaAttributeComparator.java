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
import com.liferay.adaptive.media.image.finder.AdaptiveMediaImageQueryBuilder;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageProcessor;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sergio González
 */
public class AdaptiveMediaAttributeComparator
	implements Comparator<AdaptiveMedia<AdaptiveMediaImageProcessor>> {

	public AdaptiveMediaAttributeComparator(
		AMAttribute<AdaptiveMediaImageProcessor, ?> amAttribute) {

		this(
			Collections.singletonMap(
				amAttribute, AdaptiveMediaImageQueryBuilder.SortOrder.ASC));
	}

	public AdaptiveMediaAttributeComparator(
		AMAttribute<AdaptiveMediaImageProcessor, ?> amAttribute,
		AdaptiveMediaImageQueryBuilder.SortOrder sortOrder) {

		this(Collections.singletonMap(amAttribute, sortOrder));
	}

	public AdaptiveMediaAttributeComparator(
		Map
			<AMAttribute<AdaptiveMediaImageProcessor, ?>,
				AdaptiveMediaImageQueryBuilder.SortOrder> sortCriteria) {

		_sortCriteria = (Map)sortCriteria;
	}

	@Override
	public int compare(
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1,
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2) {

		for (Map.Entry
				<AMAttribute<AdaptiveMediaImageProcessor, Object>,
					AdaptiveMediaImageQueryBuilder.SortOrder> sortCriterion :
						_sortCriteria.entrySet()) {

			AMAttribute<AdaptiveMediaImageProcessor, Object> amAttribute =
				sortCriterion.getKey();

			Optional<?> value1Optional = adaptiveMedia1.getValueOptional(
				amAttribute);
			Optional<?> value2Optional = adaptiveMedia2.getValueOptional(
				amAttribute);

			Optional<Integer> valueOptional = value1Optional.flatMap(
				value1 -> value2Optional.map(
					value2 -> amAttribute.compare(value1, value2)));

			AdaptiveMediaImageQueryBuilder.SortOrder sortOrder =
				sortCriterion.getValue();

			int result = valueOptional.map(
				sortOrder::getSortValue
			).orElse(
				0
			);

			if (result != 0) {
				return result;
			}
		}

		return 0;
	}

	private final Map
		<AMAttribute<
			AdaptiveMediaImageProcessor, Object>,
				AdaptiveMediaImageQueryBuilder.SortOrder> _sortCriteria;

}