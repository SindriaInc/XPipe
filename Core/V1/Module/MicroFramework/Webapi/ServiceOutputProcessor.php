<?php
namespace Core\MicroFramework\Webapi;

class ServiceOutputProcessor extends \Magento\Framework\Webapi\ServiceOutputProcessor
{
    public function process($data, $serviceClassName, $serviceMethodName)
    {
        $dataType = $this->methodsMapProcessor->getMethodReturnType($serviceClassName, $serviceMethodName);

        if ($dataType == 'array' || $dataType == 'StatusResponseInterface') {
            return $data;
        }
        return $this->convertValue($data, $dataType);
    }
}
