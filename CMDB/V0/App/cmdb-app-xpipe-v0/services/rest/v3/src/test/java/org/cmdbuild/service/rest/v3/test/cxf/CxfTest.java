/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.test.cxf;

import org.apache.cxf.jaxrs.model.URITemplate;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CxfTest {

	@Test
	public void testUriTemplateOrder1() {
		URITemplate one = new URITemplate("{a:processes|classes}/{classId}/{b:instances|cards}/{cardId}/attachments/");
		URITemplate two = new URITemplate("processes/{processId}/instances/");
		URITemplate three = new URITemplate("classes/{classId}/cards/");

		assertTrue(URITemplate.compareTemplates(one, two) > 0);
		assertTrue(URITemplate.compareTemplates(one, three) < 0);
	}

	@Test
	public void testUriTemplateOrder2() {
		URITemplate one = new URITemplate("{a:processes|classes}/{classId}/{b:instances|cards}/{cardId}/attachments/");
		URITemplate two = new URITemplate("{a:processes}/{processId}/{b:instances}/");
		URITemplate three = new URITemplate("{a:classes}/{classId}/{b:cards}/");

		assertTrue(URITemplate.compareTemplates(one, two) < 0);
		assertTrue(URITemplate.compareTemplates(one, three) < 0);
	}

}
