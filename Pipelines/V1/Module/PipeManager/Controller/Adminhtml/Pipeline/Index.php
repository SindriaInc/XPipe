<?php
namespace Pipelines\PipeManager\Controller\Adminhtml\Pipeline;

use Magento\Backend\App\Action\Context;
use Magento\Framework\View\Result\PageFactory;
use Magento\Backend\App\Action;

class Index extends Action
{

    const ADMIN_RESOURCE = 'Pipelines_PipeManager::listruns';

    /**
     * @var PageFactory
     */
    protected $resultPageFactory;

    /**
     * Index constructor.
     *
     * @param Context $context
     * @param PageFactory $resultPageFactory
     */
    public function __construct(
        Context $context,
        PageFactory $resultPageFactory
    ) {
        parent::__construct($context);
        $this->resultPageFactory = $resultPageFactory;
    }

    /**
     * Execute method for Pipeline Index
     *
     * @return \Magento\Framework\View\Result\Page
     */
    public function execute()
    {
        // --- PATCH: salva pipeline_id in session, variante "IDE friendly"
        $pipelineId = $this->getRequest()->getParam('pipeline_id');
        $this->_objectManager->get(\Magento\Framework\Session\SessionManagerInterface::class)
            ->setData('pipeline_id', $pipelineId);

        // --- FINE PATCH ---

        /** @var \Magento\Framework\View\Result\Page $resultPage */
        $resultPage = $this->resultPageFactory->create();
        $resultPage->setActiveMenu('Pipelines_PipeManager::pipelines');
        $resultPage->getConfig()->getTitle()->prepend(__('Pipeline Runs'));
        return $resultPage;
    }
}
