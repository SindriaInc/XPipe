<?php

namespace Sindria\Iam\InfoTable;

use Sindria\Toolkit\InfoTable\InfoTable;
use Sindria\Iam\Helper;

class UserInfoTable extends InfoTable
{

    /**
     * Define info table fields
     *
     * @override
     * @return array
     */
    protected function fields()
    {
        return [
            [
                'label' => trans('iam.users.field.id'),
                'value' => $this->entry->user->id,
            ],
            [
                'label' => trans('iam.users.field.username'),
                'value' => $this->entry->user->username,
            ],
            [
                'label' => trans('iam.users.field.name'),
                'value' => $this->entry->user->firstName,
            ],
            [
                'label' => trans('iam.users.field.surname'),
                'value' => $this->entry->user->lastName,
            ],
            [
                'label' => trans('iam.users.field.email'),
                'value' => $this->entry->user->email,
            ],
            [
                'label' => trans('iam.users.field.email_verified'),
                'value' => Helper::hasEmailVerified($this->entry->user->emailVerified),
            ],
            [
                'label' => trans('iam.users.field.job_title'),
                'value' => $this->entry->meta->jobTitle,
            ],
        ];
    }


}
