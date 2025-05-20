# Module Logger

Magento 2 module for override magento module-logger features.

## Setup module

- Require your custom module: `composer require core/module-logger:@dev` OR `composer require core/module-logger:1.0.0`
- Run setup upgrade: `php bin/magento setup:upgrade`


## Usage


use Core\Logger\Facade\LoggerFacade;

LoggerFacade::info('Ordine completato', ['order_id' => 1234]);
LoggerFacade::debug('Stato pagamento aggiornato');
LoggerFacade::log('customLevel', []); // fallback dinamico, se supportato