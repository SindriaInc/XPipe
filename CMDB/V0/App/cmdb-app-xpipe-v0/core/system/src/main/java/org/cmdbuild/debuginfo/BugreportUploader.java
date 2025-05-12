/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class BugreportUploader {

	public static void uploadBugreport(File reportfile, String filename) {
		checkNotNull(reportfile);
		checkArgument(reportfile.exists() && reportfile.length() > 0);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			HttpEntity payload = MultipartEntityBuilder.create()
					.addBinaryBody("file", reportfile, ContentType.APPLICATION_OCTET_STREAM, filename)
					.build();
			HttpPost request = new HttpPost("http://10.0.0.106:8013/bugreport");
			request.setEntity(payload);
			String response = IOUtils.toString(client.execute(request).getEntity().getContent());
			//TODO handle response
		} catch (IOException ex) {
			throw runtime(ex);
		} finally {
			try {
				client.close();
			} catch (IOException ex) {
			}
		}
	}

}
