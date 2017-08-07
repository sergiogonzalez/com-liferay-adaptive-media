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

package com.liferay.adaptive.media.performance.internal;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringPool;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	service = AdaptiveMediaImagePerformanceBlogsEntryDemoDataCreator.class
)
public class AdaptiveMediaImagePerformanceBlogsEntryDemoDataCreator {

	public BlogsEntry create(ServiceContext serviceContext) throws Exception {
		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			serviceContext.getUserId(), "Adaptive Media Performance Blog Entry",
			_getContent(serviceContext), serviceContext);

		_blogEntries.add(blogsEntry);

		return blogsEntry;
	}

	public void delete() throws Exception {
		for (BlogsEntry blogEntry : _blogEntries) {
			_blogsEntryLocalService.deleteBlogsEntry(blogEntry);
		}

		for (FileEntry fileEntry : _fileEntries) {
			_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
		}
	}

	private FileEntry _createFile(String name, ServiceContext serviceContext)
		throws Exception {

		String fileName = PortletFileRepositoryUtil.getUniqueFileName(
			serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, name);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, "image/jpeg",
			FileUtil.getBytes(getClass(), name), serviceContext);

		_fileEntries.add(fileEntry);

		return fileEntry;
	}

	private String _getContent(ServiceContext serviceContext) throws Exception {
		int numberOfImages = 50;

		StringBuilder sb = new StringBuilder(numberOfImages);

		for (int i = 0; i < numberOfImages; i++) {
			sb.append(_getImgTag(_createFile("image.jpg", serviceContext)));
		}

		return sb.toString();
	}

	private String _getImgTag(FileEntry fileEntry) throws Exception {
		String previewURL = DLUtil.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, true);

		return "<img data-fileEntryId=\"" + fileEntry.getFileEntryId() +
			"\" src=\"" + previewURL + "\"/>";
	}

	private final List<BlogsEntry> _blogEntries = new ArrayList<>();

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	private final List<FileEntry> _fileEntries = new ArrayList<>();

	@Reference
	private GroupLocalService _groupLocalService;

}