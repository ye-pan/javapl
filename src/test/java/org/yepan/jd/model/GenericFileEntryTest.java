package org.yepan.jd.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class GenericFileEntryTest {

	@Test
	void testJarFileEntry() {
		
		GenericFileEntry jarFile = GenericFileEntry.newRootFileEntry(Path.of("src/test/resources/jar/spring-context-5.3.15.jar"));
		
	
		Collection<FileEntry> files = jarFile.getChildren();
		assertNotNull(files);
		
	}

}
