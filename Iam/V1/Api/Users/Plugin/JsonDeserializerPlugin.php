<?php
namespace Iam\Users\Plugin;

use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Model\StatusResponse;
use Iam\Users\Helper\UserHelper;


class JsonDeserializerPlugin
{
    public function aroundDeserialize(
        \Magento\Framework\Webapi\Rest\Request\Deserializer\Json $subject,
        callable $proceed,
        $encodedBody
    ) {
        // Validazione personalizzata
        if (UserHelper::isJson($encodedBody) === false) {

            LoggerFacade::error('Iam_Users::aroundDeserialize - Syntax Error: Invalid or malformed JSON payload');
            return new StatusResponse(400, false, 'Syntax Error: Invalid or malformed JSON payload');
        }

        // Chiamata al metodo originale
        return $proceed($encodedBody);
    }
}
