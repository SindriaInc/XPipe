<?php

namespace Sindria\Faq\Api\Data;
interface FaqInterface
{

    public function getFaqId() : int;
    public function setFaqId(int $id);

    public function getQuestion() : string;

    public function setQuestion(string $question);

    public function getAnswer() : string;

    public function setAnswer(string $answer);

    public function getCreatedAt() : string;

    public function setCreatedAt(string $createdAt);

    public function getUpdatedAt() : string;

    public function setUpdatedAt(string $updatedAt);

    public function getStatus() : int;

    public function setStatus(int $status);

}
