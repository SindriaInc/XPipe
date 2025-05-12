# SSO Keycloak

WordPress plugin to add sso integration with keycloak.

## Requirements

- WordPress >= 4.7
- PHP >= 7.0

## Configuration

List of required envs.

| Key                             |             Value             |
|---------------------------------|:-----------------------------:|
| SSO_KEYCLOAK_AUTH_BASE_URL      |   https://auth.example.com    |
| SSO_KEYCLOAK_AUTH_API_BASE_URL  | https://auth.example.com/auth |
| SSO_KEYCLOAK_AUTH_REALM         |          realm name           |
| SSO_KEYCLOAK_AUTH_CLIENT_ID     |           client id           |
| SSO_KEYCLOAK_AUTH_CLIENT_SECRET |         client secret         |
| SSO_KEYCLOAK_AUTH_CALLBACK_BASE |     https://example.com/_     |
| SSO_KEYCLOAK_AUTOPROFILE_TOGGLE |          true/false           |
| SSO_KEYCLOAK_SSOBUTTON_TOGGLE   |          true/false           |
| SSO_KEYCLOAK_IDPONLY_TOGGLE     |          true/false           |
