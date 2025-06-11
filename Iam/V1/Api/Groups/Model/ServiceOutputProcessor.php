<?php
/**
 * Copyright © 2025 Sindria Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Iam\Groups\Model;


/**
 * Data object converter for REST
 */
class ServiceOutputProcessor  extends \Magento\Framework\Webapi\ServiceOutputProcessor
{

    public function process($data, $serviceClassName, $serviceMethodName)
    {

        /** @var string $dataType */
        $dataType = $this->methodsMapProcessor->getMethodReturnType($serviceClassName, $serviceMethodName);

        if ($dataType == 'array') {
            return $data;
        } else if ($dataType == 'StatusResponseInterface') {
            return $data;
        }
        else {
            return $this->convertValue($data, $dataType);
        }
    }


}
