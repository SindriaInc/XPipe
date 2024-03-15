package org.sindria.xpipe.core.policies.validators;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class PolicyVerifyValidator {

    public Boolean hasUuid(String uri) {

        Pattern pattern = Pattern.compile("[\\w]{8}-[\\w]{4}-[\\w]{4}-[\\w]{4}-[\\w]{12}");
        Matcher matcher = pattern.matcher(uri);

        if (matcher.find())
        {
            //System.out.println(matcher.group(1));
            return true;
        }

        return false;
    }


    public Boolean hasId(String uri) {

        Pattern pattern = Pattern.compile("/[0-9]+");
        Matcher matcher = pattern.matcher(uri);

        if (matcher.find())
        {
            //System.out.println(matcher.group(1));
            return true;
        }

        return false;
    }


    public Boolean hasQueryString(String uri) {

        Pattern pattern = Pattern.compile("\\?.*");
        Matcher matcher = pattern.matcher(uri);

        if (matcher.find())
        {
            //System.out.println(matcher.group(1));
            return true;
        }

        return false;
    }
}
