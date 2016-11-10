package com.liferay.adaptive.media.image.jaxrs;

import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.AdaptiveMediaAttribute;
import com.liferay.adaptive.media.AdaptiveMediaException;
import com.liferay.adaptive.media.image.configuration.ImageAdaptiveMediaConfigurationEntryParser;
import com.liferay.adaptive.media.image.finder.ImageAdaptiveMediaFinder;
import com.liferay.adaptive.media.image.internal.util.Tuple;
import com.liferay.adaptive.media.image.processor.ImageAdaptiveMediaAttribute;
import com.liferay.adaptive.media.image.processor.ImageAdaptiveMediaProcessor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author Alejandro Hernández
 */
public class AdaptiveMediaImageFileVersionResource {

	public AdaptiveMediaImageFileVersionResource(
		FileVersion fileVersion, ImageAdaptiveMediaFinder finder,
		ImageAdaptiveMediaConfigurationEntryParser parser) {

		_fileVersion = fileVersion;
		_finder = finder;
		_parser = parser;
	}

	@GET
	@Path("/config/{uuid}")
	@Produces("image")
	public Response getConfiguration(@PathParam("uuid") String uuid)
		throws AdaptiveMediaException, PortalException {

		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream =
			_finder.getAdaptiveMedia(
				queryBuilder -> queryBuilder.forVersion(_fileVersion).
					forConfiguration(uuid).done());

		return _getFirstAdaptiveMedia(stream);
	}

	@GET
	@Path("/data")
	@Produces("image")
	public Response getData(@QueryParam("q") String query)
		throws AdaptiveMediaException, PortalException {
		try {
			Map<String, String> properties = _parser.parseProperties(query);

			Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream =
				_finder.getAdaptiveMedia(
					queryBuilder -> queryBuilder.forVersion(_fileVersion).withAll(_getAttributes(properties)).done());

			return _getFirstAdaptiveMedia(stream);
		}
		catch (IllegalArgumentException e) {
			throw new BadRequestException();
		}
	}

	private Map<AdaptiveMediaAttribute<ImageAdaptiveMediaProcessor, Integer>, Integer> _getAttributes(
		Map<String, String> properties) {
		Map<AdaptiveMediaAttribute<ImageAdaptiveMediaProcessor, Integer>, Integer> attributes =
			new HashMap<>();

		for(String key : properties.keySet()) {
			switch (key) {
				case "width":
					attributes.put(ImageAdaptiveMediaAttribute.IMAGE_WIDTH,
						Integer.valueOf(properties.get(key)));
					break;
				case "height":
					attributes.put(ImageAdaptiveMediaAttribute.IMAGE_HEIGHT,
						Integer.valueOf(properties.get(key)));
					break;
				default:
					break;
			}
		}

		return attributes;
	}


	private Response _getFirstAdaptiveMedia(
		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream) {
		Optional<AdaptiveMedia<ImageAdaptiveMediaProcessor>> adaptiveMedia =
			stream.findFirst();

		if (!adaptiveMedia.isPresent()) {
			throw new NotFoundException();
		}

		return Response.status(200).type(_fileVersion.getMimeType()).entity(
			adaptiveMedia.get().getInputStream()).build();
	}

	private final FileVersion _fileVersion;
	private final ImageAdaptiveMediaFinder _finder;
	private final ImageAdaptiveMediaConfigurationEntryParser _parser;

}