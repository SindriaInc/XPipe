package org.sindria.xpipe.core.policies.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolicyHelper {

    public static String cleanUri(String uri, String type) {

        String cleanedUri = uri;

        if (type.equals("uuid")) {
            Pattern pattern = Pattern.compile("[\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12}");
            Matcher matcher = pattern.matcher(uri);

            if (matcher.find())
            {
                //System.out.println(matcher.group(1));
                String valueMatched = matcher.group();
                cleanedUri = uri.replace(valueMatched, "{id}");
            }
        }

        if (type.equals("id")) {
            Pattern pattern = Pattern.compile("/[0-9]+");
            Matcher matcher = pattern.matcher(uri);

            if (matcher.find())
            {
                //System.out.println(matcher.group(1));
                String valueMatched = matcher.group();
                cleanedUri = uri.replace(valueMatched, "/{id}");
            }

        }

        if (type.equals("query")) {
            Pattern pattern = Pattern.compile("\\?.*");
            Matcher matcher = pattern.matcher(uri);

            if (matcher.find())
            {
                //System.out.println(matcher.group(1));
                String valueMatched = matcher.group();
                cleanedUri = uri.replace(valueMatched, "");
            }

        }

        return cleanedUri;
    }



}
