<?php

namespace Sindria\SampleApi\Api\Data;

interface SampleApiInterface
{

    public function getApiId() : int;
    public function setApiId(int $id);

    public function getName() : string;

    public function setName(string $name);

    public function getApiData() : string;

    public function setApiData(string $data);
}
