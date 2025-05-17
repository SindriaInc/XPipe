<?php

namespace Cms\Faq\Ui\Source\Listing\Column;

use Magento\Framework\Data\OptionSourceInterface;

class Status implements OptionSourceInterface
{

    private const ENABLED = 1;
    private const DISABLED = 0;

    public function toOptionArray() : array
    {
        return [
            ['value' => self::ENABLED, 'label' => __('Enabled')],
            ['value' => self::DISABLED, 'label' => __('Disabled')]
        ];
    }
}