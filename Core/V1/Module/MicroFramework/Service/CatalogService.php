<?php

namespace Core\MicroFramework\Service;

use Core\Http\Facade\HttpFacade;

class CatalogService
{
    private string $adminAccessToken;
    private string $adminBaseUrl;
    private string $adminUsername;
    private string $adminPassword;

    private const API_COLLECTOR_ADMIN_LOGIN_URL = '%s/rest/V1/integration/admin/token';

    private const API_COLLECTOR_GET_ADMIN_PRODUCTS_URL = '%s/rest/V1/products?searchCriteria=-1';

    public function __construct(string $adminBaseUrl, string $adminUsername, string $adminPassword)
    {
        $this->adminBaseUrl = $adminBaseUrl;
        $this->adminUsername = $adminUsername;
        $this->adminPassword = $adminPassword;

        $this->adminLogin();
    }

    private function adminLogin(): void
    {
        $uri = sprintf(self::API_COLLECTOR_ADMIN_LOGIN_URL, $this->adminBaseUrl);

        $headers = [
            'Content-Type' => 'application/json',
        ];

        $payload = json_encode(['username' => $this->adminUsername, 'password' => $this->adminPassword]);
        $response = HttpFacade::postRaw($uri, $headers, $payload);

        $cleanedAdminAccessToken = substr($response->getBody(), 1, -1);
        $this->adminAccessToken = $cleanedAdminAccessToken;
    }

    public function getProductsAdmin()
    {
        $uri = sprintf(self::API_COLLECTOR_GET_ADMIN_PRODUCTS_URL, $this->adminBaseUrl);

        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->adminAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        return $response->getBody();
    }

}