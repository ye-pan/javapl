package org.yepan.jd.model;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

public interface FileEntry {
	
	FileEntry getParent();
	
	URI getUri();
	
	String getPath();
	
	
	boolean isDirectory();
	
	long length();
	
	InputStream getInputStream();
	
	Collection<FileEntry> getChildren();
}
