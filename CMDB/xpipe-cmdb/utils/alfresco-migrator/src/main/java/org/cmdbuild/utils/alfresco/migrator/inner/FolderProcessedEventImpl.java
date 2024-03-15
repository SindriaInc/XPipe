/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.alfresco.migrator.inner;

import static com.google.common.base.Preconditions.checkNotNull;

public class FolderProcessedEventImpl implements AlfrescoSource.FolderProcessedEvent {

    private final String path;

    public FolderProcessedEventImpl(String path) {
        this.path = checkNotNull(path);//root path is ""
    }

    @Override
    public String getPath() {
        return path;
    }

}
