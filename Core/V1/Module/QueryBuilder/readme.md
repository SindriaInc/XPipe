# Module QueryBuilder

Magento 2 module to add query builder utils.

## Setup module

- Require your custom module: `composer require core/module-query-builder:@dev` OR `composer require core/module-query-builder:1.0.0`
- Run setup upgrade: `php bin/magento setup:upgrade`


## Usage

```php
use Core\QueryBuilder\Facade\QueryFacade;

// Usage
QueryFacade::query($table, $sql);
