<?php
namespace Iam\Groups\Plugin;

use Magento\Framework\Webapi\Rest\Response;
use Magento\Framework\Webapi\Rest\Response\Renderer\Json as JsonRenderer;
use Core\Logger\Facade\LoggerFacade;

class ForceHttpStatusPlugin
{
//    /**
//     * @var Response
//     */
//    private Response $response;
//
//    public function __construct(Response $response)
//    {
//        $this->response = $response;
//    }
//
//    /**
//     * Forza lo status HTTP se "code" è presente nel payload
//     *
//     * @param JsonRenderer $subject
//     * @param string $result
//     * @param object $data
//     * @return string
//     */
//    public function afterRender(JsonRenderer $subject, string $result, object $data): string
//    {
//        if (isset($data->code) && is_numeric($data->code)) {
//            $code = (int)$data->code;
//
//            if ($code >= 100 && $code <= 599) {
//                // Disabled temp
//                //$this->response->setHttpResponseCode($code);
//                LoggerFacade::debug('Forced HTTP status code from plugin', ['code' => $code]);
//            }
//        }
//
//        return $result;
//    }
}
