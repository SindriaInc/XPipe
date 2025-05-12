<?php
namespace Sindria\WelcomeMessage\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Framework\App\Config\Storage\WriterInterface;

class UpdateWelcomeMessage implements DataPatchInterface
{
    private $configWriter;

    public function __construct(WriterInterface $configWriter)
    {
        $this->configWriter = $configWriter;
    }

    public function apply()
    {
        $this->configWriter->save(
            'design/header/welcome', 
            ''
        );
    }

    public static function getDependencies()
    {
        return [];
    }

    public function getAliases()
    {
        return [];
    }
}
