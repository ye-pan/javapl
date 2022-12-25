package org.yepan.jd.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GenericFileEntry implements FileEntry {

	protected Path root;
	protected URI rootUri;
	protected FileEntry parent;
	protected Path fsPath;
	protected String pathStr;
	protected URI uri;
	protected boolean isDirectory;
	protected Collection<FileEntry> children;

	private GenericFileEntry(Path root, URI rootUri, FileEntry parent, Path fsPath) {
		this.root = root;
		this.rootUri = rootUri;
		this.parent = parent;
		this.fsPath = fsPath;
	}

	public static GenericFileEntry newRootFileEntry(Path fsPath) {
		Objects.requireNonNull(fsPath);
		URI rootUri = fsPath.toUri();
		GenericFileEntry fileEntry = new GenericFileEntry(fsPath, rootUri, null, fsPath);
		return fileEntry;
	}

	@Override
	public FileEntry getParent() {
		return parent;
	}

	@Override
	public URI getUri() {
		if (uri != null) {
			return uri;
		}
		if (isRoot()) {
			if (rootUri != null) {
				this.uri = rootUri;
			} else {
				throw new IllegalStateException("顶层文件实体URI不能为空");
			}
		} else {
			try {
				this.uri = new URI(rootUri.getScheme(), rootUri.getHost(), rootUri.getPath() + getPath(), null);
			} catch (URISyntaxException e) {
				e.printStackTrace();

			}
		}

		return uri;
	}

	@Override
	public String getPath() {
		if (pathStr != null) {
			return pathStr;
		}
		if (isRoot()) {
			return "";
		} else {
			pathStr = fsPath.subpath(root.getNameCount(), fsPath.getNameCount()).toString()
					.replace(fsPath.getFileSystem().getSeparator(), "/");
			int length = pathStr.length();
			if (length > 0 && pathStr.charAt(length - 1) == '/') {
				pathStr = pathStr.substring(0, length - 1);
			}
		}
		return pathStr;
	}

	@Override
	public boolean isDirectory() {
		return Files.isDirectory(fsPath);
	}

	@Override
	public long length() {
		try {
			return Files.size(fsPath);
		} catch (IOException e) {
			e.printStackTrace();
			return -1L;
		}
	}

	@Override
	public InputStream getInputStream() {
		try {
			return Files.newInputStream(fsPath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<FileEntry> getChildren() {
		try {
			if (Files.isDirectory(fsPath)) {
				// 加载子目录
				return loadChildrenFromDirectory();
			} else {
				// 如果是文件，加载所有文件
				return loadChildrenFromFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Collection<FileEntry> loadChildrenFromFile() throws IOException {
		StringBuilder suffix = new StringBuilder(".").append(System.currentTimeMillis()).append(".")
				.append(fsPath.getFileName().toString());
		File tempFile = File.createTempFile("jd-tool.tmp", suffix.toString());
		Path tempPath = tempFile.toPath();
		Files.delete(tempPath);

		Files.copy(fsPath, tempPath);
		FileSystem subFileSystem = FileSystems.newFileSystem(tempPath);
		if (subFileSystem != null) {
			Iterator<Path> rootDirs = subFileSystem.getRootDirectories().iterator();
			if (rootDirs.hasNext()) {
				Path rootPath = rootDirs.next();
				return GenericFileEntry.newRootFileEntry(rootPath).getChildren();
			}
		}
		tempFile.delete();
		return Collections.emptyList();
	}

	private Collection<FileEntry> loadChildrenFromDirectory() throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(fsPath)) {
			// FIXME 后续如果ArrayList更好，需要更换
			final List<FileEntry> childrenFileEntries = new LinkedList<>();

			int parentNameCount = fsPath.getNameCount();
			stream.forEach((path) -> {
				if (path.getNameCount() > parentNameCount) {
					childrenFileEntries.add(createChild(path));
				}
			});
			return childrenFileEntries;
		}
	}

	private boolean isRoot() {
		return parent == null;
	}

	protected Path getRoot() {
		return this.root;
	}

	private FileEntry createChild(Path fsPath) {
		return new GenericFileEntry(this.root, this.rootUri, this, fsPath);
	}
}
