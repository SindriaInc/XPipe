<?php

namespace Pipe\Cart\Controller\Items;

use Magento\Framework\App\Action\Context;
use Magento\Checkout\Model\Session;
use Magento\Checkout\Model\Cart as CustomerCart;

class Remove extends \Magento\Framework\App\Action\Action
{
    /**
     * @var \Magento\Checkout\Model\Session
     */
    protected $checkoutSession;

    /**
     * @var \Magento\Checkout\Model\Cart
     */
    protected $cart;

    /**
     * @param Context $context
     * @param \Magento\Checkout\Model\Session $checkoutSession
     * @param CustomerCart $cart
     */
    public function __construct(Context $context, Session $checkoutSession, CustomerCart $cart)
    {
        $this->checkoutSession = $checkoutSession;
        $this->cart = $cart;

        parent::__construct($context);
    }

    /**
     * @return \Magento\Framework\App\ResponseInterface|\Magento\Framework\Controller\ResultInterface|void
     * @throws \Magento\Framework\Exception\LocalizedException
     * @throws \Magento\Framework\Exception\NoSuchEntityException
     */
    public function execute()
    {
        $allItems = $this->checkoutSession->getQuote()->getAllVisibleItems();
        foreach ($allItems as $item) {
            $itemId = $item->getItemId();
            $this->cart->removeItem($itemId)->save();
        }

        $message = __(
            'You deleted all item from shopping cart.'
        );
        $this->messageManager->addSuccessMessage($message);

        $response = [
            'success' => true,
        ];

        $this->getResponse()->representJson(
            $this->_objectManager->get('Magento\Framework\Json\Helper\Data')->jsonEncode($response)
        );
    }
}
