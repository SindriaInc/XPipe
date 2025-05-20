<?php
namespace Core\Notifications\Service;

use Core\Logger\Facade\LoggerFacade;
use Magento\Framework\App\Helper\AbstractHelper;
use Magento\AdminNotification\Model\Inbox;
use Magento\Framework\App\Helper\Context;

class NotificationService extends AbstractHelper
{
    protected $inbox;

    public function __construct(
        Context $context,
        Inbox $inbox
    ) {
        parent::__construct($context);
        $this->inbox = $inbox;
    }

    public function addNotification($payload)
    {
        $severity = $payload['severity'];
        $entity = $payload['entity'] ?? '';
        $event = $payload['event'] ?? '';
        $detail = $payload['detail'] ?? '';
        $url = $payload['url'] ?? '';
        $isInternal = $payload['isInternal'] ?? false;

        switch ($severity) {
            case 'critical':
                $this->inbox->addCritical(ucfirst($entity) . ' ' . strtolower($event), $detail, $url, $isInternal);
                LoggerFacade::info('Notification added successfully', ['title' => ucfirst($entity) . ' ' . strtolower($event), 'message' => $detail, 'severity' => $severity]);
                break;
            case 'error':
                $this->inbox->addMajor(ucfirst($entity) . ' ' .  strtolower($event), $detail, $url, $isInternal);
                LoggerFacade::info('Notification added successfully', ['title' => ucfirst($entity) . ' ' . strtolower($event), 'message' => $detail, 'severity' => $severity]);
                break;
            case 'warning':
                $this->inbox->addMinor(ucfirst($entity) . ' ' .  strtolower($event), $detail, $url, $isInternal);
                LoggerFacade::info('Notification added successfully', ['title' => ucfirst($entity) . ' ' . strtolower($event), 'message' => $detail, 'severity' => $severity]);
                break;
            case 'info':
                $this->inbox->addNotice(ucfirst($entity) . ' ' .  strtolower($event), $detail, $url, $isInternal);
                LoggerFacade::info('Notification added successfully', ['title' => ucfirst($entity) . ' ' . strtolower($event), 'message' => $detail, 'severity' => $severity]);
                break;
            default:
                $this->inbox->addNotice(ucfirst($entity) . ' ' .  strtolower($event), $detail, $url, $isInternal);
                LoggerFacade::info('Notification added successfully', ['title' => ucfirst($entity) . ' ' . strtolower($event), 'message' => $detail, 'severity' => $severity]);
                break;
        }
    }
}
