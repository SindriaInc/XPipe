<?php

namespace Iam\UsersMeta\Api\Data;

interface UserMetaInterface
{
    public function getUserMetaId();

    public function setUserMetaId($userMetaId);

    public function getUsername();
    public function setUsername($username);

    public function getJobTitle();
    public function setJobTitle($jobTitle);

    public function getSeniority();
    public function setSeniority($seniority);

    public function getLocation();
    public function setLocation($location);

    public function getWorkMode();
    public function setWorkMode($workMode);

    public function getCreatedAt();
    public function setCreatedAt($createdAt);

    public function getUpdatedAt();
    public function setUpdatedAt($updatedAt);

}