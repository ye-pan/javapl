package org.yepan.jd.exception;

public class IllegalArgsException extends IllegalArgumentException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7418313915411963161L;
	private String args;
	private String value;
	
	public IllegalArgsException(String args, String value, String message) {
		super(message);
		this.args = args;
		this.value = value;
	}
	
	@Override
	public String getMessage() {
		return String.format(super.getMessage(), args, value);
	}
}
