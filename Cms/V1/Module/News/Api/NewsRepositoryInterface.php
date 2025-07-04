<?php

namespace Cms\News\Api;

use Cms\News\Api\Data\NewsInterface;

interface NewsRepositoryInterface
{
    public function save(NewsInterface $news) : void;

    public function delete(NewsInterface $news) : void;

    public function getNewsById(int $id) : NewsInterface;
}