package org.sindria.xpipe.lib.nanoREST.config.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigService {

    public ConfigService() {

    }

    public String parseValue(String value) {

        try {
            Pattern pattern = Pattern.compile("\\$*\\{([A-Z_]+):(.*)}");
            Matcher matcher = pattern.matcher(value);
            String result = null;

            if (matcher.find()) {
                String env = matcher.group(1);
                result = System.getenv(env);

                if (result == null) {
                    result = matcher.group(2);
                } else if (result == "") {
                    result = matcher.group(2);
                }

            }

            return result;

        } catch (Exception e) {
            System.out.println("Parser exception: " + e);
            System.out.println("Detail exception: " + e.getCause());
            return null;
        }

    }

    public int parseValue(String value, boolean isInteger) {

        try {
            Pattern pattern = Pattern.compile("\\$*\\{([A-Z_]+):(.*)}");
            Matcher matcher = pattern.matcher(value);
            String resultTmp = null;
            int result = -1;

            if (matcher.find()) {
                String env = matcher.group(1);
                resultTmp = System.getenv(env);
                result = Integer.parseInt(resultTmp);

                if (resultTmp == null) {
                    resultTmp = matcher.group(2);
                    result = Integer.parseInt(resultTmp);
                } else if (resultTmp == "") {
                    resultTmp = matcher.group(2);
                    result = Integer.parseInt(resultTmp);
                }

            }

            return result;

        } catch (Exception e) {
            System.out.println("Parser exception: " + e);
            System.out.println("Detail exception: " + e.getCause());
            return -1;
        }

    }
}
