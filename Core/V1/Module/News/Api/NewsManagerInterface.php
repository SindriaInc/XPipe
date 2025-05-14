<?php

namespace Core\News\Api;

use Sindria\News\Api\Data\NewsInterface;

interface NewsManagerInterface
{
    public function getNews() : NewsInterface;
}