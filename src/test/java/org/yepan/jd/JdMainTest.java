package org.yepan.jd;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.yepan.jd.exception.IllegalArgsException;

class JdMainTest {

	@Test
	void test() {
		assertThrows(IllegalArgsException.class, ()->{
			JdMain.main(null);
		});
		
	}

	@Test
	void testWithJar() {
		assertThrows(IllegalArgsException.class, ()->{
			JdMain.main(new String[] {"D:\\Seeyon\\A8V82\\v8.2\\ApacheJetspeed\\webapps\\seeyon\\WEB-INF\\lib\\seeyon-ctp-core.jar"});
		});
	}
	
	@Test
	void testWithJarAndOutput() {
		JdMain.main(new String[] {"D:\\Seeyon\\A8V82\\v8.2\\ApacheJetspeed\\webapps\\seeyon\\WEB-INF\\lib\\seeyon-ctp-core.jar", "D:\\data\\output"});
	}
}
