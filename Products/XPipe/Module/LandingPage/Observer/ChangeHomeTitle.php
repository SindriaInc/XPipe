<?php
namespace Sindria\LandingPage\Observer;

use Magento\Framework\Event\ObserverInterface;
use Magento\Framework\Event\Observer;
use Magento\Framework\App\RequestInterface;
use Magento\Framework\View\Page\Config as PageConfig;

class ChangeHomeTitle implements ObserverInterface
{
    protected $request;
    protected $pageConfig;

    public function __construct(
        RequestInterface $request,
        PageConfig $pageConfig
    ) {
        $this->request = $request;
        $this->pageConfig = $pageConfig;
    }

    public function execute(Observer $observer)
    {
        if ($this->request->getFullActionName() === 'cms_index_index') {
            $this->pageConfig->getTitle()->set('XPipe');
        }
    }
}
