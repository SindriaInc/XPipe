<?php

namespace Cms\Faq\Api;

use Cms\Faq\Api\Data\FaqInterface;

interface FaqRepositoryInterface
{
    public function save(FaqInterface $faq) : void;

    public function delete(FaqInterface $faq) : void;

    public function getFaqById(int $id) : FaqInterface;
}