package org.cmdbuild.services.soap.client;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import static com.google.common.reflect.Reflection.newProxy;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;
import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.PASSWORD_TYPE;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_REF;
import static org.apache.wss4j.common.ConfigurationConstants.USER;
import static org.apache.wss4j.common.ConfigurationConstants.USERNAME_TOKEN;
import static org.apache.wss4j.common.WSS4JConstants.PW_DIGEST;
import static org.apache.wss4j.common.WSS4JConstants.PW_NONE;
import static org.apache.wss4j.common.WSS4JConstants.PW_TEXT;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import com.google.common.reflect.AbstractInvocationHandler;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.Builder;
import org.cmdbuild.services.soap.client.beans.Private;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdbuildSoapClient<T> implements SoapClient<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<T> proxyClass;
    private final String url;
    private final Authentication authentication;
    private final T proxy;

    private CmdbuildSoapClient(SoapClientBuilder<T> builder) {
        this.proxyClass = builder.proxyClass;
        this.url = builder.url;
        this.authentication = builder.authentication;
        this.proxy = newProxy(proxyClass, new AbstractInvocationHandler() {

            private final Supplier<T> proxySupplier = Suppliers.memoize(() -> prepareJaxRsClient());

            @Override
            protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    T jaxWsproxy = proxySupplier.get();

                    String requestId = randomId();
                    String actionId = "soap_" + proxyClass.getSimpleName() + "_" + method.getName();
                    logger.debug("invoking soap ws with requestId = {} actionId = {}", requestId, actionId);

//					Map<String, List<String>> httpHeaders = new HashMap<>(); TODO add requestId/trackingId headers
//					httpHeaders.put("CMDBuild-RequestId", Arrays.asList(requestId)); 
//					httpHeaders.put("CMDBuild-ActionId", Arrays.asList(actionId));
//					client.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, httpHeaders);
                    return method.invoke(jaxWsproxy, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }

            private T prepareJaxRsClient() {
                JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
                proxyFactory.setServiceClass(proxyClass);
                proxyFactory.setAddress(url);
                Object jaxWsproxy = proxyFactory.create();
                Client client = ClientProxy.getClient(jaxWsproxy);
                Endpoint endpoint = client.getEndpoint(); 
                authentication.accept(new AuthenticationVisitor() {

                    @Override
                    public void accept(PasswordAuthentication element) {
                        logger.info("configure soap client with auth = {}", element.getType().toClient());
                        switch (element.getType()) {
                            case NONE:
                                break; // nothing to do
                            case TEXT:
                            case DIGEST:
                                endpoint.getOutInterceptors().add(new WSS4JOutInterceptor(map(
                                        ACTION, USERNAME_TOKEN,
                                        USER, element.getUsername(),
                                        PASSWORD_TYPE, element.getType().toClient(),
                                        PW_CALLBACK_REF, new ClientPasswordCallback(element.getUsername(), element.getPassword())
                                )));
                                break;
                        }
                    }

                    @Override
                    public void accept(TokenAuthenticator element) {
                        logger.info("configure soap client with auth = `CMDBuild-Authorization` header token");
                        client.getRequestContext().put(Header.HEADER_LIST, list(header("CMDBuild-Authorization", element.getValue())));
                    }

                });
                return (T) jaxWsproxy;
            }
        });

    }

    public Class<T> getProxyClass() {
        return proxyClass;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public T getProxy() {
        return proxy;
    }

    private Header header(String name, String value) {
        try {
            return new Header(new QName(name), value, new JAXBDataBinding(String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final class ClientPasswordCallback implements CallbackHandler {

        private final String password;
        private final String username;

        public ClientPasswordCallback(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
            if (username.equals(pc.getIdentifier())) {
                pc.setPassword(password);
            }
        }
    }

    public static <T> SoapClientBuilder<T> aSoapClient() {
        return new SoapClientBuilder<>();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
                .append("proxy class", proxyClass) //
                .append("url", url) //
                .append("authentication", authentication) //
                .toString();
    }

    public static TokenAuthenticator token(Supplier<String> value) {
        return new TokenAuthenticator(notNull(value));
    }

    public static interface Authentication {

        void accept(AuthenticationVisitor visitor);

    }

    public static interface AuthenticationVisitor {

        void accept(PasswordAuthentication element);

        void accept(TokenAuthenticator element);

    }

    public static enum PasswordType {

        NONE {
            @Override
            String toClient() {
                return PW_NONE;
            }
        },
        TEXT {
            @Override
            String toClient() {
                return PW_TEXT;
            }
        },
        /**
         * @deprecated digest password auth requires cleartext password on db, and this is NOT recommended
         */
        @Deprecated
        DIGEST {
            @Override
            String toClient() {
                return PW_DIGEST;
            }
        },;

        abstract String toClient();

    }

    public static class PasswordAuthentication implements Authentication {

        private final PasswordType type;
        private final String username;
        private final String password;

        private PasswordAuthentication(@Nullable PasswordType type, String username, String password) {
            this.type = firstNotNull(type, PasswordType.NONE);
            switch (this.type) {
                case DIGEST:
                case TEXT:
                    checkNotBlank(username);
                    checkNotBlank(password);
            }
            this.username = username;
            this.password = password;
        }

        @Override
        public void accept(AuthenticationVisitor visitor) {
            visitor.accept(this);
        }

        public PasswordType getType() {
            return type;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "PasswordAuthentication{" + "type=" + type + ", username=" + username + '}';
        }

    }

    public static Authentication usernameAndPassword(@Nullable PasswordType type, String username, String password) {
        return new PasswordAuthentication(type, username, password);
    }

    public static class TokenAuthenticator implements Authentication {

        private final Supplier<String> value;

        /**
         * Use factory method.
         *
         * @param value
         */
        private TokenAuthenticator(Supplier<String> value) {
            this.value = value;
        }

        @Override
        public void accept(AuthenticationVisitor visitor) {
            visitor.accept(this);
        }

        public String getValue() {
            return value.get();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof TokenAuthenticator)) {
                return false;
            }
            TokenAuthenticator other = TokenAuthenticator.class.cast(obj);
            return new EqualsBuilder() //
                    .append(this.value, other.value) //
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder() //
                    .append(value) //
                    .build();
        }

        @Override
        public String toString() {
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }

    }

    public static class SoapClientBuilder<T> implements Builder<CmdbuildSoapClient<T>> {

        private Class proxyClass = Private.class;
        private String url;
        private Authentication authentication;

        @Deprecated
        private PasswordType passwordType;
        @Deprecated
        private String username;
        @Deprecated
        private String password;

        /**
         * @deprecated Should be private but it's kept for backward
         * compatibility.
         */
        @Deprecated
        public SoapClientBuilder() {
        }

        @Override
        public CmdbuildSoapClient<T> build() {
            notNull(proxyClass);
            notBlank(url);
            if (authentication == null) {
                authentication = usernameAndPassword(passwordType, username, password);
            }
            notNull(authentication);
            return new CmdbuildSoapClient<>(this);
        }

        public SoapClientBuilder<T> forClass(Class<T> proxyClass) {
            this.proxyClass = proxyClass;
            return this;
        }

        public SoapClientBuilder<T> withAuthentication(Authentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public SoapClientBuilder<T> withUrl(String url) {
            this.url = url;
            return this;
        }

        @Deprecated
        public SoapClientBuilder<T> withPasswordType(PasswordType passwordType) {
            this.passwordType = passwordType;
            return this;
        }

        @Deprecated
        public SoapClientBuilder<T> withUsername(String username) {
            this.username = username;
            return this;
        }

        @Deprecated
        public SoapClientBuilder<T> withPassword(String password) {
            this.password = password;
            return this;
        }

    }

}
