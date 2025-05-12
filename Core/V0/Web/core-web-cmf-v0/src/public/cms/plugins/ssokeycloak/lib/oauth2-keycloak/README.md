# oauth2-keycloak

A wrapper for the Keycloak OAuth 2.0 Client Provider, support Authentication and Authorization.



## Installation

To install, use composer:

```php
composer require sindria/oauth2-keycloak
```



## Init

```php
$passport = Passport::init([
    'authServerUrl' => 'http://127.0.0.1:8080/auth',
    'realm'         => 'xxxx',
    'clientId'      => 'backend',
    'clientSecret'  => 'xxxxx',
    'redirectUri'   => 'http://127.0.0.1:8003/auto',
    'periodNoCheck' => 3600,
    'periodCheck'   => 180,
]);
```



## Authentication

### Login

```php
$user = $passport->checkLogin();
$user->getAttr('username');
$user->toArray();
```

### logout

```php
$passport->logout();
```

### Other Methds

```php
$passport->getAccessToken(); // can save in client
$passport->getToken(); // secret
$passport->getAuthorizationUrl();
$passport->getLogoutUrl();
```



## Authorization

```php
$user = $passport->checkAuth();

// permission
$user->can($resource, $scope);
$user->cannot($resource, $scope);

// list
$user->getClients();
$user->getPermissions();

// role
$user->getRoles();
$user->hasRole($role);
$user->inRoles($roleList);
```

