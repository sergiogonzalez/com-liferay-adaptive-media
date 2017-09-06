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

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.exception.AdaptiveMediaRuntimeException;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageAttribute;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class AdaptiveMediaImageAttributeTest {

	@Test
	public void testAllPublicAttributesAreSupported() {
		Collection<AMAttribute<?, ?>> publicAMAttributes = Arrays.asList(
			AMAttribute.getConfigurationUuidAMAttribute(),
			AMAttribute.getContentLengthAMAttribute(),
			AMAttribute.getContentTypeAMAttribute(),
			AMAttribute.getFileNameAMAttribute(),
			AdaptiveMediaImageAttribute.IMAGE_HEIGHT,
			AdaptiveMediaImageAttribute.IMAGE_WIDTH);

		Map<String, AMAttribute<?, ?>> allowedAMAttributesMap =
			AdaptiveMediaImageAttribute.getAllowedAMAttributes();

		Collection<AMAttribute<?, ?>> allowedAMAttributes =
			allowedAMAttributesMap.values();

		Assert.assertTrue(allowedAMAttributes.containsAll(publicAMAttributes));
	}

	@Test(
		expected = AdaptiveMediaRuntimeException.AdaptiveMediaAttributeFormatException.class
	)
	public void testImageHeightFailsForNonIntegers() {
		AdaptiveMediaImageAttribute.IMAGE_HEIGHT.convert("xyz");
	}

	@Test
	public void testImageHeightRecognizesIntegers() {
		int result = AdaptiveMediaImageAttribute.IMAGE_HEIGHT.convert("42");

		Assert.assertEquals(42, result);
	}

	@Test(
		expected = AdaptiveMediaRuntimeException.AdaptiveMediaAttributeFormatException.class
	)
	public void testImageWidthFailsForNonIntegers() {
		AdaptiveMediaImageAttribute.IMAGE_WIDTH.convert("xyz");
	}

	@Test
	public void testImageWidthRecognizesIntegers() {
		int result = AdaptiveMediaImageAttribute.IMAGE_WIDTH.convert("42");

		Assert.assertEquals(42, result);
	}

}