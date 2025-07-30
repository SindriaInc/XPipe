<?php
/**
 * Copyright © Sindria, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
namespace Pipelines\Dedicated\Block\Adminhtml\Pipeline\Run;

use Magento\Backend\Block\Widget\Container;

/**
 * @api
 * @since 100.0.2
 */
class Navbar extends Container
{
    //protected $_headerText = 'Run Logs';

    /**
     * Navbar mage constructor
     *
     * @return void
     */
    protected function _construct()
    {
        parent::_construct();

        $this->buttonList->add(
            'back',
            [
                'label'   => __('Back'),
                'class'   => 'back',
                'onclick' => sprintf(
                    "setLocation('%s');",
                    $this->getBackUrl()
                )
            ],
            -1 // priorità (prima di altri)
        );
    }

    /**
     * URL di destinazione quando si clicca “Back”.
     * Puoi leggere ?back=... dalla request o
     * puntare a una route statica.
     */
    public function getBackUrl(): string
    {
        // 1. Priorità assoluta: parametro ?back= ...
        if ($target = $this->getRequest()->getParam('back')) {
            return $target;
        }

        // 2. In assenza di ?back=..., usa il Referer inviato dal browser
        if ($referer = (string) $this->getRequest()->getServer('HTTP_REFERER')) {
            // consenti solo URL interni alla tua installazione backend
            $baseUrl = $this->getBaseUrl();
            if (strpos($referer, $baseUrl) === 0) {
                return $referer; // include tutti i parametri
            }
        }

        // 3. Fallback statico (safe)
        return $this->getUrl('dedicated/index');
    }
}
