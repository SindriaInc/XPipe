<?php

namespace Sindria\Iam\InfoTable;

use Faker\Provider\Base;
use Sindria\Toolkit\InfoTable\InfoTable;
use Sindria\Toolkit\BaseHelper;

class PolicyInfoTable extends InfoTable
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
                'label' => trans('iam.policies.field.id'),
                'value' => BaseHelper::encodeSequence($this->entry->id),
            ],
            [
                'label' => trans('iam.policies.field.name'),
                'value' => $this->entry->name,
            ],
            [
                'label' => trans('iam.policies.field.content'),
                'value' => BaseHelper::jsonBeatufy($this->entry->content),
            ],
            [
                'label' => trans('iam.policies.field.type'),
                'value' => $this->entry->type->name,
            ],
        ];
    }


}
