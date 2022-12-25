package org.yepan.jd;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class JavaFiles {
	
	public static Path make(Path output, String internalName) {
		Path javaFilePath = Path.of(output.toString(), internalName + ".java");
		File parentFile = javaFilePath.getParent().toFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		return javaFilePath;
	}
	
	public static void close(Closeable... objs) {
		for (Closeable closeable : objs) {
			if (closeable != null) {
				try {
					closeable.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
