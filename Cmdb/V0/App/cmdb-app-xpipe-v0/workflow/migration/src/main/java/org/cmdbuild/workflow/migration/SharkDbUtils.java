/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.migration;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Ordering;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.workflow.model.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SharkDbUtils {

    public static SharkHelper sharkHelper(Map<String, String> config) {
        return new SharkHelperImpl(config::get);
    }

    public static SharkHelper sharkHelper(Function<String, String> configSupplier) {
        return new SharkHelperImpl(configSupplier);
    }

    public interface SharkHelper {

        JdbcTemplate getSharkJdbcTemplate();

        Triple<String, String, String> getSharkActivityDefinitionIdWithParent(String sharkActivityId);

        List<XpdlData> getAllXpdlData();

        Map<String, Object> getFlowDataForProcess(String sharkProcessCode);

        @Nullable
        default XpdlData getXpdlDataByPackageAndVersionOrNull(String packageId, String version) {
            return getAllXpdlData().stream().filter(d -> equal(d.getPackageId(), packageId) && equal(d.getVersion(), version)).collect(toOptional()).orElse(null);
        }

        default XpdlData getXpdlDataByPackageAndVersion(String packageId, String version) {
            return checkNotNull(getXpdlDataByPackageAndVersionOrNull(packageId, version), "xpdl data not found for packageid =< %s > and version =< %s >", packageId, version);
        }

        default XpdlData getLastXpdlDataByPackage(String packageId) {
            return checkNotNull(getAllXpdlData().stream().filter(d -> equal(d.getPackageId(), packageId)).sorted(Ordering.natural().onResultOf(d -> toInt(((XpdlData) d).getVersion())).reversed()).findFirst().orElse(null), "xpdl data not found for packageid =< %s >  ", packageId);
        }
    }

    public interface XpdlData {

        String getPackageId();

        String getVersion();

        byte[] getXpdlData();

        default String getXpdlDataAsString() {
            return new String(getXpdlData(), StandardCharsets.UTF_8);
        }
    }

    private static class SharkHelperImpl implements SharkHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final String url, username, password;
        private final JdbcTemplate sharkJdbcTemplate;

        private final Supplier<List<XpdlData>> xpdlDataSupplier = Suppliers.memoize(this::doGetAllXpdlData);

        public SharkHelperImpl(Function<String, String> configSupplier) {
            url = checkNotBlank(configSupplier.apply("org.cmdbuild.workflow.shark.db.url"), "shark db url is required for process migration (set config param 'org.cmdbuild.workflow.shark.db.url')");
            username = firstNotBlank(configSupplier.apply("org.cmdbuild.workflow.shark.db.username"), "shark");
            password = firstNotBlank(configSupplier.apply("org.cmdbuild.workflow.shark.db.password"), "shark");
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            sharkJdbcTemplate = new JdbcTemplate(dataSource);
            logger.info("shark database is = {}", url);
        }

        @Override
        public JdbcTemplate getSharkJdbcTemplate() {
            return sharkJdbcTemplate;
        }

        @Override
        public Triple<String, String, String> getSharkActivityDefinitionIdWithParent(String sharkActivityId) {
            try {
                return sharkJdbcTemplate.queryForObject("SELECT _activity.activitydefinitionid _activity_definition_id, _activity_parent.activitydefinitionid _parent_activity_definition_id, _activity_parent.id _activity_id FROM shark.shkactivities _activity LEFT JOIN shark.shkactivities _activity_parent ON _activity.blockactivityid = _activity_parent.id WHERE _activity.id = ?", (r, i) -> {
                    return Triple.of(checkNotBlank(r.getString("_activity_definition_id")), r.getString("_parent_activity_definition_id"), r.getString("_activity_id"));
                }, sharkActivityId);
            } catch (Exception ex) {
                throw new WorkflowException(ex, "unable to find shark activity definition for id =< %s >", sharkActivityId);
            }
        }

        @Override
        public List<XpdlData> getAllXpdlData() {
            return xpdlDataSupplier.get();
        }

        @Override
        public Map<String, Object> getFlowDataForProcess(String sharkProcessCode) {
            return sharkJdbcTemplate.query("SELECT"
                    + " objectid,variabledefinitionid,variabletype,variablevalue,variablevaluexml,variablevaluevchar,variablevaluedbl,variablevaluelong,variablevaluedate,variablevaluebool"
                    + " FROM shark.shkprocessdata WHERE process = (SELECT objectid FROM shark.shkprocesses WHERE id = ?)", (r, i) -> {
                        long tupleId = r.getLong("objectid");
                        try {
                            String key = checkNotBlank(r.getString("variabledefinitionid"));
                            Object value = convertSharkDbValue(r);
                            return Pair.of(key, value);
                        } catch (Exception ex) {
                            throw new WorkflowException(ex, "error processing shark `shkprocessdata` tuple with objectid = %s", tupleId);
                        }
                    }, checkNotBlank(sharkProcessCode)).stream().collect(toMap(Pair::getKey, Pair::getValue));

        }

        @Nullable
        private Object convertSharkDbValue(ResultSet r) throws SQLException {
            int type = r.getInt("variabletype");
            return switch (type) {
                case 0 ->
                    r.getBoolean("variablevaluebool");
                case 1 ->
                    r.getLong("variablevaluelong");
                case 2 ->
                    r.getDouble("variablevaluedbl");
                case 3 ->
                    nullToEmpty(r.getString("variablevaluevchar"));
                case 4 ->
                    r.getTimestamp("variablevaluedate");
                case 5 ->
                    convertBinarySharkDbValue(r.getBytes("variablevalue"));
                default ->
                    throw new WorkflowException("unsupported shark variable type = %s", type);
            };
        }

        @Nullable
        private Object convertBinarySharkDbValue(@Nullable byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                return null;
            } else {
                try {
                    logger.trace("deserializing shark binary value from bytes = {}", Base64.encodeBase64String(bytes));
                    Object value = deserializeObject(bytes);
                    logger.trace("deserialized shark value object = {} ({})", value, getClassOfNullable(value).getName());
                    return value;
                } catch (Exception ex) {
                    throw new WorkflowException(ex, "error deserializing shark binary value = %s", Base64.encodeBase64String(bytes));
                }
            }
        }

        private List<XpdlData> doGetAllXpdlData() {
            return sharkJdbcTemplate.query("SELECT xpdlid _packageid, xpdlversion _version, xpdlcontent _content FROM shark.shkxpdls x JOIN shark.shkxpdldata d ON x.objectid = d.xpdl ORDER BY xpdlid, xpdlversion", (rs, rowNum) -> {
                String packageId = checkNotBlank(rs.getString("_packageid")),
                        version = checkNotBlank(rs.getString("_version"));
                byte[] data = rs.getBytes("_content");
                checkArgument(data.length > 0);
                return new XpdlData() {
                    @Override
                    public String getPackageId() {
                        return packageId;
                    }

                    @Override
                    public String getVersion() {
                        return version;
                    }

                    @Override
                    public byte[] getXpdlData() {
                        return data;
                    }
                };
            });
        }

    }

}
