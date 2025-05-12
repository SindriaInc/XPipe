<?php

namespace Sindria\Faq\Api;

use Sindria\Faq\Api\Data\FaqInterface;

interface FaqRepositoryInterface
{
    public function save(FaqInterface $faq) : void;

    public function delete(FaqInterface $faq) : void;

    public function getFaqById(int $id) : FaqInterface;
}