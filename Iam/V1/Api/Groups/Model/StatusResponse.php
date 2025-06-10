<?php
namespace Iam\Groups\Model;

use Iam\Groups\Api\Data\StatusResponseInterface;

class StatusResponse implements StatusResponseInterface
{
    /**
     * @var int
     */
    private int $code;

    /**
     * @var bool
     */
    private bool $success;

    /**
     * @var string
     */
    private string $message;

    /**
     * @var array
     */
    private array $data = [];

    /**
     * Constructor
     *
     * @param int $code
     * @param bool $success
     * @param string $message
     * @param array $data
     */
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

    /**
     * @inheritdoc
     */
    public function getCode(): int
    {
        return $this->code;
    }

    /**
     * @inheritdoc
     */
    public function setCode(int $code): void
    {
        $this->code = $code;
    }

    /**
     * @inheritdoc
     */
    public function getSuccess(): bool
    {
        return $this->success;
    }

    /**
     * @inheritdoc
     */
    public function setSuccess(bool $success): void
    {
        $this->success = $success;
    }

    /**
     * @inheritdoc
     */
    public function getMessage(): string
    {
        return $this->message;
    }

    /**
     * @inheritdoc
     */
    public function setMessage(string $message): void
    {
        $this->message = $message;
    }

    /**
     * @inheritdoc
     */
    public function getData(): array
    {
        return $this->data;
    }

    /**
     * @inheritdoc
     */
    public function setData(array $data): void
    {
        $this->data = $data;
    }
}
