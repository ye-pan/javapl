package org.yepan.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    /**
     * 删除文件，如果是文件夹会删除整个文件夹
     *
     * @param path 指定文件路径
     * @throws IOException ioe
     */
    public static void delete(Path path) throws IOException {
        deleteFile(path.toFile());
        Files.deleteIfExists(path);
    }

    /**
     * 清空目录，只会删除目录下的所有文件
     *
     * @param path
     * @throws IOException
     */
    public static void clear(Path path) throws IOException {
        deleteFile(path.toFile());
    }

    /**
     * 删除文件，如果是文件夹会删除整个文件夹
     *
     * @param file 指定文件
     * @throws IOException ioe
     */
    private static void deleteFile(File file) throws IOException {
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (File subFile : files) {
            if (!subFile.isFile()) {
                deleteFile(subFile);
            }
            Files.deleteIfExists(subFile.toPath());
        }
    }

    /**
     * 获取指定文件夹下所有文件列表，且文件列表是基于path的相对路径
     * 
     * @param path 指定文件
     * @return relativeSubPath
     */
    public static List<String> walkFileTreeRelative(Path path, int subtractStart) {
        File file = path.toFile();
        if (!file.exists()) {
            return Collections.emptyList();
        }
        List<String> paths = new LinkedList<>();
        walkFileTreeSubtractPath(file, subtractStart, paths);
        return paths;
    }

    private static void walkFileTreeSubtractPath(File current, int subtractStart, List<String> relativePaths) {
        File[] files = current.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                Path path = file.toPath();
                Path subPath = path.subpath(subtractStart, path.getNameCount());
                relativePaths.add(subPath.toString());
            } else {
                walkFileTreeSubtractPath(file, subtractStart, relativePaths);
            }
        }
    }
    
    public static void unzip(Path zip, Path target) {
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(zip))) {
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                Path filePath = Paths.get(target.toString(), entry.getName());
                if (!entry.isDirectory()) {
                    File parentDir = filePath.getParent().toFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    Files.copy(zin, filePath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    filePath.toFile().mkdirs();
                }
                zin.closeEntry();
                entry = zin.getNextEntry();
            }
        } catch (IOException ioe) {
            throw new IllegalArgumentException("用zip解压" + zip + "异常", ioe);
        }
    }
    
    public static void zip(Path path, Path zip) {
        int subtractStart = path.getNameCount();
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip))) {
            zip(path, subtractStart, zos);
        } catch (IOException ioe) {
            throw new IllegalArgumentException("压缩" + path + "异常", ioe);
        }
    }

    private static void zip(Path path, int subtractStart, final ZipOutputStream zipOut) throws IOException {
        File file = path.toFile();
        if (file.isDirectory()) {
            //迭代判断，并且加入对应文件路径
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File f : files) {
                zip(f.toPath(), subtractStart, zipOut);
            }
        } else {
            String name = path.subpath(subtractStart, path.getNameCount()).toString();
            //创建文件
            zipOut.putNextEntry(new ZipEntry(name));
            //读取文件并写出
            Files.copy(file.toPath(), zipOut);
        }
    }

	public static Path make(Path output, String path) {
		Path destPath = Path.of(output.toString(), path);
		File parentFile = destPath.getParent().toFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		return destPath;
	}
}