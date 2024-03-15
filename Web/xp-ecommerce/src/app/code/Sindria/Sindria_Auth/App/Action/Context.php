<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Sindria\Auth\App\Action;

use Magento\Backend\App\Action\Context as MagentoContext;
use Magento\Framework\Controller\ResultFactory;
use Sindria\Auth\Model\Auth;

class Context extends MagentoContext
{
    public function __construct(\Magento\Framework\App\RequestInterface $request, \Magento\Framework\App\ResponseInterface $response, \Magento\Framework\ObjectManagerInterface $objectManager, \Magento\Framework\Event\ManagerInterface $eventManager, \Magento\Framework\UrlInterface $url, \Magento\Framework\App\Response\RedirectInterface $redirect, \Magento\Framework\App\ActionFlag $actionFlag, \Magento\Framework\App\ViewInterface $view, \Magento\Framework\Message\ManagerInterface $messageManager, \Magento\Backend\Model\View\Result\RedirectFactory $resultRedirectFactory, ResultFactory $resultFactory, \Magento\Backend\Model\Session $session, \Magento\Framework\AuthorizationInterface $authorization, \Sindria\Auth\Model\Auth $auth, \Magento\Backend\Helper\Data $helper, \Magento\Backend\Model\UrlInterface $backendUrl, \Magento\Framework\Data\Form\FormKey\Validator $formKeyValidator, \Magento\Framework\Locale\ResolverInterface $localeResolver, $canUseBaseUrl = false)
    {
        parent::__construct($request, $response, $objectManager, $eventManager, $url, $redirect, $actionFlag, $view, $messageManager, $resultRedirectFactory, $resultFactory, $session, $authorization, $auth, $helper, $backendUrl, $formKeyValidator, $localeResolver, $canUseBaseUrl);
    }
}
