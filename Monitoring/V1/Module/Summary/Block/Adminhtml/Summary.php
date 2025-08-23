<?php

namespace Monitoring\Summary\Block\Adminhtml;

use Magento\Backend\Block\Template\Context;
use Magento\Framework\View\Element\Template;
use Monitoring\Summary\Helper\SummaryHelper;

class Summary extends Template
{

    private $authSession;


    public function __construct(
        Context $context,
        \Magento\Backend\Model\Auth\Session $authSession,

        array $data = [])
    {
        $this->authSession = $authSession;
        parent::__construct($context, $data);

    }

    public function getSummaryDashboard()
    {
        return SummaryHelper::getMonitoringSummaryDashboardUrl();

    }




}

