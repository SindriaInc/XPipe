<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipelines\Configmap\Block\Adminhtml\Buttons;

use Magento\Framework\View\Element\UiComponent\Control\ButtonProviderInterface;

/**
 * Class BackButton
 */
class DeleteButton extends GenericButton implements ButtonProviderInterface
{
    public function getButtonData()
    {
        return [
            'label' => __('Delete'),
            'on_click' => 'deleteConfirm(\'' . __(
                    'Are you sure you want to delete selected configmap?'
                ) . '\', \'' . $this->getDeleteUrl() . '\')',
            'class' => 'delete',
            'sort_order' => 20
        ];
    }


    /**
     * Get URL for back (reset) button
     *
     * @return string
     */
    public function getDeleteUrl()
    {
        return $this->getUrl('configmap/index/delete');
    }
}
