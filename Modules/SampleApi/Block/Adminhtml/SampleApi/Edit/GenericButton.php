<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\SampleApi\Block\Adminhtml\SampleApi\Edit;


use Magento\Framework\App\RequestInterface;
use Magento\Framework\UrlInterface;

/**
 * Class GenericButton
 */
class GenericButton
{
    /**
     * @var UrlInterface
     */
    protected $url;

    private RequestInterface $request;



    public function __construct(
        UrlInterface $url,
        RequestInterface $request
    ) {
        $this->url = $url;
        $this->request = $request;
    }


    public function getId() : int
    {
       return $this->request->getParam('id', 0);
    }

    /**
     * Generate url by route and parameters
     *
     * @param   string $route
     * @param   array $params
     * @return  string
     */
    public function getUrl($route = '', $params = [])
    {
        return $this->url->getUrl($route, $params);
    }
}
