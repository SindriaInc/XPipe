<?php

namespace Sindria\Copyright\Plugin;

class CopyrightPlugin
{
    public function afterGetCopyright(\Magento\Theme\Block\Html\Footer $subject, $result)
    {
        return __('Copyright © 2016-present Sindria, Inc. All rights reserved.');
    }
}
