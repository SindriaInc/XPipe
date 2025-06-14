<?php
namespace Pipelines\TemplateCatalog\Service;
use Core\Http\Facade\HttpFacade;
use Pipelines\TemplateCatalog\Helper\TemplateCatalogHelper;

class TemplateCatalogService
{
    private string $adminAccessToken;
    private string $adminUsername;
    private string $adminPassword;

    private const API_COLLECTOR_ADMIN_LOGIN_URL = 'http://172.16.10.101/rest/V1/integration/admin/token';
    private const API_COLLECTOR_GET_PRODUCTS_URL = 'http://172.16.10.101/rest/V1/products?searchCriteria=-1';

    public function __construct()
    {
        $this->adminUsername = TemplateCatalogHelper::getAdminUsername();
        $this->adminPassword = TemplateCatalogHelper::getAdminPassword();
        $this->adminLogin($this->adminUsername, $this->adminPassword);
    }
    
    private function adminLogin(string $adminUsername, string $adminPassword): void
    {
        $uri = self::API_COLLECTOR_ADMIN_LOGIN_URL;

        $headers = [
            'Content-Type' => 'application/json',
        ];

        $payload = json_encode(['username' => $adminUsername, 'password' => $adminPassword]);
        $response = HttpFacade::postRaw($uri, $headers, $payload);

        $cleanedAdminAccessToken = substr($response->getBody(), 1, -1);
        $this->adminAccessToken = $cleanedAdminAccessToken;
    }

    public function getProductsAdmin()
    {
        $uri = self::API_COLLECTOR_GET_PRODUCTS_URL;

        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->adminAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        return $response->getBody();
    }


}
