/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.email.template;

import java.util.List;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import static org.cmdbuild.email.utils.EmailMtaUtils.renameDuplicates;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.hamcrest.Matcher;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.endsWithIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class EmailTemplateProcessorServiceImplTest {
    
    /**
     * Test of createEmailFromTemplate method, of class EmailTemplateProcessorServiceImpl.
     */
    @Test
    public void testEmailAttachments_RenameDuplicates() {
        System.out.println("emailAttachments_RenameDuplicates");
        
        //arrange:
        List<EmailAttachment> emailAttachments = list(mock_buildEmailAttachment_PDF("abc.pdf"), 
                            mock_buildEmailAttachment_PDF("abc.pdf")
                            );

        //act:
        List<EmailAttachment> result = renameDuplicates(emailAttachments);        
        
        //assert:
        assertThat(result, hasSize(2));
        assertFalse(result.get(0).getFileName().equals(result.get(1).getFileName()));
        final Matcher<EmailAttachment> matcherExtension = Matchers.hasProperty("fileName", endsWithIgnoringCase(".pdf"));
        assertThat(result, everyItem(matcherExtension));        
    }

    private static EmailAttachmentImpl mock_buildEmailAttachment_PDF(final String fileName) {
        return EmailAttachmentImpl.builder().withFileName(fileName)
                .withData(new byte[0])
                .withContentType("application/pdf")
                .build();
    }
    
}
