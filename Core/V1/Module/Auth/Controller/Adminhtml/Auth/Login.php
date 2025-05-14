<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Core\Auth\Controller\Adminhtml\Auth;

use Magento\Backend\App\Area\FrontNameResolver;
use Magento\Backend\App\BackendAppList;
use Magento\Backend\Controller\Adminhtml\Auth\Login as MagentoLogin;
use Magento\Backend\Model\UrlFactory;
use Magento\Framework\App\Request\Http;

class Login extends MagentoLogin
{
    public function __construct(\Core\Auth\App\Action\Context $context, \Magento\Framework\View\Result\PageFactory $resultPageFactory, FrontNameResolver $frontNameResolver = null, BackendAppList $backendAppList = null, UrlFactory $backendUrlFactory = null, Http $http = null)
    {
        parent::__construct($context, $resultPageFactory, $frontNameResolver, $backendAppList, $backendUrlFactory, $http);
    }
}
