package com.liferay.adaptive.media.image.configuration;

import com.liferay.adaptive.media.image.internal.configuration.ImageAdaptiveMediaConfigurationEntryImpl;

import java.util.Map;

/**
 * @author Alejandro Hernández
 */
public interface ImageAdaptiveMediaConfigurationEntryParser {

	public ImageAdaptiveMediaConfigurationEntryImpl parse(String s);

	public Map<String, String> parseProperties(String p);
}
