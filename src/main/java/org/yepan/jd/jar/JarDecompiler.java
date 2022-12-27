package org.yepan.jd.jar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yepan.jd.JavaFiles;
import org.yepan.jd.model.FileEntry;
import org.yepan.jd.model.GenericFileEntry;

public class JarDecompiler {
	
	private static final Logger log = LoggerFactory.getLogger(JarDecompiler.class);
	
	private static final ClassFileToJavaSourceDecompiler DECOMPILER = new ClassFileToJavaSourceDecompiler();

	private final Path jarPath;
	private final Path output;
	
	public JarDecompiler(Path jarPath, Path output) {
		this.jarPath = Objects.requireNonNull(jarPath);
		this.output = Objects.requireNonNull(output);
	}
	
	
	public void decompiler() {
		Map<String, Object> configuration = new HashMap<>();
		configuration.put("realignLineNumbers", false);
		FileEntry jarFile = GenericFileEntry.newRootFileEntry(jarPath);
		decompilerFileEntry(jarFile, configuration);
		log.info("{}反编译到{}完成", jarPath, output);
	}


	private void decompilerFileEntry(FileEntry jarFile, Map<String, Object> configuration) {
		Collection<FileEntry> fileEntries = jarFile.getChildren();
		for (FileEntry fileEntry : fileEntries) {
			if (fileEntry.isDirectory()) {
				decompilerFileEntry(fileEntry, configuration);
			} else {
				String path = fileEntry.getPath();
				if (path.endsWith(".class")) {
					//.class文件，进行反编译处理
					if (path.contains("$") || path.endsWith("module-info.class")) {
						//内部类，匿名类，module描述文件，不处理
					} else {
						String internalName = path.substring(0, path.length() - 6); // 6 = ".class".length()
						Loader loader = new FileEntryLoader(fileEntry);
						Path outputPath = JavaFiles.make(output, internalName);
						try {
							Files.deleteIfExists(outputPath);
						} catch (IOException e) {
							log.error("反编译Java文件，删除已经存在的文件" + outputPath + "异常", e);
						}
						OutFilePrinter printer = new OutFilePrinter(outputPath);
						printer.setUnicodeEscape(false);
						try {
				            
							DECOMPILER.decompile(loader, printer, internalName, configuration);
						} catch (Exception e) {
							log.error("处理" + path + "异常", e);
						}
					}
				} else {
					//TODO 普通文件，直接按照目录层级拷贝
					/*
					 * Path outputPath = FileUtil.make(output, path); try {
					 * Files.deleteIfExists(outputPath); } catch(IOException e) {
					 * log.error("反编译Java文件，删除已经存在的文件" + outputPath + "异常", e); } try (InputStream
					 * in = fileEntry.getInputStream()) { Files.copy(in, outputPath); } catch
					 * (IOException e) { log.error("处理" + path + "异常", e); }
					 */
					
				}
			}
		}
	}
}
