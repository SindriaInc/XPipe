<?php

namespace Iam\UsersMeta\Model;

use Iam\UsersMeta\Api\Data\UserMetaInterface;
use Magento\Framework\Model\AbstractModel;

class UserMeta extends AbstractModel implements UserMetaInterface
{
    private const USER_META_ID = 'user_meta_id';
    private const USERNAME = 'username';
    private const JOB_TITLE = 'job_title';
    private const SENIORITY = 'seniority';
    private const LOCATION = 'location';
    private const WORK_MODE = 'work_mode';
    private const CREATED_AT = 'created_at';
    private const UPDATED_AT = 'updated_at';

    protected function _construct()
    {
        $this->_eventPrefix = 'iam_users_meta';
        $this->_eventObject = 'userMeta';
        $this->_idFieldName = self::USER_META_ID;
        $this->_init(\Iam\UsersMeta\Model\ResourceModel\UserMeta::class);
    }


    public function getUserMetaId()
    {
        return (int)$this->getData(self::USER_META_ID);
    }

    public function setUserMetaId($userMetaId)
    {
        $this->setData(self::USER_META_ID, $userMetaId);
    }

    public function getUsername()
    {
        return $this->getData(self::USERNAME);
    }

    public function setUsername($username)
    {
        $this->setData(self::USERNAME, $username);
    }

    public function getJobTitle()
    {
        return $this->getData(self::JOB_TITLE);
    }

    public function setJobTitle($jobTitle)
    {
        $this->setData(self::JOB_TITLE, $jobTitle);
    }

    public function getSeniority()
    {
        return $this->getData(self::SENIORITY);
    }

    public function setSeniority($seniority)
    {
        $this->setData(self::SENIORITY, $seniority);
    }

    public function getLocation()
    {
        return $this->getData(self::LOCATION);
    }

    public function setLocation($location)
    {
        $this->setData(self::LOCATION, $location);
    }

    public function getWorkMode()
    {
        return $this->getData(self::WORK_MODE);
    }

    public function setWorkMode($workMode)
    {
        $this->setData(self::WORK_MODE, $workMode);
    }

    public function getCreatedAt()
    {
        return $this->getData(self::CREATED_AT);
    }

    public function setCreatedAt($createdAt)
    {
        $this->setData(self::CREATED_AT, $createdAt);
    }

    public function getUpdatedAt()
    {
       return $this->getData(self::UPDATED_AT);
    }

    public function setUpdatedAt($updatedAt)
    {
        $this->setData(self::UPDATED_AT, $updatedAt);
    }
}
