package com.liferay.adaptive.media.image.jaxrs.internal;

import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.AdaptiveMediaAttribute;
import com.liferay.adaptive.media.AdaptiveMediaException;
import com.liferay.adaptive.media.image.finder.ImageAdaptiveMediaFinder;
import com.liferay.adaptive.media.image.finder.ImageAdaptiveMediaQueryBuilder;
import com.liferay.adaptive.media.image.processor.ImageAdaptiveMediaAttribute;
import com.liferay.adaptive.media.image.processor.ImageAdaptiveMediaProcessor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @author Alejandro Hernández
 */
public class ImageAdaptiveMediaFileVersionResource {

	public ImageAdaptiveMediaFileVersionResource(
		FileVersion fileVersion, ImageAdaptiveMediaFinder finder,
		UriBuilder uriBuilder) {

		_fileVersion = fileVersion;
		_finder = finder;
		_uriBuilder = uriBuilder;
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

		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream =
			_getAdaptiveMediaStream(query);

		return _getFirstAdaptiveMedia(stream);
	}

	@GET
	@Path("/variants")
	@Produces({"application/json", "application/xml"})
	public Response getVariants(@QueryParam("q") String query)
		throws AdaptiveMediaException, PortalException {

		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream =
			_getAdaptiveMediaStream(query);

		return _getImageAdaptiveMediaList(stream);
	}

	@GET
	@Path("/variants/ordered")
	@Produces({"application/json", "application/xml"})
	public Response getVariantsOrdered(
			@QueryParam("orderBy") String attribute,
			@QueryParam("ascending") Boolean ascending)
		throws AdaptiveMediaException, PortalException {

		if ((ascending == null) ||
			!ImageAdaptiveMediaAttribute.allowedAttributes().containsKey(
				attribute)) {

			throw new BadRequestException();
		}

		AdaptiveMediaAttribute adaptiveMediaAttribute =
			ImageAdaptiveMediaAttribute.allowedAttributes().get(attribute);

		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream =
			_finder.getAdaptiveMedia(
				queryBuilder -> queryBuilder.forVersion(_fileVersion).orderBy(
					adaptiveMediaAttribute, ascending).done());

		return _getImageAdaptiveMediaList(stream);
	}

	private Map<AdaptiveMediaAttribute<ImageAdaptiveMediaProcessor, Object>,
		Object> _getAdaptiveMediaAttributes(String query) {

		String pattern = "([a-z]|-|_)+=(\\d+)(;(\\w+)=(\\d+))*";

		if ((query == null) || !query.matches(pattern)) {
			throw new BadRequestException();
		}

		String[] attributes = _ATTRIBUTE_SEPARATOR_PATTERN.split(query);

		Map<AdaptiveMediaAttribute<ImageAdaptiveMediaProcessor, Object>, Object>
			properties = new HashMap<>();

		for (String attribute : attributes) {
			String[] keyValuePair = _KEY_VALUE_SEPARATOR_PATTERN.split(
				attribute);

			if (ImageAdaptiveMediaAttribute.allowedAttributes().containsKey(
					keyValuePair[0])) {

				AdaptiveMediaAttribute adaptiveMediaAttribute =
					ImageAdaptiveMediaAttribute.allowedAttributes().get(
						keyValuePair[0]);

				properties.put(adaptiveMediaAttribute, adaptiveMediaAttribute.
					convert(keyValuePair[1]));
			}
		}

		return properties;
	}

	private Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>>
		_getAdaptiveMediaStream(String query) throws AdaptiveMediaException,
		PortalException {

		return _finder.getAdaptiveMedia(
			queryBuilder -> {
				ImageAdaptiveMediaQueryBuilder.InitialStep step =
					queryBuilder.forVersion(_fileVersion);

				_getAdaptiveMediaAttributes(query).forEach(step::with);

				return step.done();
			});
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

	private Response _getImageAdaptiveMediaList(
		Stream<AdaptiveMedia<ImageAdaptiveMediaProcessor>> stream) {

		UriBuilder uriBuilder = _uriBuilder.path(
			ImageAdaptiveMediaFileVersionResource.class, "getConfiguration");

		List<ImageAdaptiveMediaRepr> adaptiveMedias = stream.map(adaptiveMedia
			-> new ImageAdaptiveMediaRepr(adaptiveMedia, uriBuilder.clone(),
			_fileVersion.getFileVersionId())).collect(Collectors.toList());

		GenericEntity<List<ImageAdaptiveMediaRepr>> entity =
			new GenericEntity<List<ImageAdaptiveMediaRepr>>(adaptiveMedias) {
			};

		return Response.ok(entity).build();
	}

	private static final Pattern _ATTRIBUTE_SEPARATOR_PATTERN = Pattern.compile(
		"\\s*;\\s*");

	private static final Pattern _KEY_VALUE_SEPARATOR_PATTERN = Pattern.compile(
		"\\s*=\\s*");

	private final FileVersion _fileVersion;
	private final ImageAdaptiveMediaFinder _finder;
	private final UriBuilder _uriBuilder;

}