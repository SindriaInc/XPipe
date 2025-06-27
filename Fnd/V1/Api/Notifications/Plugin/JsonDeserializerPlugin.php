<?php
namespace Fnd\Notifications\Plugin;

use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Model\StatusResponse;
use Fnd\Notifications\Helper\NotificationsHelper;
use Magento\Framework\Webapi\Exception as WebapiException;
use Magento\Framework\Phrase;

class JsonDeserializerPlugin
{
//    protected $myJsonValidator;
//
//    public function __construct(\Vendor\Module\Helper\JsonValidator $myJsonValidator)
//    {
//        $this->myJsonValidator = $myJsonValidator;
//    }

    public function aroundDeserialize(
        \Magento\Framework\Webapi\Rest\Request\Deserializer\Json $subject,
        callable $proceed,
        $encodedBody
    ) {
        // Validazione personalizzata
        if (NotificationsHelper::isJson($encodedBody) === false) {

            LoggerFacade::error('Fnd_Notifications::handle - Syntax Error: Invalid or malformed JSON payload');
            return new StatusResponse(400, false, 'Syntax Error: Invalid or malformed JSON payload');
        }

        // Chiamata al metodo originale
        return $proceed($encodedBody);
    }
}
