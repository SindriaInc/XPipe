<?php

namespace Cms\News\Api;

use Cms\News\Api\Data\NewsInterface;

interface NewsManagerInterface
{
    public function getNews() : NewsInterface;
}