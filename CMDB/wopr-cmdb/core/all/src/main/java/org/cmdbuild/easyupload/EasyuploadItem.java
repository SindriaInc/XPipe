/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

public interface EasyuploadItem extends EasyuploadItemInfo {

	static final String EASYUPLOAD_CONTENT = "Content";

	byte[] getContent();

}
