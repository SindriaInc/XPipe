<?php
namespace Core\MicroFramework\Api\Data;

interface StatusResponseInterface
{
    public function getCode(): int;
    public function setCode(int $code): void;
    public function getSuccess(): bool;
    public function setSuccess(bool $success): void;
    public function getMessage(): string;
    public function setMessage(string $message): void;
    public function getData(): array;
    public function setData(array $data): void;
}
