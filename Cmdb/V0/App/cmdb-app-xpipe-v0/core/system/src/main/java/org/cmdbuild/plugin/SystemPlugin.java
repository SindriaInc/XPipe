/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin;

import com.google.common.base.Joiner;
import static java.lang.String.format;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.fault.FaultEvent;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.maven.MavenUtils;
import static org.cmdbuild.utils.maven.MavenUtils.mavenNameVersionToFilename;

public interface SystemPlugin {

    String getName();

    String getDescription();

    String getVersion();

    String getRequiredCoreVersion();

    String getChecksum();

    List<String> getRequiredLibs();

    List<FaultEvent> getHealthCheck();

    default List<String> getRequiredLibFiles() {
        return list(getRequiredLibs()).map(MavenUtils::mavenGavToFilename);
    }

    default String getNameVersion() {
        return format("%s-%s", getName(), getVersion());
    }

    default String getFilename() {
        return mavenNameVersionToFilename(getName(), getVersion());
    }

    default boolean isOk() {
        return getHealthCheck().isEmpty();
    }

    default String getHealthCheckMessage() {
        return Joiner.on("; ").join(getHealthCheck().stream().map((event) -> event.getLevel().name() + ": " + event.getMessage()).collect(toList()));//TODO duplicate code !!
    }
}
