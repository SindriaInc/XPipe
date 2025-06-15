<?php

namespace Core\Github\Model;

class Workflow
{
    private string $ref;

    /** @var array<string, string> */
    private array $inputs;

    /**
     * Workflow constructor.
     *
     * @param string $ref
     * @param array<string, string> $inputs
     */
    public function __construct(string $ref, array $inputs)
    {
        $this->ref = $ref;
        $this->inputs = $inputs;
    }

    /**
     * Get the ref.
     *
     * @return string
     */
    public function getRef(): string
    {
        return $this->ref;
    }

    /**
     * Get the inputs.
     *
     * @return array<string, string>
     */
    public function getInputs(): array
    {
        return $this->inputs;
    }

    /**
     * Serialize the Workflow object to JSON.
     *
     * @return string
     */
    public function serialize(): string
    {
        return json_encode([
            'ref' => $this->ref,
            'inputs' => $this->inputs,
        ]);
    }
}
