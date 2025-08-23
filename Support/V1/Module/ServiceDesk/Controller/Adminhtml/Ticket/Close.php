<?php
namespace Support\ServiceDesk\Controller\Adminhtml\Ticket;

use Core\Logger\Facade\LoggerFacade;
use Magento\Backend\App\Action\Context;
use Magento\Framework\Controller\ResultFactory;
use Magento\Framework\View\Result\PageFactory;
use Magento\Backend\App\Action;
use Support\ServiceDesk\Service\GithubIssuesService;

class Close extends Action
{

    const ADMIN_RESOURCE = 'Support_ServiceDesk::closeticket';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    private GithubIssuesService  $githubIssuesService;

    /**
     * Index constructor.
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     */
    public function __construct(
        Context $context,
        PageFactory $resultPageFactory,
        GithubIssuesService $githubIssuesService
    ) {
        parent::__construct($context);
        $this->resultPageFactory = $resultPageFactory;
        $this->githubIssuesService = $githubIssuesService;
    }

    /**
     * Execute method for Pipeline Index
     *
     * @return \Magento\Framework\View\Result\Page
     */
    public function execute()
    {
        try {
            $resultRedirect = $this->resultFactory->create(ResultFactory::TYPE_REDIRECT);
            $ticketId = $this->getRequest()->getParam('ticket_id');


            $resource = $this->githubIssuesService->closeTicket($ticketId);

            if ($resource['success'] === false) {
                $this->messageManager->addErrorMessage(
                    __('Unable to close ticket.')
                );

                LoggerFacade::debug('Unable to close ticket.', [
                    'ticket_id' => $ticketId
                ]);

                return $resultRedirect->setPath('servicedesk/index/index');
            }

            $this->messageManager->addSuccessMessage(
                __('Ticket closed successfully.')
            );

            LoggerFacade::info('Ticket closed successfully.', [
                'ticket_id' => $ticketId
            ]);

            return $resultRedirect->setPath('servicedesk/index/index');

        } catch (\Exception $e) {
            $this->messageManager->addErrorMessage(
                __('Exception while closing the ticket.' . ' ' . $e->getMessage())
            );

            LoggerFacade::error('Exception while closing the ticket.' . ' ' . $e->getMessage(), [
                'pipeline_id' => $ticketId
            ]);

            return $resultRedirect->setPath('servicedesk/index/index');
        }
    }
}
