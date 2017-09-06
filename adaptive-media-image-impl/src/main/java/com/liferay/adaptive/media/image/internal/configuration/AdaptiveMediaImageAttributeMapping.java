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

package com.liferay.adaptive.media.image.internal.configuration;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageAttribute;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Gives convenient access to a set of media attributes. It offers a type-safe
 * interface to access attribute values, accepting only attributes of the
 * correct type (those for adaptive images), and returning values of the correct
 * type.
 *
 * @author Adolfo Pérez
 */
public class AdaptiveMediaImageAttributeMapping {

	/**
	 * Returns an {@link AdaptiveMediaImageAttributeMapping} that uses the map
	 * as the underlying attribute storage.
	 *
	 * @param  properties the map to get the properties from
	 * @return a non-<code>null</code> mapping that provides type-safe access to
	 *         an underlying map
	 */
	public static AdaptiveMediaImageAttributeMapping fromProperties(
		Map<String, String> properties) {

		if (properties == null) {
			throw new IllegalArgumentException("properties map cannot be null");
		}

		Map<AMAttribute<AdaptiveMediaImageProcessor, ?>, Optional<?>>
			amAttributes = new HashMap<>();

		amAttributes.put(
			AMAttribute.getConfigurationUuidAMAttribute(),
			_getValueOptional(
				properties, AMAttribute.getConfigurationUuidAMAttribute()));
		amAttributes.put(
			AMAttribute.getContentLengthAMAttribute(),
			_getValueOptional(
				properties, AMAttribute.getContentLengthAMAttribute()));
		amAttributes.put(
			AMAttribute.getContentTypeAMAttribute(),
			_getValueOptional(
				properties, AMAttribute.getContentTypeAMAttribute()));
		amAttributes.put(
			AMAttribute.getFileNameAMAttribute(),
			_getValueOptional(
				properties, AMAttribute.getFileNameAMAttribute()));
		amAttributes.put(
			AdaptiveMediaImageAttribute.IMAGE_HEIGHT,
			_getValueOptional(
				properties, AdaptiveMediaImageAttribute.IMAGE_HEIGHT));
		amAttributes.put(
			AdaptiveMediaImageAttribute.IMAGE_WIDTH,
			_getValueOptional(
				properties, AdaptiveMediaImageAttribute.IMAGE_WIDTH));

		return new AdaptiveMediaImageAttributeMapping(amAttributes);
	}

	/**
	 * Returns an {@link Optional} instance that contains the value of the
	 * attribute (if any) in this mapping.
	 *
	 * @param  amAttribute a non <code>null</code> attribute
	 * @return a non-<code>null</code> optional that contains the
	 *         (non-<code>null</code>) value (if any)
	 */
	public <V> Optional<V> getValueOptional(
		AMAttribute<AdaptiveMediaImageProcessor, V> amAttribute) {

		if (amAttribute == null) {
			throw new IllegalArgumentException("attribute cannot be null");
		}

		return (Optional<V>)_amAttributes.get(amAttribute);
	}

	protected AdaptiveMediaImageAttributeMapping(
		Map<AMAttribute<AdaptiveMediaImageProcessor, ?>, Optional<?>>
			amAttributes) {

		_amAttributes = amAttributes;
	}

	private static <V> Optional<V> _getValueOptional(
		Map<String, String> properties,
		AMAttribute<AdaptiveMediaImageProcessor, V> amAttribute) {

		String value = properties.get(amAttribute.getName());

		if (value == null) {
			return Optional.empty();
		}

		return Optional.of(amAttribute.convert(value));
	}

	private final Map
		<AMAttribute<AdaptiveMediaImageProcessor, ?>, Optional<?>>
			_amAttributes;

}