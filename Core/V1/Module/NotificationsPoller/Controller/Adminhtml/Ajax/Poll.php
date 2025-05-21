<?php
namespace Core\NotificationsPoller\Controller\Adminhtml\Ajax;

use Magento\Backend\App\Action;
use Magento\Framework\Controller\Result\JsonFactory;
use Magento\AdminNotification\Model\ResourceModel\Inbox\CollectionFactory;

class Poll extends Action
{
    /**
     * @var JsonFactory
     */
    protected $resultJsonFactory;

    /**
     * @var CollectionFactory
     */
    protected $collectionFactory;

    /**
     * Poll constructor.
     *
     * @param Action\Context $context
     * @param JsonFactory $resultJsonFactory
     * @param CollectionFactory $collectionFactory
     */
    public function __construct(
        Action\Context $context,
        JsonFactory $resultJsonFactory,
        CollectionFactory $collectionFactory
    ) {
        parent::__construct($context);
        $this->resultJsonFactory = $resultJsonFactory;
        $this->collectionFactory = $collectionFactory;
    }

    /**
     * Poll unread admin notifications and return as JSON
     *
     * @return \Magento\Framework\Controller\Result\Json
     */
    public function execute()
    {
        $collection = $this->collectionFactory->create()
            ->addFieldToFilter('is_read', 0)
            ->setOrder('notification_id', 'DESC')
            ->setPageSize(5);

        $notifications = [];

        foreach ($collection as $notification) {
            $notifications[] = [
                'title'    => $notification->getTitle(),
                'message'  => $notification->getDescription(),
                'severity' => $notification->getSeverity()
            ];
        }

        return $this->resultJsonFactory->create()->setData([
            'notifications' => $notifications
        ]);
    }

    /**
     * Allow access (you may replace with actual ACL logic)
     *
     * @return bool
     */
    protected function _isAllowed()
    {
        return true; // oppure implementa ACL se necessario
    }
}
