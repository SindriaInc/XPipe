/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Handle for documents to merge in <i>dms</i> and related metadata.
 * @author afelice
 */
public class DmsDocumentDataHandle {
    private Map<FileAttachmentHandle, DocumentHandle> docToInsertColl = map();
    private final Map<FileAttachmentHandle, String> docToRemoveColl = map();
    
    public void addDocumentToInsert(FileAttachmentHandle fileAttributeHandle, DocumentHandle document) {
        docToInsertColl.put(fileAttributeHandle, document);
    }    

    public void addDocumentToRemove(FileAttachmentHandle fileAttributeHandle, String toRemoveFilename) {
        docToRemoveColl.put(fileAttributeHandle, toRemoveFilename);
    }    
    
    public Map<FileAttachmentHandle, DocumentHandle> getToInsertColl() {
        return map(docToInsertColl);
    }
    
    public Map<FileAttachmentHandle, String> getToRemoveColl() {
        return map(docToRemoveColl);
    }
    
    public boolean isToInsertEmpty() {
        return docToInsertColl.isEmpty();
    }

    public boolean isToRemoveEmpty() {
        return docToRemoveColl.isEmpty();
    }    
    
    /**
     * Handle newly added {@link Card}: in <code>diff</code> there was still an <code>UUID</code>,
     * after card insert the persisted <code>id</code> has to be used.
     * 
     * @param uuidsMap 
     */
    public void apply(Map<String, Long> uuidsMap) {
        if (!uuidsMap.isEmpty()) {
            Map<FileAttachmentHandle, DocumentHandle> result = map();
            
            docToInsertColl.entrySet().forEach(entry -> {                
                final String foundCardId = entry.getKey().getCardId();
                FileAttachmentHandle toUseKey = entry.getKey();
                if (uuidsMap.containsKey(foundCardId)) {
                    // Substitute card UUID with persisted id
                    toUseKey = entry.getKey().copyWith(String.valueOf(uuidsMap.get(foundCardId)));
                }
                    
                result.put(toUseKey, entry.getValue());                
            });
            
            docToInsertColl = result;
        }
    }

}
