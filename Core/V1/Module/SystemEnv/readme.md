# Module SystemEnv

Magento 2 module to add global system env interface with low overhead.

This module permit global access to system env trought facade and load only whitelisted app env one time in a singleton object before dispatch front controller.

## Setup module

- Require your custom module: `composer require core/module-system-env:@dev` OR `composer require core/module-system-env:1.0.0`
- Run setup upgrade: `php bin/magento setup:upgrade`


## Usage

```php
use Core\SystemEnv\Facade\SystemEnvFacade;

// Usage
SystemEnvFacade::get('MY_ENV_KEY');
SystemEnvFacade::get('MY_ENV_KEY', 'default');
SystemEnvFacade::all();
```
