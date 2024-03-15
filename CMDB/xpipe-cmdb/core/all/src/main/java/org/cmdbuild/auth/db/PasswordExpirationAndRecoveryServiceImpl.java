/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.db;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordEncodingService;
import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.auth.login.PasswordRecoveryService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import static org.cmdbuild.auth.user.UserRepository.BLANK_PASSWORD;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class PasswordExpirationAndRecoveryServiceImpl implements PasswordRecoveryService {

    private final static String PASSWORD_LAST_MOD_QUERY = "WITH q AS "
            + "(SELECT \"Username\",(WITH _history AS (SELECT h.\"BeginDate\",ROW_NUMBER() OVER (ORDER BY h.\"BeginDate\" DESC) AS _row,h.\"Password\" FROM \"User\" h WHERE h.\"CurrentId\" = u.\"Id\")"
            + " SELECT _history.\"BeginDate\" FROM _history WHERE _row = COALESCE((SELECT _row-1 FROM _history WHERE _history.\"Password\" IS DISTINCT FROM u.\"Password\" LIMIT 1),(SELECT MAX(_row) FROM _history))) \"BeginDate\""
            + " FROM \"User\" u WHERE _cm3_utils_is_not_blank(u.\"Password\") AND \"Status\" = 'A') SELECT \"Username\" FROM q";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PasswordManagementConfiguration pswConfig;
    private final UserRepository userRepository;
    private final DaoService dao;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final PasswordEncodingService encodingService;

    public PasswordExpirationAndRecoveryServiceImpl(PasswordManagementConfiguration pswConfig, UserRepository userRepository, DaoService dao, EmailService emailService, EmailTemplateService emailTemplateService, PasswordEncodingService encodingService) {
        this.pswConfig = checkNotNull(pswConfig);
        this.userRepository = checkNotNull(userRepository);
        this.dao = checkNotNull(dao);
        this.emailService = checkNotNull(emailService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.encodingService = checkNotNull(encodingService);
    }

    @ScheduledJob(value = "0 0 6 * * ?", clusterMode = RUN_ON_SINGLE_NODE) //run every day
    public void checkPasswordExpiration() {
        if (pswConfig.isPasswordManagementEnabled() && isNotNullAndGtZero(pswConfig.getMaxPasswordAgeDays())) {
            if (isNotNullAndGtZero(pswConfig.getForewarningDays())) {
                dao.getJdbcTemplate().queryForList(format(PASSWORD_LAST_MOD_QUERY + " WHERE \"BeginDate\" BETWEEN %s AND %s",
                        systemToSqlExpr(now().minusDays(pswConfig.getMaxPasswordAgeDays())), systemToSqlExpr(now().minusDays(pswConfig.getMaxPasswordAgeDays()).plusDays(pswConfig.getForewarningDays()))), String.class).forEach((expiring) -> {
                    UserData user = userRepository.getUserData(LoginUserIdentity.build(expiring));
                    if (user.isNotService() || pswConfig.isServiceUsersPasswordExpirationEnabled()) {
                        logger.info("expiring password detected for username = {}", expiring);
                        if (user.isActive() && user.hasEmail()) {
                            emailService.create(emailService.applyTemplate(
                                    EmailImpl.builder().withStatus(ES_OUTGOING).withTo(user.getEmail()).build(),
                                    firstNotNull(emailTemplateService.getByNameOrIdOrNull("SystemPswExpiringNotification"), checkNotNull(emailTemplateService.getByNameOrIdOrNull("SystemPswExpiringNotificationDefault"))),
                                    map("username", expiring)
                            ));
                        }
                    }
                });
            }
            dao.getJdbcTemplate().queryForList(format(PASSWORD_LAST_MOD_QUERY + " WHERE \"BeginDate\" < %s", systemToSqlExpr(now().minusDays(pswConfig.getMaxPasswordAgeDays()))), String.class).forEach((expired) -> {
                UserData user = userRepository.getUserData(LoginUserIdentity.build(expired));
                if (user.isNotService() || pswConfig.isServiceUsersPasswordExpirationEnabled()) {
                    logger.info("expired password detected for username =< {} >", expired);
                    user = userRepository.update(UserDataImpl.copyOf(user).withPassword(BLANK_PASSWORD).withRecoveryToken(user.getPassword()).build());
                    if (user.isActive()) {
                        if (user.hasEmail()) {
                            emailService.create(emailService.applyTemplate(
                                    EmailImpl.builder().withStatus(ES_OUTGOING).withTo(user.getEmail()).build(),
                                    firstNotNull(emailTemplateService.getByNameOrIdOrNull("SystemPswExpiredNotification"), checkNotNull(emailTemplateService.getByNameOrIdOrNull("SystemPswExpiredNotificationDefault"))),
                                    map("username", expired)
                            ));
                        } else {
                            logger.info("expired password for user =< {} >, unable to send notification email cause user is missing an email address", expired);
                        }
                    } else {
                        logger.info("expired password for inactive user =< {} >, will not send notification email (user is not active)", expired);
                    }
                }
            });
        }
    }

    @Override
    public void requirePasswordRecovery(String username) {
        UserData userData = userRepository.getActiveUserData(LoginUserIdentity.build(username));
        checkArgument(userData.hasEmail(), "cannot send password recovery to user =< %s >: user does not have any email address", username);
        String recoveryToken = randomId();
        Email email = emailService.applyTemplate(
                EmailImpl.builder().withStatus(ES_OUTGOING).withTo(userData.getEmail()).build(),
                firstNotNull(emailTemplateService.getByNameOrIdOrNull("SystemPswRecoveryNotification"), checkNotNull(emailTemplateService.getByNameOrIdOrNull("SystemPswRecoveryNotificationDefault"))),
                map("username", username, "recoveryToken", recoveryToken)
        );
        logger.info("required password recovery for user =< {} >, ser recovery token and send email to user", username);
        userRepository.update(UserDataImpl.copyOf(userData).withRecoveryToken(encodingService.encryptPassword(recoveryToken)).build());
        emailService.create(email);
    }

}
