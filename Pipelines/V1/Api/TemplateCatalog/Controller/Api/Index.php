<?php
namespace Pipelines\TemplateCatalog\Controller\Api;

use Pipelines\TemplateCatalog\Api\Data\StatusResponseInterface;
use Pipelines\TemplateCatalog\Model\StatusResponse;
use Pipelines\TemplateCatalog\Service\TemplateCatalogService;
use Pipelines\TemplateCatalog\Helper\SystemEnvHelper;
use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\RequestInterface;

class Index
{
    protected TemplateCatalogService $templateCatalogService;
    protected RequestInterface $request;

    public function __construct(
        TemplateCatalogService $templateCatalogService,
        RequestInterface       $request
    ) {
        $this->templateCatalogService = $templateCatalogService;
        $this->request = $request;
    }

    /**
     * @return StatusResponseInterface
     */
    public function execute() : StatusResponseInterface
    {
        try {
            $token = SystemEnvHelper::get('PIPELINES_TEMPLATE_CATALOG_ACCESS_TOKEN', '1234');

            if ($token !== $this->request->getHeader('X-Token-XPipe')) {
                LoggerFacade::error('Invalid Token');
                return new StatusResponse(403, false, 'Invalid Token');
            }


            $productsAdmin = $this->templateCatalogService->getProductsAdmin();

            $data = ['products' => $productsAdmin];

            return new StatusResponse(200, true, 'ok', $data);

        } catch (\Exception $e) {
            LoggerFacade::error('Internal error', ['error' => $e]);
            return  new StatusResponse(500, false, 'Internal server error');
        }
    }
}
