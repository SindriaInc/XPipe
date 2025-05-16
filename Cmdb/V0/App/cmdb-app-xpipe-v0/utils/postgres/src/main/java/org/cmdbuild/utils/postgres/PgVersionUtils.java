/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import static com.google.common.base.Preconditions.checkArgument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class PgVersionUtils {

    public static int getPostgresServerVersionNum(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("show server_version_num"); ResultSet resultSet = preparedStatement.executeQuery()) {
            checkArgument(resultSet.next(), "unable to get postgres server version");
            int pgServerVersion = resultSet.getInt(1);
            checkArgument(pgServerVersion > 0, "unable to get postgres server version");
            return pgServerVersion;
        } catch (SQLException ex) {
            throw runtime(ex);
        }
    }

    public static String getPostgresServerVersionFromNumber(int versionNumber) {
        return Integer.toString(versionNumber)
                .replaceFirst("^([0-9]{1,2})([0-9]{2})([0-9]{2})$", "$1.$2.$3")
                .replaceAll("[.]0([0-9])", ".$1")
                .replaceAll("^([0-9]{2}.)0.", "$1");
    }
}
