/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import com.google.common.base.Joiner;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.transform;
import java.util.List;
import javax.annotation.Nullable;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class EmailAddressUtils {

    public static InternetAddress parseEmailAddress(String emailAddress) {
        try {
            return new InternetAddress(checkNotBlank(emailAddress));
        } catch (AddressException ex) {
            throw new EmailException(ex, "error parsing email address from string =< %s >", emailAddress);
        }
    }

    public static List<InternetAddress> parseEmailAddressList(@Nullable String emailAddressList) {
        try {
            return list(InternetAddress.parse(nullToEmpty(emailAddressList), false));
        } catch (AddressException ex) {
            throw new EmailException(ex, "error parsing email address list from string =< %s >", emailAddressList);
        }
    }

    public static List<String> parseEmailAddressListAsStrings(@Nullable String emailAddressList) {
        return list(transform(parseEmailAddressList(emailAddressList), InternetAddress::toString));
    }

    public static String addressListToString(@Nullable List<String> list) {
        return Joiner.on(", ").join(CmCollectionUtils.nullToEmpty(list));//TODO check this
    }

}
