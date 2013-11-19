/**
 * 
 */
package com.tqlab.plugin.mybatis;

/**
 * @author John Lee
 * 
 */
public class MybatisPluginException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4350101455492534722L;

	/**
	 * 
	 */
	public MybatisPluginException() {
		super();
	}

	/**
	 * @param message
	 */
	public MybatisPluginException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MybatisPluginException(final Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MybatisPluginException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
