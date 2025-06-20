<?php
namespace Pipelines\TemplateCatalog\Service;

use Core\MicroFramework\Service\CatalogService;
use Pipelines\TemplateCatalog\Helper\TemplateCatalogHelper;
use Core\Http\Facade\HttpFacade;

class TemplateCatalogService extends CatalogService
{
    public function __construct()
    {
        parent::__construct(TemplateCatalogHelper::getPipelinesCollectorBaseUrl(), TemplateCatalogHelper::getPipelinesCollectorAdminUsername(), TemplateCatalogHelper::getPipelinesCollectorAdminPassword());
    }

}
