<?php
namespace Core\MicroFramework\Plugin;

use Magento\Framework\Webapi\Rest\Response;
use Core\MicroFramework\Api\Data\StatusResponseInterface;

class ForceHttpStatusPlugin
{
    private Response $response;

    public function __construct(Response $response)
    {
        $this->response = $response;
    }

    public function afterRender(
        \Magento\Framework\Webapi\Rest\Response\Renderer\Json $subject,
        $result,
        $data
    ) {
        if ($data instanceof StatusResponseInterface) {
            $this->response->setHttpResponseCode($data->getCode());
        }
        return $result;
    }
}
