<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Support\TicketForm\Block\Adminhtml\Buttons;

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


    public function __construct(
        UrlInterface $url

    ) {
        $this->url = $url;
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
