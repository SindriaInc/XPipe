<?php

namespace Core\MicroFramework\Service;

use Core\Http\Facade\HttpFacade;

abstract class CatalogService
{
    private string $adminAccessToken;
    private string $adminBaseUrl;
    private string $adminUsername;
    private string $adminPassword;

    private const API_COLLECTOR_ADMIN_LOGIN_URL = '%s/rest/V1/integration/admin/token';
    private const API_COLLECTOR_GET_ADMIN_PRODUCTS_URL = '%s/rest/V1/products?searchCriteria=-1';
    private const API_COLLECTOR_GET_ADMIN_PRODUCTS_BY_CATEGORY_URL = '%s/rest/V1/products?searchCriteria[filterGroups][0][filters][0][field]=category_id&searchCriteria[filterGroups][0][filters][0][value]=%s&searchCriteria[filterGroups][0][filters][0][condition_type]=eq';
    private const API_COLLECTOR_GET_ADMIN_PRODUCT_BY_SKU_URL = '%s/rest/V1/products/%s';
    private const API_COLLECTOR_GET_ADMIN_CATEGORIES_URL = '%s/rest//V1/categories/list?searchCriteria=-1';

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

    public function getProducts() : array
    {
        $uri = sprintf(self::API_COLLECTOR_GET_ADMIN_PRODUCTS_URL, $this->adminBaseUrl);

        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->adminAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        $productsAdminRow = $response->getBody();

        return json_decode($productsAdminRow, true);
    }

    public function getProductsByCategory(string $categoryId) : array
    {
        $uri = sprintf(self::API_COLLECTOR_GET_ADMIN_PRODUCTS_BY_CATEGORY_URL, $this->adminBaseUrl, $categoryId);

        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->adminAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        $productsAdminRow = $response->getBody();

        return json_decode($productsAdminRow, true);
    }

    public function getProductBySku(string $sku) : array
    {
        $uri = sprintf(self::API_COLLECTOR_GET_ADMIN_PRODUCT_BY_SKU_URL, $this->adminBaseUrl, $sku);

        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->adminAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        $productAdminRow = $response->getBody();

        return json_decode($productAdminRow, true);
    }


    public function getCategories() : array
    {
        $uri = sprintf(self::API_COLLECTOR_GET_ADMIN_CATEGORIES_URL, $this->adminBaseUrl);

        $headers = [
            'Content-Type' => 'application/json',
            'Authorization' => 'Bearer ' . $this->adminAccessToken,
        ];

        $response = HttpFacade::get($uri, $headers);

        $categoriesAdminRow = $response->getBody();

        return json_decode($categoriesAdminRow, true);
    }

}