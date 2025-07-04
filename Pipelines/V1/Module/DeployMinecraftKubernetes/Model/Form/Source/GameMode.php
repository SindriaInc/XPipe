<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipelines\DeployMinecraftKubernetes\Model\Form\Source;

use Magento\Framework\Data\OptionSourceInterface;


/**
 * Class Theme
 */
class GameMode implements OptionSourceInterface
{
    private $form;

    public function __construct()
    {
        $this->form = \Pipelines\DeployMinecraftKubernetes\Model\DeployMinecraftKubernetes::getInstance();
    }

    /**
     * Get options
     *
     * @return array
     */
    public function toOptionArray()
    {
        $options[] = ['label' => 'Default', 'value' => ''];
        return array_merge($options, $this->form->getGameMode());
    }
}
