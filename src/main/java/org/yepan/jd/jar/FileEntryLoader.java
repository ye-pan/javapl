package org.yepan.jd.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.yepan.jd.model.FileEntry;

public class FileEntryLoader implements Loader {

	private FileEntry entry;
	
	public FileEntryLoader(FileEntry fileEntry) {
		this.entry = Objects.requireNonNull(fileEntry);
	}
	
	public FileEntry getEntry(String internalName) {
		String path = internalName + ".class";
		if (entry.getPath().equals(path)) {
			return entry;
		}

		for (FileEntry e : entry.getParent().getChildren()) {
			if (e.getPath().equals(path)) {
				return e;
			}
		}
		return null;
	}
	
	@Override
	public boolean canLoad(String internalName) {
		return getEntry(internalName) != null;
	}

	@Override
	public byte[] load(String internalName) throws LoaderException {
		FileEntry entry = getEntry(internalName);
		if (entry == null) {
			return null;
		} else {
			try (InputStream in = entry.getInputStream(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				byte[] buf = new byte[4096];
				int size = 0;
				while ((size = in.read(buf)) > 0) {
					bos.write(buf, 0, size);
				}
				return bos.toByteArray();
			} catch(IOException e) {
				throw new LoaderException(e);
			}
		}
	}
	
	

}
