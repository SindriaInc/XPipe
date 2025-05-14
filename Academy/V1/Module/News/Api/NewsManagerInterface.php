<?php

namespace Academy\News\Api;

use Academy\News\Api\Data\NewsInterface;

interface NewsManagerInterface
{
    public function getNews() : NewsInterface;
}