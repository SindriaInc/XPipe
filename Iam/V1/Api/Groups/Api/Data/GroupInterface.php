<?php

namespace Iam\Groups\Api\Data;

interface GroupInterface
{
    public function getGroupId();

    public function setGroupId($groupId);

    public function getSlug();
    public function setSlug($slug);

    public function getLabel();
    public function setLabel($label);

    public function getShort();
    public function setShort($short);

    public function getCreatedAt();
    public function setCreatedAt($createdAt);

    public function getUpdatedAt();
    public function setUpdatedAt($updatedAt);

}