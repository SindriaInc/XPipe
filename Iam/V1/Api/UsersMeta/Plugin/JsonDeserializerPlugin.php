<?php
namespace Iam\UsersMeta\Plugin;

use Core\Logger\Facade\LoggerFacade;
use Core\MicroFramework\Model\StatusResponse;
use Iam\UsersMeta\Helper\UserMetaHelper;


class JsonDeserializerPlugin
{
    public function aroundDeserialize(
        \Magento\Framework\Webapi\Rest\Request\Deserializer\Json $subject,
        callable $proceed,
        $encodedBody
    ) {
        // Validazione personalizzata
        if (UserMetaHelper::isJson($encodedBody) === false) {

            LoggerFacade::error('Iam_UsersMeta::aroundDeserialize - Syntax Error: Invalid or malformed JSON payload');
            return new StatusResponse(400, false, 'Syntax Error: Invalid or malformed JSON payload');
        }

        // Chiamata al metodo originale
        return $proceed($encodedBody);
    }
}
