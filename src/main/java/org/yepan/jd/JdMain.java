package org.yepan.jd;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yepan.jd.exception.IllegalArgsException;
import org.yepan.jd.jar.JarDecompiler;

public class JdMain {
	private static final Logger log = LoggerFactory.getLogger(JdMain.class);

	public static void main(String[] args) {
		if (args == null || args.length != 2) {
			throw new IllegalArgsException("", "2", "%s参数数目应该为%s");
		}
		String src = args[0];
		log.info("Jar file path: {}", src);
		String output = args[1];
		log.info("Output path: {}", output);
		final Path outputPath = Path.of(output);

		File file = new File(src);
		File[] files = file.listFiles();
		if (files == null || files.length == 0) {
			throw new IllegalArgsException("", src, "%s%s对应文件下没有待反编译的jar包");
		}
		List<Path> decompilerJars = new ArrayList<>();
		for (File jar : files) {
			if (jar.isFile() && jar.getName().endsWith(".jar")) {
				final Path jarPath = jar.toPath();
				decompilerJars.add(jarPath);
			}

		}

		ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
		CountDownLatch latch = new CountDownLatch(decompilerJars.size());
		decompilerJars.forEach((path) -> {
			executor.execute(() -> {
				try {
					JarDecompiler jarDecompiler = new JarDecompiler(path, outputPath);
					jarDecompiler.decompiler();
				} finally {
					latch.countDown();
				}
			});
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
