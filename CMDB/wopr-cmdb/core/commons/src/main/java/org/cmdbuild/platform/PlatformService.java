/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.platform;

public interface PlatformService {

	void stopContainer();

	void restartContainer();

	void upgradeLocalWebapp(byte[] newWarData);
}
