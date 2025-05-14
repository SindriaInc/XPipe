<?php
namespace Academy\SampleApi\Plugin\Backend\Model\Menu;

class Config
{
    public function afterGetMenu(\Magento\Backend\Model\Menu\Config $subject, \Magento\Backend\Model\Menu $menu)
    {
        $menuItem = $menu->get('Academy_SampleApi::sampleapi');
        if ($menuItem) {
            $menuItem->set('class', 'admin__menu-icon _icon-document');
        }
        return $menu;
    }
}