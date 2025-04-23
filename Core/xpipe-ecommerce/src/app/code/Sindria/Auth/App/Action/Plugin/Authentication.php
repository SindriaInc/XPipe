<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Auth\App\Action\Plugin;

use Magento\Backend\App\Action\Plugin\Authentication as MagentoAuthentication;
use Sindria\Auth\Model\Auth;

class Authentication extends MagentoAuthentication
{
    public function __construct(\Sindria\Auth\Model\Auth $auth, \Magento\Backend\Model\UrlInterface $url, \Magento\Framework\App\ResponseInterface $response, \Magento\Framework\App\ActionFlag $actionFlag, \Magento\Framework\Message\ManagerInterface $messageManager, \Magento\Backend\Model\UrlInterface $backendUrl, \Magento\Framework\Controller\Result\RedirectFactory $resultRedirectFactory, \Magento\Backend\App\BackendAppList $backendAppList, \Magento\Framework\Data\Form\FormKey\Validator $formKeyValidator)
    {
        parent::__construct($auth, $url, $response, $actionFlag, $messageManager, $backendUrl, $resultRedirectFactory, $backendAppList, $formKeyValidator);
    }
}
