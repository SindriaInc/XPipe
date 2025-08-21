<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Support\TicketForm\Model\Form\Source;

use Magento\Framework\Data\OptionSourceInterface;


/**
 * Class Theme
 */
class TicketTypes implements OptionSourceInterface
{
    private $form;

    public function __construct()
    {
        $this->form = \Support\TicketForm\Model\TicketForm::getInstance();
    }

    /**
     * Get options
     *
     * @return array
     */
    public function toOptionArray(): array
    {
        $options[] = ['label' => 'Default', 'value' => ''];
        return array_merge($options, $this->form->getTicketTypes());
    }
}
