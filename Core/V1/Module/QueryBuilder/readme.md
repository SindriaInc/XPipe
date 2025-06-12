# Module QueryBuilder

Magento 2 module to add query builder utils.

## Setup module

- Require your custom module: `composer require core/module-query-builder:@dev` OR `composer require core/module-query-builder:1.0.0`
- Run setup upgrade: `php bin/magento setup:upgrade`


## Usage

```php
use Core\QueryBuilder\Facade\QueryBuilderFacade;

// Example code
private const API_CONFIGMAP_LIST_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/metadata?list=true';

$uri = sprintf(self::API_CONFIGMAP_LIST_URL, $owner);

$headers = [
    'Content-Type' => 'application/json',
    "X-Vault-Token" => $this->token,
];

$payload = json_encode(ConfigmapHelper::preparePayload($data));


// Usage
QueryBuilderFacade::get($uri, $headers);
QueryBuilderFacade::post($uri, $headers, $payload);
QueryBuilderFacade::put($uri, $headers, $payload);
QueryBuilderFacade::delete($uri, $headers);
QueryBuilderFacade::postRaw($uri, $headers, $payload);
QueryBuilderFacade::putRaw($uri, $headers, $payload);
QueryBuilderFacade::deleteRaw($uri, $headers);