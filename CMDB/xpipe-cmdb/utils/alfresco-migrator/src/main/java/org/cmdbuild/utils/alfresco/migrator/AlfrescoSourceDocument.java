/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator;

import org.cmdbuild.utils.io.BigByteArray;

public interface AlfrescoSourceDocument extends AlfrescoSourceDocumentInfo {

    BigByteArray getData();

    AlfrescoSourceDocument withFolder(String otherFolder);

}
