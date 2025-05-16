package org.cmdbuild.dms.cmis;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CmisDmsRepositoryConfig {

    private final String url, username, password;
    private final int readTimeout;

    private CmisDmsRepositoryConfig(CmisDmsRepositoryConfigBuilder builder) {
        this.url = checkNotBlank(builder.url);
        this.username = checkNotBlank(builder.username);
        this.password = checkNotBlank(builder.password);
        this.readTimeout = firstNotNull(builder.readTimeout, 30000);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public static CmisDmsRepositoryConfigBuilder builder() {
        return new CmisDmsRepositoryConfigBuilder();
    }

    public static CmisDmsRepositoryConfigBuilder copyOf(CmisDmsRepositoryConfig source) {
        return new CmisDmsRepositoryConfigBuilder()
                .withUrl(source.getUrl())
                .withUsername(source.getUsername())
                .withPassword(source.getPassword())
                .withReadTimeout(source.getReadTimeout());
    }

    public static CmisDmsRepositoryConfig from(CmisConfiguration cmisConfiguration) {
        return CmisDmsRepositoryConfig.builder()
                .withUsername(cmisConfiguration.getCmisUser())
                .withUrl(cmisConfiguration.getCmisUrl())
                .withPassword(cmisConfiguration.getCmisPassword()).build();
    }

    public static class CmisDmsRepositoryConfigBuilder implements Builder<CmisDmsRepositoryConfig, CmisDmsRepositoryConfigBuilder> {

        private String url;
        private String username;
        private String password;
        private Integer readTimeout;

        public CmisDmsRepositoryConfigBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public CmisDmsRepositoryConfigBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public CmisDmsRepositoryConfigBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public CmisDmsRepositoryConfigBuilder withReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @Override
        public CmisDmsRepositoryConfig build() {
            return new CmisDmsRepositoryConfig(this);
        }

    }
}
