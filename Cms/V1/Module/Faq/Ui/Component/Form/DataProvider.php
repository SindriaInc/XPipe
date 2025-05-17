<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Cms\Faq\Ui\Component\Form;

use Cms\Faq\Model\ResourceModel\Faq\CollectionFactory;
use Cms\Faq\Model\ResourceModel\Faq\Collection;
use Magento\Framework\App\Request\DataPersistorInterface;
use Magento\Ui\DataProvider\Modifier\PoolInterface;

/**
 * Class DataProvider
 */
class DataProvider extends \Magento\Ui\DataProvider\ModifierPoolDataProvider
{
    /**
     * @var Collection
     */
    protected $collection;

    /**
     * @var DataPersistorInterface
     */
    protected $dataPersistor;

    /**
     * @var array
     */
    protected $loadedData;

    private \Magento\Framework\App\RequestInterface $request;

    protected $urlBuilder;

    /**
     * Constructor
     *
     * @param string $name
     * @param string $primaryFieldName
     * @param string $requestFieldName
     * @param CollectionFactory $blockCollectionFactory
     * @param DataPersistorInterface $dataPersistor
     * @param array $meta
     * @param array $data
     * @param PoolInterface|null $pool
     *
     */
    public function __construct(
        $name,
        $primaryFieldName,
        $requestFieldName,
        CollectionFactory $blockCollectionFactory,
        DataPersistorInterface $dataPersistor,
        array $meta = [],
        array $data = [],
        PoolInterface $pool = null,
        \Magento\Framework\App\RequestInterface $request,
         \Magento\Framework\UrlInterface $urlBuilder
    ) {
        $this->collection = $blockCollectionFactory->create();
        $this->dataPersistor = $dataPersistor;
        $this->request = $request;
        $this->urlBuilder = $urlBuilder;

        parent::__construct($name, $primaryFieldName, $requestFieldName, $meta, $data, $pool);
    }

    /**
     * Get data
     *
     * @return array
     */
    public function getData()
    {
        if (isset($this->loadedData)) {
            return $this->loadedData;
        }
        $items = $this->collection->getItems();
        /** @var \Cms\Faq\Model\Faq $faq */
        foreach ($items as $faq) {
            $this->loadedData[$faq->getId()] = ['faq' => $faq->getData()];
        }

//        $data = $this->dataPersistor->get('cms_faq');
        if (!empty($data)) {
            $faq = $this->collection->getNewEmptyItem();
            $faq->setData($data);
            $this->loadedData[$faq->getId()] = $faq->getData();
//            $this->dataPersistor->clear('cms_faq');
        }

        return $this->loadedData;
    }

    public function getConfigData()
    {
        $configData = parent::getConfigData();

        $id = (int) $this->request->getParam('faq_id');
        $submitUrl = $id
            ? $this->urlBuilder->getUrl('faq/index/edit', ['faq_id' => $id])
            : $this->urlBuilder->getUrl('faq/index/save');

        $configData['submit_url'] = $submitUrl;

        return $configData;
    }


}
