<?php
namespace Sindria\ThemeSetup\Setup\Patch\Data;

use Magento\Framework\Setup\Patch\DataPatchInterface;
use Magento\Theme\Model\ResourceModel\Theme\Collection;
use Magento\Framework\App\Config\ConfigResource\ConfigInterface;

class SetTheme implements DataPatchInterface
{
    protected $themeCollection;
    protected $configResource;

    public function __construct(
        Collection $themeCollection,
        ConfigInterface $configResource
    ) {
        $this->themeCollection = $themeCollection;
        $this->configResource = $configResource;
    }

    public function apply()
    {
        $theme = $this->themeCollection->getThemeByFullPath('frontend/Sindria/sindria');

        if ($theme && $theme->getId()) {
            $this->configResource->saveConfig(
                'design/theme/theme_id',
                $theme->getId(),
                'default',
                0
            );
        }
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
