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
import com.liferay.adaptive.media.image.internal.configuration.AdaptiveMediaImageAttributeMapping;
import com.liferay.adaptive.media.image.internal.processor.AdaptiveMediaImage;
import com.liferay.adaptive.media.image.processor.AdaptiveMediaImageProcessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adolfo Pérez
 */
public class AMAttributeComparatorTest {

	@Before
	public void setUp() {
		Map<
			AMAttribute<AdaptiveMediaImageProcessor, ?>,
				AdaptiveMediaImageQueryBuilder.SortOrder>
					amAttributes = new HashMap<>();

		amAttributes.put(
			AMAttribute.getContentLengthAMAttribute(),
			AdaptiveMediaImageQueryBuilder.SortOrder.ASC);
		amAttributes.put(
			AMAttribute.getFileNameAMAttribute(),
			AdaptiveMediaImageQueryBuilder.SortOrder.DESC);

		_multiAMAttributeComparator = new AMAttributeComparator(amAttributes);
	}

	@Test
	public void testSortDifferentMediaByMultipleAttributes() {
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1 =
			_createMedia(
				AMAttribute.getContentLengthAMAttribute(), 10,
				AMAttribute.getFileNameAMAttribute(), "zzz");
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2 =
			_createMedia(
				AMAttribute.getContentLengthAMAttribute(), 10,
				AMAttribute.getFileNameAMAttribute(), "aaa");

		int result = _multiAMAttributeComparator.compare(
			adaptiveMedia1, adaptiveMedia2);

		Assert.assertEquals(-25, result);
	}

	@Test
	public void testSortDifferentMediaByMultipleAttributesInverse() {
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1 =
			_createMedia(
				AMAttribute.getContentLengthAMAttribute(), 10,
				AMAttribute.getFileNameAMAttribute(), "zzz");
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2 =
			_createMedia(
				AMAttribute.getContentLengthAMAttribute(), 10,
				AMAttribute.getFileNameAMAttribute(), "aaa");

		int result = _multiAMAttributeComparator.compare(
			adaptiveMedia2, adaptiveMedia1);

		Assert.assertEquals(25, result);
	}

	@Test
	public void testSortDifferentMediaByOneAttribute() {
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1 =
			_createMedia(AMAttribute.getContentLengthAMAttribute(), 10);
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2 =
			_createMedia(AMAttribute.getContentLengthAMAttribute(), 20);

		int result = _singleAMAttributeComparator.compare(
			adaptiveMedia1, adaptiveMedia2);

		Assert.assertEquals(-10, result);
	}

	@Test
	public void testSortDifferentMediaByOneAttributeInverse() {
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1 =
			_createMedia(AMAttribute.getContentLengthAMAttribute(), 10);
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2 =
			_createMedia(AMAttribute.getContentLengthAMAttribute(), 20);

		int result = _singleAMAttributeComparator.compare(
			adaptiveMedia2, adaptiveMedia1);

		Assert.assertEquals(10, result);
	}

	@Test
	public void testSortEqualMediaByMultipleAttributes() {
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1 =
			_createMedia(
				AMAttribute.getContentLengthAMAttribute(), 10,
				AMAttribute.getFileNameAMAttribute(), "aaa");
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2 =
			_createMedia(
				AMAttribute.getContentLengthAMAttribute(), 10,
				AMAttribute.getFileNameAMAttribute(), "aaa");

		int result = _singleAMAttributeComparator.compare(
			adaptiveMedia1, adaptiveMedia2);

		Assert.assertEquals(0, result);
	}

	@Test
	public void testSortEqualMediaByOneAttribute() {
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia1 =
			_createMedia(AMAttribute.getContentLengthAMAttribute(), 10);
		AdaptiveMedia<AdaptiveMediaImageProcessor> adaptiveMedia2 =
			_createMedia(AMAttribute.getContentLengthAMAttribute(), 10);

		int result = _singleAMAttributeComparator.compare(
			adaptiveMedia1, adaptiveMedia2);

		Assert.assertEquals(0, result);
	}

	private <S, T> AdaptiveMedia<AdaptiveMediaImageProcessor> _createMedia(
		AMAttribute<AdaptiveMediaImageProcessor, S> amAttribute1, S value1,
		AMAttribute<AdaptiveMediaImageProcessor, T> amAttribute2, T value2) {

		Map<String, String> properties = new HashMap<>();

		properties.put(amAttribute1.getName(), String.valueOf(value1));
		properties.put(amAttribute2.getName(), String.valueOf(value2));

		AdaptiveMediaImageAttributeMapping attributeMapping =
			AdaptiveMediaImageAttributeMapping.fromProperties(properties);

		return new AdaptiveMediaImage(() -> null, attributeMapping, null);
	}

	private <T> AdaptiveMedia<AdaptiveMediaImageProcessor> _createMedia(
		AMAttribute<AdaptiveMediaImageProcessor, T> amAttribute, T value) {

		AdaptiveMediaImageAttributeMapping attributeMapping =
			AdaptiveMediaImageAttributeMapping.fromProperties(
				Collections.singletonMap(
					amAttribute.getName(), String.valueOf(value)));

		return new AdaptiveMediaImage(() -> null, attributeMapping, null);
	}

	private AMAttributeComparator _multiAMAttributeComparator;
	private final AMAttributeComparator _singleAMAttributeComparator =
		new AMAttributeComparator(AMAttribute.getContentLengthAMAttribute());

}