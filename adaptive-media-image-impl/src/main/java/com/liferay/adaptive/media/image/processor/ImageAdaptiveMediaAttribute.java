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

package com.liferay.adaptive.media.image.processor;

import com.liferay.adaptive.media.AdaptiveMediaAttribute;
import com.liferay.adaptive.media.AdaptiveMediaRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public final class ImageAdaptiveMediaAttribute {

	/**
	 * Returns a string-attribute map containing all the name-attribute pairs
	 * allowed to be used.
	 *
	 * @return the list of attributes allowed
	 */
	public static Map<String, AdaptiveMediaAttribute<?, ?>> allowedAttributes() {
		Map<String, AdaptiveMediaAttribute<?, ?>> attributes =
			AdaptiveMediaAttribute.allowedAttributes();

		attributes.put(ImageAdaptiveMediaAttribute.IMAGE_WIDTH.getName(),
			ImageAdaptiveMediaAttribute.IMAGE_WIDTH);
		attributes.put(ImageAdaptiveMediaAttribute.IMAGE_HEIGHT.getName(),
			ImageAdaptiveMediaAttribute.IMAGE_HEIGHT);

		return attributes;
	}

	public static final AdaptiveMediaAttribute<
		ImageAdaptiveMediaProcessor, Integer>
			IMAGE_HEIGHT = new AdaptiveMediaAttribute<>(
				"height", ImageAdaptiveMediaAttribute::_parseInt,
				ImageAdaptiveMediaAttribute::_intDistance);

	public static final AdaptiveMediaAttribute<
		ImageAdaptiveMediaProcessor, Integer>
			IMAGE_WIDTH = new AdaptiveMediaAttribute<>(
				"width", ImageAdaptiveMediaAttribute::_parseInt,
				ImageAdaptiveMediaAttribute::_intDistance);

	private static int _intDistance(int i1, int i2) {
		return i1 - i2;
	}

	private static int _parseInt(String value) {
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException nfe) {
			throw new AdaptiveMediaRuntimeException.
				AdaptiveMediaAttributeFormatException(nfe);
		}
	}

	private ImageAdaptiveMediaAttribute() {
	}

}