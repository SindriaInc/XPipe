<?php

namespace Core\Github\Model;

class Variable
{
    private string $name;
    private string $value;

    /**
     * Variable constructor.
     *
     * @param string $name
     * @param string $value
     */
    public function __construct(string $name, string $value)
    {
        $this->name = $name;
        $this->value = $value;
    }

    /**
     * Get the name.
     *
     * @return string
     */
    public function getName(): string
    {
        return $this->name;
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
            'name' => $this->name,
            'value' => $this->value,
        ]);
    }
}
