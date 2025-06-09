<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Core\ProductVersion\Block\Page;

use Core\ProductVersion\Helper\SystemEnvHelper;

/**
 * Adminhtml footer block
 *
 * @override
 * @api
 * @author      Luca Pitzoi <luca.pitzoi@sindria.org>
 * @since 100.0.2
 */
class Footer extends \Magento\Backend\Block\Template
{
    /**
     * @var string
     */
    protected $_template = 'Core_ProductVersion::page/footer.phtml';

    /**
     * @var \Magento\Framework\App\ProductMetadataInterface
     * @since 100.1.0
     */
    protected $productMetadata;

    /**
     * @param \Magento\Backend\Block\Template\Context $context
     * @param \Magento\Framework\App\ProductMetadataInterface $productMetadata
     * @param array $data
     */
    public function __construct(
        \Magento\Backend\Block\Template\Context $context,
        \Magento\Framework\App\ProductMetadataInterface $productMetadata,
        array $data = []
    ) {
        $this->productMetadata = $productMetadata;
        parent::__construct($context, $data);
    }

    /**
     * @inheritdoc
     */
    protected function _construct()
    {
        $this->setShowProfiler(true);
    }

    /**
     * Get product name
     *
     * @return string
     * @since 100.1.0
     */
    public function getProductName() : string
    {
        //return $this->productMetadata->getName();
        return SystemEnvHelper::get('XPIPE_CORE_PRODUCT_NAME', "XPipe");
    }

    /**
     * Get product version
     *
     * @return string
     * @since 100.1.0
     */
    public function getProductVersion() : string
    {
        //return $this->productMetadata->getVersion();
        return SystemEnvHelper::get('XPIPE_CORE_PRODUCT_VERSION', "0.1.0");
    }

    /**
     * @inheritdoc
     * @since 101.0.0
     */
    protected function getCacheLifetime()
    {
        return 3600 * 24 * 10;
    }
}
