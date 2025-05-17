<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Cms\Faq\Block\Adminhtml\Faq\Edit;

use Magento\Framework\View\Element\UiComponent\Control\ButtonProviderInterface;


class SaveButton extends GenericButton implements ButtonProviderInterface
{

    public function getButtonData(): array
    {
        return [
            'label' => __('Save'),
            'class' => 'save primary',
            'data_attribute' => [
                'mage-init' => [
                    'buttonAdapter' => [
                        'actions' => [
                            [
                                'targetName' => 'cms_faq_form.cms_faq_form',
                                'actionName' => 'save'
                            ]
                        ]
                    ]
                ]
            ],
//            'class_name' => Container::SPLIT_BUTTON,
//            'options' => $this->getOptions(),
        ];
    }

}
