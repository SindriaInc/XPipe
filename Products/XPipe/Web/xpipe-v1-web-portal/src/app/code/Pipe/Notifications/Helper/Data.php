<?php
namespace Pipe\Notifications\Helper;

use Magento\Framework\App\Helper\AbstractHelper;
use Magento\AdminNotification\Model\Inbox;

class Data extends AbstractHelper
{
    protected $inbox;

    public function __construct(
        \Magento\Framework\App\Helper\Context $context,
        Inbox $inbox
    ) {
        parent::__construct($context);
        $this->inbox = $inbox;
    }

    public function addNotification($title, $message, $severity, $url = '', $source = '')
    {
        $data = [
            'title' => $title . ($source ? " [$source]" : ''),
            'description' => $message,
            'url' => $url,
        ];

        switch ($severity) {
            case 'critical':
                $this->inbox->addCritical($data);
                break;
            case 'major':
                $this->inbox->addMajor('TEST', 'Description Test', 'http://test.com', false);
                break;
            case 'minor':
                $this->inbox->addMinor($data);
                break;
            default:
                $this->inbox->addNotice($data);
                break;
        }
    }
}
