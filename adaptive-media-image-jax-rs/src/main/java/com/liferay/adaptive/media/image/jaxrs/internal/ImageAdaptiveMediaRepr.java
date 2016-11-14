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

package com.liferay.adaptive.media.image.jaxrs.internal;

import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.AdaptiveMediaAttribute;
import com.liferay.adaptive.media.image.processor.ImageAdaptiveMediaProcessor;

import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alejandro Hernández
 */
@XmlRootElement(name = "variant")
public class ImageAdaptiveMediaRepr {

	public ImageAdaptiveMediaRepr() {
	}

	public ImageAdaptiveMediaRepr(
		AdaptiveMedia<ImageAdaptiveMediaProcessor> adaptiveMedia,
		UriBuilder uriBuilder, long fileVersionId) {

		_uri =
			uriBuilder.build(Long.toString(fileVersionId), adaptiveMedia.
				getAttributeValue(AdaptiveMediaAttribute.configId()).get()).
				toString();
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	public String getUri() {
		return _uri;
	}

	public void setProperties(Map<String, Object> properties) {
		_properties = properties;
	}

	public void setUri(String uri) {
		_uri = uri;
	}

	private Map<String, Object> _properties;
	private String _uri;

}