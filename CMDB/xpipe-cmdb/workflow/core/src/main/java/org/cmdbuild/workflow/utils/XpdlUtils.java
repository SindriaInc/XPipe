/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static java.lang.String.format;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.codec.Charsets;

public class XpdlUtils {

	public static DataSource xpdlToDatasource(String xpdl, String filename) {
		return xpdlToDatasource(xpdl.getBytes(Charsets.UTF_8), filename);
	}

	public static DataSource xpdlToDatasource(byte[] xpdl, String filename) {
		ByteArrayDataSource dataSource = new ByteArrayDataSource(xpdl, "application/x-xpdl");
		dataSource.setName(format("%s.xpdl", filename));
		return dataSource;
	}
}
