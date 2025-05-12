<?php

namespace Sindria\News\Api;

use Sindria\News\Api\Data\NewsInterface;

interface NewsRepositoryInterface
{
    public function save(NewsInterface $news) : void;

    public function delete(NewsInterface $news) : void;

    public function getNewsById(int $id) : NewsInterface;
}