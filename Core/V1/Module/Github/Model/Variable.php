<?php

namespace Core\Github\Model;

class Variable
{
    private string $key;
    private string $value;

    /**
     * Variable constructor.
     *
     * @param string $key
     * @param string $value
     */
    public function __construct(string $key, string $value)
    {
        $this->key = $key;
        $this->value = $value;
    }

    /**
     * Get the name.
     *
     * @return string
     */
    public function getKey(): string
    {
        return $this->key;
    }

    /**
     * Get the value.
     *
     * @return string
     */
    public function getValue(): string
    {
        return $this->value;
    }

    /**
     * Serialize the Variable object to JSON.
     *
     * @return string
     */
    public function serialize(): string
    {
        return json_encode([
            'name' => $this->key,
            'value' => $this->value,
        ]);
    }
}
