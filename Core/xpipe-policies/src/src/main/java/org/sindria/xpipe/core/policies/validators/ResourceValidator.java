package org.sindria.xpipe.core.policies.validators;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class ResourceValidator {

    public static final String SRN_REGEX = "^srn:[a-z0-9-]+:[a-z0-9-]+:[a-z0-9-]+:\\d+:[a-z0-9-]+:(any|void|[a-f0-9-]{36})$";
    private static final Pattern SRN_PATTERN = Pattern.compile(SRN_REGEX);

    public boolean isValid(String srn) {

        return SRN_PATTERN.matcher(srn).matches();
    }
}
