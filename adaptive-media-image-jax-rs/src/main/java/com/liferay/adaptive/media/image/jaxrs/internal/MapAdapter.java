/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.adaptive.media.image.jaxrs.internal;

import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Alejandro Hernández
 */
public class MapAdapter extends XmlAdapter<Element, Map<String, Object>> {

	public MapAdapter() throws Exception {
		_documentBuilder =
			SecureXMLFactoryProviderUtil.newDocumentBuilderFactory().
				newDocumentBuilder();
	}

	@Override
	public Element marshal(Map<String, Object> map) throws Exception {
		Document document = _documentBuilder.newDocument();

		Element rootElement = document.createElement("properties");

		document.appendChild(rootElement);

		map.forEach((k, v) -> {
			Element childElement = document.createElement(k);

			childElement.setTextContent(v.toString());
			rootElement.appendChild(childElement);
		});

		return rootElement;
	}

	@Override
	public Map<String, Object> unmarshal(Element rootElement) throws Exception {
		throw new UnsupportedOperationException();
	}

	private DocumentBuilder _documentBuilder;

}