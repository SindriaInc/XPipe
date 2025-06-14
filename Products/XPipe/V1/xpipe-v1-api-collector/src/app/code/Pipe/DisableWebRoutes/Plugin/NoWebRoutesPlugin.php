<?php

namespace Pipe\DisableWebRoutes\Plugin;

use Magento\Framework\App\RequestInterface;
use Magento\Framework\Controller\Result\JsonFactory;
use Magento\Framework\App\FrontControllerInterface;
use Magento\Backend\App\Area\FrontNameResolver;

class NoWebRoutesPlugin
{
    private JsonFactory $jsonFactory;
    private string $adminPath;

    public function __construct(JsonFactory $jsonFactory, FrontNameResolver $frontNameResolver)
    {
        $this->jsonFactory = $jsonFactory;
        $this->adminPath = $frontNameResolver->getFrontName();
    }

    public function aroundDispatch(FrontControllerInterface $subject, \Closure $proceed, RequestInterface $request)
    {
        $path = $request->getPathInfo();

        // Consenti solo API e admin
        if (preg_match('#^/(rest|V1|graphql)#i', $path) || strpos($path, '/' . $this->adminPath) === 0) {
            return $proceed($request);
        }

        // Blocca anche static e media
        if (preg_match('#^/(static|media)#i', $path)) {
            $result = $this->jsonFactory->create();
            return $result->setData(["error" => "Web access disabled"])->setHttpResponseCode(403);
        }

        // Tutto il resto: risposta JSON con errore
        $result = $this->jsonFactory->create();
        return $result->setData(["error" => "Web access disabled"])->setHttpResponseCode(403);
    }
}
