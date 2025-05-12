# SSO Keycloak

WordPress plugin to add sso integration with keycloak.

## Requirements

- WordPress >= 4.7
- PHP >= 7.0

## Configuration

List of required envs.

| Key                             |          Value           |
|---------------------------------|:------------------------:|
| XPIPE_WEB_AUTH_BASE_URL         |      keycloak FQDN       |
| XPIPE_WEB_AUTH_LEGACY_BASE_URL  | keycloak FQDN with /auth |
| XPIPE_WEB_AUTH_REALM            |        realm name        |
| XPIPE_WEB_AUTH_CLIENT_ID        |      your client id      |
| XPIPE_WEB_AUTH_CLIENT_SECRET    |    your client secret    |
| SSO_KEYCLOAK_AUTOPROFILE_TOGGLE |        true/false        |
| SSO_KEYCLOAK_SSOBUTTON_TOGGLE   |        true/false        |