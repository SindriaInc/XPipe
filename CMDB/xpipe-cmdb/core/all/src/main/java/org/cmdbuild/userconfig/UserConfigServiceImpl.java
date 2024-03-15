/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.eventbus.EventBus;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.io.CmImageUtils.checkIsImage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.url.CmUrlUtils.isDataUrl;
import static org.cmdbuild.utils.url.CmUrlUtils.urlToByteArray;

@Component
public class UserConfigServiceImpl implements UserConfigService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserConfigRepository configRepository;
    private final OperationUserSupplier operationUser;
    private final EventBus eventBus;

    public UserConfigServiceImpl(UserConfigRepository configRepository, OperationUserSupplier operationUser, EventBusService eventBusService) {
        this.configRepository = checkNotNull(configRepository);
        this.operationUser = checkNotNull(operationUser);
        eventBus = eventBusService.getContextEventBus();
    }

    @Override
    public Map<String, String> getByUsername(String username) {
        return configRepository.getByUsername(username);
    }

    @Override
    public void setByUsername(String username, Map<String, String> data) {
        validateConfig(data);
        configRepository.setByUsername(username, data);
        eventBus.post(UserConfigChangedEvent.INSTANCE);
//        sessionRepository.getSessionsByUsername(username).stream().map(s -> SessionImpl.copyOf(s).addSessionData((Map) data).build()).forEach(sessionRepository::updateSession);//TODO check this
    }

    @Override
    @Nullable
    public Optional<String> getByUsername(String username, String key) {
        logger.debug("get config by usename = {} key = {}", username, key);
        Map<String, String> config = getByUsername(username);
        return config == null ? null : Optional.fromNullable(config.get(key));
    }

    @Override
    public void setByUsername(String username, String key, @Nullable String value) {
        logger.info("set config by usename = {} key = {} value = {}", username, key, value);
        Map<String, String> config = getByUsername(username);
        setByUsername(username, map(config).with(key, value));
    }

    @Override
    public void deleteByUsername(String username, String key) {
        logger.info("delete config by usename = {} key = {}", username, key);
        Map<String, String> config = getByUsername(username);
        setByUsername(username, map(config).withoutKey(key));
    }

    @Override
    @Nullable
    public String getForCurrentUsernameOrNull(String key) {
        return getByUsernameOrNull(operationUser.getUsername(), key);
    }

    @Override
    public Map<String, String> getForCurrentUsername() {
        return getByUsername(operationUser.getUsername());
    }

    @Override
    public void setForCurrent(String key, String value) {
        setByUsername(operationUser.getUsername(), key, value);
    }

    private static void validateConfig(Map<String, String> data) {
        checkArgument(data.size() < 1000, "too many user config values");
        data.forEach((k, v) -> {
            try {
                checkArgument(nullToEmpty(k).length() < 1000, "invalid config key");
                checkArgument(nullToEmpty(v).length() < 1000000, "config value too big");
                switch (k) {
                    case "icon" -> {
                        if (!isBlank(v)) {
                            checkArgument(isDataUrl(v), "invalid value format");
                            byte[] bytes = urlToByteArray(v);
                            checkIsImage(bytes);
                        }
                    }
                }
            } catch (Exception ex) {
                throw illegalArgument(ex, "invalid user config for key =< %s >", abbreviate(k));
            }
        });
    }

}
