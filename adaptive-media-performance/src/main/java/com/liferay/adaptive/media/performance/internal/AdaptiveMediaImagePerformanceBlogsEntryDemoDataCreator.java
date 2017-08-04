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
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.users.admin.demo.data.creator.BasicUserDemoDataCreator;

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

	public BlogsEntry create(long companyId) throws Exception {
		User user = _basicUserDemoDataCreator.create(
			companyId, "user1@liferay.com");

		Group guestGroup = _groupLocalService.getGroup(companyId, "Guest");

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			user.getUserId(), "Adaptive Media Performance Blog Entry",
			_getContent(user, guestGroup),
			_getServiceContext(guestGroup.getGroupId()));

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

		_basicUserDemoDataCreator.delete();
	}

	private FileEntry _createFile(long userId, long groupId, String name)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			userId, groupId, 0, name, "image/jpeg",
			FileUtil.getBytes(getClass(), name), new ServiceContext());

		_fileEntries.add(fileEntry);

		return fileEntry;
	}

	private String _getContent(User user, Group guestGroup) throws Exception {
		FileEntry image1 = _createFile(
			user.getUserId(), guestGroup.getGroupId(), "image.jpg");

		String previewURL = DLUtil.getPreviewURL(
			image1, image1.getFileVersion(), null, StringPool.BLANK, false,
			true);

		return "<img data-fileEntryId=\"" + image1.getFileEntryId() +
			"\" src=\"" + previewURL + "\">";
	}

	private ServiceContext _getServiceContext(long groupId) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(groupId);

		return serviceContext;
	}

	@Reference
	private BasicUserDemoDataCreator _basicUserDemoDataCreator;

	private final List<BlogsEntry> _blogEntries = new ArrayList<>();

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	private final List<FileEntry> _fileEntries = new ArrayList<>();

	@Reference
	private GroupLocalService _groupLocalService;

}