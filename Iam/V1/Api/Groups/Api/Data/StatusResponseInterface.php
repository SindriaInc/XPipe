<?php
namespace Iam\Groups\Api\Data;

interface StatusResponseInterface
{
    /**
     * Get HTTP status code
     *
     * @return int
     */
    public function getCode(): int;

    /**
     * Set HTTP status code
     *
     * @param int $code
     * @return void
     */
    public function setCode(int $code): void;

    /**
     * Get success flag
     *
     * @return bool
     */
    public function getSuccess(): bool;

    /**
     * Set success flag
     *
     * @param bool $success
     * @return void
     */
    public function setSuccess(bool $success): void;

    /**
     * Get response message
     *
     * @return string
     */
    public function getMessage(): string;

    /**
     * Set response message
     *
     * @param string $message
     * @return void
     */
    public function setMessage(string $message): void;

    /**
     * Get optional data
     *
     * @return array
     */
    public function getData(): array;

    /**
     * Set optional data
     *
     * @param array $data
     * @return void
     */
    public function setData(array $data): void;
}
