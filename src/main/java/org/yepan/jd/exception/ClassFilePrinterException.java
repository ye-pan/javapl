package org.yepan.jd.exception;

import java.nio.file.Path;

public class ClassFilePrinterException extends RuntimeException {
	
	private static final long serialVersionUID = -661137522660340708L;
	private Path path;
	
	public ClassFilePrinterException(Path path) {
		this.path = path;
	}
	
	public ClassFilePrinterException(Path path, Throwable e) {
		super(e);
		this.path = path;
	}
	
	@Override
	public String getMessage() {
		return super.getMessage() + ", 输出文件路径：" + path;
	}
}
