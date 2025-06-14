<?php
namespace Core\StoreViewItalian\Helper;

class StoreViewItalianHelper
{
    public static function getCoreStoreViewItalianToggle() : int
    {
        return (int)SystemEnvHelper::get('CORE_STOREVIEW_ITALIAN_TOGGLE');
    }
}
