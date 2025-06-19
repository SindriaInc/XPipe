<?php
namespace Core\MicroFramework\Model;

use Core\MicroFramework\Api\Data\StatusResponseInterface;

class StatusResponse implements StatusResponseInterface, \JsonSerializable
{
    private int $code;
    private bool $success;
    private string $message;
    private array $data;

    public function __construct(
        int $code = 200,
        bool $success = true,
        string $message = 'ok',
        array $data = []
    ) {
        $this->code = $code;
        $this->success = $success;
        $this->message = $message;
        $this->data = $data;
    }

    public function getCode(): int
    {
        return $this->code;
    }

    public function setCode(int $code): void
    {
        $this->code = $code;
    }

    public function getSuccess(): bool
    {
        return $this->success;
    }

    public function setSuccess(bool $success): void
    {
        $this->success = $success;
    }

    public function getMessage(): string
    {
        return $this->message;
    }

    public function setMessage(string $message): void
    {
        $this->message = $message;
    }

    public function getData(): array
    {
        return $this->data;
    }

    public function setData(array $data): void
    {
        $this->data = $data;
    }

    public function jsonSerialize(): array
    {
        return [
            'code' => $this->getCode(),
            'success' => $this->getSuccess(),
            'message' => $this->getMessage(),
            'data' => $this->data
        ];
    }
}
