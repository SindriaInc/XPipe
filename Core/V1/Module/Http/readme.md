# Module Http

Magento 2 module for http utils.

## Setup module

- Require your custom module: `composer require core/module-http:@dev` OR `composer require core/module-http:1.0.0`
- Run setup upgrade: `php bin/magento setup:upgrade`


## Usage

```php
use Core\Http\Facade\HttpFacade;

// Example code
private const API_CONFIGMAP_LIST_URL = 'https://dev-vault-xpipe.sindria.org/v1/%s/metadata?list=true';

$uri = sprintf(self::API_CONFIGMAP_LIST_URL, $owner);

$headers = [
    'Content-Type' => 'application/json',
    "X-Vault-Token" => $this->token,
];

$payload = json_encode(ConfigmapHelper::preparePayload($data));


// Usage
HttpFacade::get($uri, $headers);
HttpFacade::post($uri, $headers, $payload);
HttpFacade::put($uri, $headers, $payload);
HttpFacade::delete($uri, $headers);
HttpFacade::postRaw($uri, $headers, $payload);
HttpFacade::putRaw($uri, $headers, $payload);
HttpFacade::deleteRaw($uri, $headers);