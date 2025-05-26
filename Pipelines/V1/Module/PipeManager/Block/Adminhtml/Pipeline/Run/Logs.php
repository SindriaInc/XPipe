<?php


namespace Pipelines\PipeManager\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;

class Logs extends Template
{

    public function __construct(Context $context, array $data = [])
    {
        parent::__construct($context, $data);
    }

    public function getLogs(): array
    {
        // Get param from request
        $this->getRequest()->getParams();

        // Call Api

        return [];

    }
}

