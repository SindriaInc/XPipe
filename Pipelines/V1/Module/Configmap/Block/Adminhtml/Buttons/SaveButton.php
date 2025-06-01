<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipelines\Configmap\Block\Adminhtml\Buttons;

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
                                'targetName' => 'configmap_form.configmap_form',
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
