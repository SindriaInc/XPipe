<?php

namespace Core\News\ViewModel;

use Magento\Framework\View\Element\Block\ArgumentInterface;
use Sindria\News\Api\NewsManagerInterface;


class NewsViewModel implements ArgumentInterface
{
    private NewsManagerInterface $newsManager;

    public function __construct(NewsManagerInterface $newsManager)
    {
        $this->newsManager = $newsManager;
    }

    public function getNews()
    {
        return $this->newsManager->getNews();
    }

}