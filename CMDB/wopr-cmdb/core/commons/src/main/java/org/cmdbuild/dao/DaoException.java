/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao;

import static java.lang.String.format;

public class DaoException extends RuntimeException {

	public DaoException(Throwable cause) {
		super(cause);
	}

	public DaoException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

	public DaoException(String message, Object... args) {
		super(format(message, args));
	}

}
