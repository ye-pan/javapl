package org.yepan.jd.jar;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.jd.core.v1.api.printer.Printer;
import org.yepan.jd.JavaFiles;
import org.yepan.jd.exception.ClassFilePrinterException;

public class OutFilePrinter implements Printer {
	private static final char TAB = '\t';
	private static final char NEWLINE = '\n';

	protected boolean unicodeEscape = true;
	protected boolean realignmentLineNumber = false;
	protected int majorVersion = 0;
	protected int minorVersion = 0;
	protected int indentationCount;
	private final Path output;
	private OutputStream out;

	public OutFilePrinter(Path path) {
		this.output = Objects.requireNonNull(path);
	}

	public void setUnicodeEscape(boolean unicodeEscape) {
		this.unicodeEscape = unicodeEscape;
	}

	public void setRealignmentLineNumber(boolean realignmentLineNumber) {
		this.realignmentLineNumber = realignmentLineNumber;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	protected void escape(String s) {
		if (unicodeEscape && (s != null)) {
			int length = s.length();

			for (int i = 0; i < length; i++) {
				char c = s.charAt(i);

				if (c == '\t') {
					out(c);
				} else if (c < 32) {
					// Write octal format
					out("\\0");
					out((char) ('0' + (c >> 3)));
					out((char) ('0' + (c & 0x7)));
				} else if (c > 127) {
					// Write octal format
					out("\\u");

					int z = (c >> 12);
					out((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
					z = ((c >> 8) & 0xF);
					out((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
					z = ((c >> 4) & 0xF);
					out((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
					z = (c & 0xF);
					out((char) ((z <= 9) ? ('0' + z) : (('A' - 10) + z)));
				} else {
					out(c);
				}
			}
		} else {
			out(s);
		}
	}

	private void out(String str) {
		try {
			if (str == null) {
				str = "null";
			}
			this.out.write(str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassFilePrinterException(output, e);
		}
	}

	private void out(char c) {
		try {
			this.out.write(c);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassFilePrinterException(output, e);
		}
	}

	@Override
	public void start(int maxLineNumber, int majorVersion, int minorVersion) {
		if (Files.exists(output)) {
			throw new ClassFilePrinterException(output, "Java文件已经存在，不再处理");
		}
		try {
			this.out = Files.newOutputStream(output, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ClassFilePrinterException(output, e);
		}
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.indentationCount = 0;
	}

	@Override
	public void end() {
		try {
			if (this.out != null) {
				this.out.flush();
			}
		} catch (IOException e) {
			throw new ClassFilePrinterException(output, e);
		}
		JavaFiles.close(this.out);
	}

	@Override
	public void printText(String text) {
		escape(text);
	}

	@Override
	public void printNumericConstant(String constant) {
		escape(constant);
	}

	@Override
	public void printStringConstant(String constant, String ownerInternalName) {
		escape(constant);
	}

	@Override
	public void printKeyword(String keyword) {
		out(keyword);
	}

	@Override
	public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
		escape(name);
	}

	@Override
	public void printReference(int type, String internalTypeName, String name, String descriptor,
			String ownerInternalName) {
		escape(name);
	}

	@Override
	public void indent() {
		indentationCount++;
	}

	@Override
	public void unindent() {
		if (indentationCount > 0) {
			indentationCount--;
		}
	}

	@Override
	public void startLine(int lineNumber) {
		for (int i = 0; i < indentationCount; i++) {
			out(TAB);
		}
	}

	@Override
	public void endLine() {
		out(NEWLINE);
	}

	@Override
	public void extraLine(int count) {
		if (realignmentLineNumber) {
			while (count-- > 0) {
				out(NEWLINE);
			}
		}
	}

	@Override
	public void startMarker(int type) {

	}

	@Override
	public void endMarker(int type) {

	}
}
