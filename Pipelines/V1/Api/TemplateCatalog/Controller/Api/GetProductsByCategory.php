<?php
namespace Pipelines\TemplateCatalog\Controller\Api;

use Magento\Framework\App\RequestInterface;

use Core\MicroFramework\Api\Data\StatusResponseInterface;
use Core\MicroFramework\Model\StatusResponse;
use Core\MicroFramework\Action\ValidateAccessTokenTrait;
use Core\Logger\Facade\LoggerFacade;

use Pipelines\TemplateCatalog\Helper\TemplateCatalogHelper;
use Pipelines\TemplateCatalog\Service\TemplateCatalogService;

class GetProductsByCategory
{
    use ValidateAccessTokenTrait;

    protected TemplateCatalogService $templateCatalogService;
    protected RequestInterface $request;
    private string $accessToken;

    public function __construct(
        TemplateCatalogService $templateCatalogService,
        RequestInterface       $request
    ) {
        $this->templateCatalogService = $templateCatalogService;
        $this->request = $request;

        $this->accessToken = TemplateCatalogHelper::getPipelinesTemplateCatalogAccessToken();
    }

    /**
     * @param string $category_id
     * @return StatusResponseInterface
     */
    public function execute(string $category_id) : StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            $products = $this->templateCatalogService->getProductsByCategory($category_id);

            $data = ['products' => $products];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
