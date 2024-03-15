/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import static org.apache.commons.io.CopyUtils.copy;
import static org.apache.commons.io.FileUtils.readLines;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.minions.MinionStatus.MS_ERROR;
import static org.cmdbuild.minions.MinionStatus.MS_READY;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.getUnsafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeFunction;
import static org.cmdbuild.utils.maven.MavenUtils.getResourceByGavOrNull;
import static org.cmdbuild.utils.maven.MavenUtils.mavenGavToFilename;
import static org.cmdbuild.utils.maven.MavenUtils.mavenNameVersionToFilename;
import static org.cmdbuild.utils.xml.CmXmlUtils.applyXpath;
import static org.cmdbuild.utils.xml.CmXmlUtils.toDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

public class SystemPluginUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String buildPluginStatusInfoMessage(List<SystemPlugin> plugins) {
        return plugins.isEmpty() ? "" : (list(plugins).sorted(SystemPlugin::getName).map(p -> format("%-28s    %s   %s", p.getDescription(), serializeEnum(p.isOk() ? MS_READY : MS_ERROR), abbreviate(p.getHealthCheckMessage()))).collect(joining("\n")) + "\n\n");
    }

    public static List<SystemPlugin> scanWarFileForPlugins(File warFileOrWebappDir) {
        if (warFileOrWebappDir.isFile()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create(format("jar:%s", warFileOrWebappDir.toPath().toUri())), emptyMap())) {
                Path index = fileSystem.getPath("/WEB-INF/lib/systemlibs.list");
                Set<String> systemLibs = Files.exists(index) ? list(Files.readAllLines(index)).filter(StringUtils::isNotBlank).sorted().toSet() : emptySet();
                return Files.list(fileSystem.getPath("/WEB-INF/lib/"))
                        .filter(p -> p.getFileName().toString().endsWith(".jar") && !systemLibs.contains(p.getFileName().toString()))
                        .map(unsafeFunction(p -> scanFileForPlugin(p.getFileName().toString(), Files.readAllBytes(p)))).filter(Objects::nonNull).sorted(Ordering.natural().onResultOf(SystemPlugin::getNameVersion))
                        .collect(toImmutableList());
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            return scanFolderForPlugins(new File(warFileOrWebappDir, "/WEB-INF/lib/"));
        }
    }

    public static void addPluginFilesToWarFileInplace(File warFileOrWebappDir, List<File> plugins) {
        addPluginsToWarFileInplace(warFileOrWebappDir, list(plugins).map(f -> toDataSource(f)));
    }

    public static void addPluginsToWarFileInplace(File warFileOrWebappDir, List<DataSource> plugins) {
        Set<String> fileNames = list(plugins).map(d -> FilenameUtils.getName(d.getName())).toSet();
        list(plugins).stream().map(p -> scanFileForPlugin(p.getName(), toByteArray(p))).filter(Objects::nonNull).forEach(p -> {
            LOGGER.debug("processing plugin =< {} >", p.getNameVersion());
            p.getRequiredLibs().forEach(l -> {
                if (!fileNames.contains(mavenGavToFilename(l))) {
                    DataSource lib = getResourceByGavOrNull(l);
                    LOGGER.debug("loading maven lib =< {} >", l);
                    if (lib != null) {
                        LOGGER.debug("add maven lib =< {} >", lib.getName());
                        fileNames.add(lib.getName());
                        plugins.add(lib);
                    } else {
                        LOGGER.warn("missing required lib =< {} > for plugin =< {} > (unable to load from maven)", l, p.getNameVersion());
                    }

                }
            });
        });
        if (warFileOrWebappDir.isFile()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create(format("jar:%s", warFileOrWebappDir.toPath().toUri())), emptyMap())) {
                plugins.forEach(unsafeConsumer(f -> {
                    Path path = fileSystem.getPath("/WEB-INF/lib/", FilenameUtils.getName(f.getName()));
                    try (OutputStream outputStream = Files.newOutputStream(path); InputStream inputStream = f.getInputStream()) {
                        copy(inputStream, outputStream);
                    }
                }));
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            plugins.forEach(unsafeConsumer(f -> {
                Path path = Path.of(warFileOrWebappDir.getAbsolutePath(), "/WEB-INF/lib/", FilenameUtils.getName(f.getName()));
                try (OutputStream outputStream = Files.newOutputStream(path); InputStream inputStream = f.getInputStream()) {
                    copy(inputStream, outputStream);
                }
            }));
        }
    }

    public static void removePluginsFromWarFileInplace(File warFileOrWebappDir, List<String> plugins) {
        Map<String, SystemPlugin> map = map(scanWarFileForPlugins(warFileOrWebappDir), SystemPlugin::getName);
        List<String> files = list(plugins).distinct().map(p -> checkNotNull(map.get(p), "plugin not found for name =< %s >", p)).flatMap(p -> list(p.getFilename()).with(p.getRequiredLibFiles()));
        removePluginFilesFromWarFileInplace(warFileOrWebappDir, files);
    }

    public static void removePluginFilesFromWarFileInplace(File warFileOrWebappDir, List<String> pluginFiles) {
        if (warFileOrWebappDir.isFile()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create(format("jar:%s", warFileOrWebappDir.toPath().toUri())), emptyMap())) {
                pluginFiles.forEach(unsafeConsumer(f -> {
                    Path path = fileSystem.getPath("/WEB-INF/lib/", f);
                    Files.delete(path);
                }));
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            pluginFiles.forEach(unsafeConsumer(f -> {
                Path path = Path.of(warFileOrWebappDir.getAbsolutePath(), "/WEB-INF/lib/", f);
                Files.delete(path);
            }));
        }
    }

    public static List<SystemPlugin> scanFolderForPlugins(File folder) {
        LOGGER.debug("scan folder = {}", folder.getAbsolutePath());
        File index = new File(folder, "systemlibs.list");
        Set<String> systemLibs = index.exists() ? getUnsafe(() -> list(readLines(index)).filter(StringUtils::isNotBlank).sorted().map(l -> {
            LOGGER.trace("load, from index, sys lib =< {} >", l);
            return l;
        }).toSet()) : emptySet();
        if (systemLibs.isEmpty()) {
            LOGGER.warn("system lib index not found for system plugin folder = {}: full folder scan required", folder.getAbsolutePath());
        } else {
            LOGGER.debug("skip {} libs from system lib index", systemLibs.size());
        }
        return scanFilesForPlugins(list(folder.listFiles((d, n) -> n.matches("(?i).+[.]jar") && !systemLibs.contains(n))));
    }

    public static List<SystemPlugin> scanFilesForPlugins(Collection<File> files) {
        LOGGER.debug("scan {} files for plugins", files.size());
        return list(files).map(SystemPluginUtils::scanFileForPlugin).filter(Objects::nonNull);
    }

    @Nullable
    public static SystemPlugin scanFileForPlugin(File file) {
        return scanFileForPlugin(file.getAbsolutePath(), toByteArray(file));
    }

    @Nullable
    private static SystemPlugin scanFileForPlugin(String path, byte[] data) {
        LOGGER.debug("scan file =< {} >", path);
        String filename = FilenameUtils.getName(path);
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            String pomXml = null, pomProperties = null;
            while ((entry = in.getNextEntry()) != null) {
                if (entry.getName().matches("META-INF/maven/.*/pom.xml")) {
                    pomXml = readToString(in);
                } else if (entry.getName().matches("META-INF/maven/.*/pom.properties")) {
                    pomProperties = readToString(in);
                }
            }
            if (isNotBlank(pomProperties) && isNotBlank(pomXml)) {
                Document pom = toDocument(pomXml);
                if (toBoolean(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "boolean(/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin'])"))) {
                    Map<String, String> properties = loadProperties(pomProperties);
                    String name = checkNotBlank(properties.get("artifactId")),
                            version = checkNotBlank(properties.get("version")),
                            requiredCoreVersion = applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin.requiredCoreVersion']"),
                            description = nullToEmpty(firstNotBlankOrNull(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:description"), applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:name"))),
                            expectedFilename = mavenNameVersionToFilename(name, version),
                            checksum = hash(data);
                    checkArgument(equal(filename, expectedFilename), "invalid lib filename =< %s > expected filename =< %s >", filename, expectedFilename);
                    LOGGER.info("found plugin =< {} >", expectedFilename);
                    List<String> libs = Splitter.onPattern("[\\s,;]+").omitEmptyStrings().trimResults().splitToList(nullToEmpty(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin.libs']")));
                    LOGGER.debug("plugin libs config = {}", libs);
//                    libs = list(libs).map(artifactId -> format("%s-%s.jar", artifactId, checkNotBlank(applyXpath(pom, 
                    libs = list(libs).map(artifactId -> format("%s:%s:%s",
                            checkNotBlank(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), format("/pom:project/pom:dependencies/pom:dependency[pom:artifactId='%s']/pom:groupId", artifactId))),
                            artifactId,
                            checkNotBlank(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), format("/pom:project/pom:dependencies/pom:dependency[pom:artifactId='%s']/pom:version", artifactId)))));
                    LOGGER.info("plugin libs = {}", libs);
                    return SystemPluginImpl.builder()
                            .withName(name)
                            .withDescription(description)
                            .withVersion(version)
                            .withRequiredCoreVersion(requiredCoreVersion)
                            .withRequiredLibs(libs)
                            .withChecksum(checksum)
                            .build();
                }
            }
            return null;
        } catch (Exception ex) {
            throw runtime(ex, "error processing plugin file =< %s >", path);
        }
    }
}
