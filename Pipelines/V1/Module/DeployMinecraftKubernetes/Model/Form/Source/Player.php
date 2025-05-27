<?php
/**
 * Copyright © Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipelines\DeployMinecraftKubernetes\Model\Form\Source;

use Magento\Framework\Data\OptionSourceInterface;
use Magento\Framework\View\Design\Theme\Label\ListInterface;

/**
 * Class Theme
 */
class Player implements OptionSourceInterface
{
    /**
     * @var \Magento\Framework\View\Design\Theme\Label\ListInterface
     */
    protected $playerList;

    /**
     * Constructor
     *
     * @param ListInterface $playerList
     */
    public function __construct(ListInterface $playerList)
    {
        $this->playerList = $playerList;
    }

    /**
     * Get options
     *
     * @return array
     */
    public function toOptionArray()
    {
        $options[] = ['label' => 'Default', 'value' => ''];
        return array_merge($options, $this->playerList->getLabels());
    }
}
