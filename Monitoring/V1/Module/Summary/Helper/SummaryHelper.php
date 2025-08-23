<?php
namespace Monitoring\Summary\Helper;

use Core\SystemEnv\Facade\SystemEnvFacade;

class SummaryHelper
{
    public static function getMonitoringSummaryDashboardUrl(): string
    {
        return SystemEnvFacade::get('MONITORING_SUMMARY_DASHBOARD_URL');
    }
}
