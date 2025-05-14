<?php

namespace Core\News\Api\Data;

interface NewsInterface
{

    public function getNewsId() : int;
    public function setNewsId(int $id);

    public function getTitle() : string;

    public function setTitle(string $title);

    public function getContent() : string;

    public function setContent(string $content);

    public function getCreatedAt() : string;

    public function setCreatedAt(string $createdAt);

    public function getUpdatedAt() : string;

    public function setUpdatedAt(string $updatedAt);

    public function isActive() : int;

    public function setIsActive(int $active);

}
