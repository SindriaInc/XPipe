<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Faq\Block\Adminhtml\Faq\Edit;


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


    public function getFaqId() : int
    {
       return $this->request->getParam('faq_id', 0);
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
