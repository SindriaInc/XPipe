<?php
namespace Pipelines\TemplateCatalog\Controller\Api;

use Pipelines\TemplateCatalog\Api\Data\StatusResponseInterface;
use Pipelines\TemplateCatalog\Helper\TemplateCatalogHelper;
use Pipelines\TemplateCatalog\Model\StatusResponse;
use Pipelines\TemplateCatalog\Service\TemplateCatalogService;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;
use Pipelines\TemplateCatalog\Traits\ValidateAccessTokenTrait;

class Index
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
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $this->validateAccessToken($this->accessToken);

            $productsAdmin = $this->templateCatalogService->getProductsAdmin();

            $data = ['products' => $productsAdmin];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
