<?php

namespace Sindria\Iam;

use Sindria\Toolkit\BaseHelper;
class Helper
{

    public static function buildManagePoliciesAction() : string
    {
        $newActionUrl = cms_dashboard_page_route('policies');
        $newActionText = trans('iam.users.actions.manage_policies');
        return ( BaseHelper::hasCapability('read_policies') ? '<a href="'.$newActionUrl.'" class="page-title-action">'.$newActionText.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$newActionText.'</a>' );
    }

    public static function buildAttachPolicyAction() : string
    {
        $newActionUrl = cms_dashboard_page_route('attach-policy');
        $newActionText = trans('iam.policies.actions.attach');
        return ( BaseHelper::hasCapability('admin_policies') ? '<a href="'.$newActionUrl.'" class="page-title-action">'.$newActionText.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$newActionText.'</a>' );
    }


    public static function buildDetachPolicyAction() : string
    {
        $newActionUrl = cms_dashboard_page_route('detach-policy');
        $newActionText = trans('iam.policies.actions.detach');
        return ( BaseHelper::hasCapability('admin_policies') ? '<a href="'.$newActionUrl.'" class="page-title-action">'.$newActionText.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$newActionText.'</a>' );
    }


    public static function hasEmailVerified($emailVerified) : string
    {
        return $emailVerified ? trans('iam.users.field.email_verified.true') : trans('iam.users.field.email_verified.false');
    }


    public static function getUserInfoBack() : string
    {
        return cms_dashboard_page_route('iam');
    }

    public static function getUsersAddFormAction() : string
    {
        return cms_dashboard_page_route('store-user');
    }

    public static function getUsersAddFormMethod() : string
    {
        return 'post';
    }

    public static function getUsersCancelForm() : string
    {
        return cms_dashboard_page_route('iam');
    }

    public static function getUsersShowFormAction() : string
    {
        return cms_dashboard_page_route('edit-user');
    }

    public static function getUsersShowFormMethod() : string
    {
        return 'post';
    }

    public static function getPolicyInfoBack() : string
    {
        return cms_dashboard_page_route('policies');
    }

    public static function getPoliciesAddFormAction() : string
    {
        return cms_dashboard_page_route('store-policy');
    }

    public static function getPoliciesAddFormMethod() : string
    {
        return 'post';
    }

    public static function getPoliciesCancelForm() : string
    {
        return cms_dashboard_page_route('policies');
    }

    public static function getPoliciesAttachFormAction() : string
    {
        return cms_dashboard_page_route('attach-store');
    }

    public static function getPoliciesAttachFormMethod() : string
    {
        return 'post';
    }


    public static function getPoliciesDetachFormAction() : string
    {
        return cms_dashboard_page_route('detach-store');
    }

    public static function getPoliciesDetachFormMethod() : string
    {
        return 'post';
    }


}
