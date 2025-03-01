/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dms;

import jakarta.activation.DataHandler;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

/**
 *
 * @author afelice
 */
public class DocumentHandle {

    private final String documentId;
    private final DataHandler dataHandler;
    private final String filename;
    private final Map<String, Object> metadata;

    // Optional parameters
    private final String description;
    private final String author;

    public DocumentHandle(String documentId, Map<String, Object> metadata, DataHandler dataHandler) {
        this.documentId = documentId;
        this.dataHandler = dataHandler;
        this.filename = checkAttrib(metadata, "name");

        // May be unchanged, so not present in given metadata
        if (metadata.containsKey("description")) {
            this.description = checkAttrib(metadata, "description");
        } else {
            this.description = null;
        }

        if (metadata.containsKey("author")) {
            this.author = checkAttrib(metadata, "author");
        } else {
            this.author = null;
        }
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "DocumentHandle{documentId=< %s >,filename=< %s >,description=< %s >}".formatted(documentId, filename, description);
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getFilename() {
        return filename;
    }

    public String getAuthor() {
        return author;
    }

    public boolean hasAuthor() {
        return metadata.containsKey("author");
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return metadata.containsKey("description");
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    private String checkAttrib(Map<String, Object> attributes, String attribName) {
        if (!attributes.containsKey(attribName)) {
            throw runtime("error deserializing document metadata, couldn't find =< %s > attribute".formatted(attribName));
        }
        if (!(attributes.get(attribName) instanceof String)) {
            Object attribValue = attributes.get(attribName);
            throw runtime("error deserializing document metadata, value for =< %s > attribute expected a string, found =< %s >".formatted(attribName, attribValue));
        }

        return (String) attributes.get(attribName);
    }

}
