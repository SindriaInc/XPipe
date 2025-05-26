<?php
/**
 * Copyright © Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipelines\PipeManager\Block\Adminhtml\Pipeline\Run;
/**
 * @api
 * @since 100.0.2
 */
class Navbar extends \Magento\Backend\Block\Template
{
    /**
     * Block constructor
     *
     * @return void
     */
//    protected function _construct()
//    {
//
//        $this->_blockGroup = 'Pipelines_PipeManager';
//        $this->_controller = 'pipeline_run/show';
//
//        //        $this->_headerText = __('Custom Variables');
//        parent::_construct();
//        $this->buttonList->update('add', 'label', __('Add New Variable'));
////        dd($this);
//    }

    public function getBackUrl(): string
    {
//        $back = $this->getRequest()->getParam('back');
//
//        if ($back) {
//            return $back;
//        }

        // Fallback
        return $this->getUrl('pipemanager/pipeline/index');
    }
}
