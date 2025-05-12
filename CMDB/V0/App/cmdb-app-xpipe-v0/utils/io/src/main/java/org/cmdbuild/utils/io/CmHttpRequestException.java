/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmException;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmHttpRequestException extends CmException {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String content;

    public CmHttpRequestException(Throwable nativeException, String content) {
        super(nativeException);
        this.content = content;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(content);
    }

    public boolean hasJsonContent() {
        return hasContent() && CmIoUtils.isJson(content);
    }

    public Map<String, Object> getContentAsJsonSafe() {
        try {
            return hasJsonContent() ? CmJsonUtils.fromJson(content, CmJsonUtils.MAP_OF_OBJECTS) : Collections.emptyMap();
        } catch (Exception ex) {
            LOGGER.warn("error reading json response =< {} >", CmStringUtils.abbreviate(content), ex);
            return Collections.emptyMap();
        }
    }

}
