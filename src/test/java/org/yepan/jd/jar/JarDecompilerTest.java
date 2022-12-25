package org.yepan.jd.jar;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class JarDecompilerTest {

	@Test
	void testDecompilerJar() {
		JarDecompiler jarDecompiler = new JarDecompiler(Path.of("src/test/resources/jar/spring-context-5.3.15.jar"), Path.of("src/test/resources/jar/output"));
		jarDecompiler.decompiler();
	}

}
