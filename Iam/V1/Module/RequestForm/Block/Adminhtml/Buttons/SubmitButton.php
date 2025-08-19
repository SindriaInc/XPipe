<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Iam\RequestForm\Block\Adminhtml\Buttons;

use Magento\Framework\View\Element\UiComponent\Control\ButtonProviderInterface;


class SubmitButton extends GenericButton implements ButtonProviderInterface
{

    public function getButtonData(): array
    {
        return [
            'label' => __('Submit'),
            'class' => 'save primary',
            'data_attribute' => [
                'mage-init' => [
                    'buttonAdapter' => [
                        'actions' => [
                            [
                                'targetName' => 'requestform_form.requestform_form',
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
