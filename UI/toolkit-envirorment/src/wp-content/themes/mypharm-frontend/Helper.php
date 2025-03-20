<?php


use Sindria\Toolkit\BaseHelper;
class Helper
{

    public static function buildManagePoliciesAction() : string
    {
        $newActionUrl = cms_dashboard_page_route('policies');
        $newActionText = \Sindria\Iam\trans('iam.users.actions.manage_policies');
        return ( BaseHelper::hasCapability('read_policies') ? '<a href="'.$newActionUrl.'" class="page-title-action">'.$newActionText.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$newActionText.'</a>' );
    }

    public static function buildAttachPolicyAction() : string
    {
        $newActionUrl = cms_dashboard_page_route('attach-policy');
        $newActionText = \Sindria\Iam\trans('iam.policies.actions.attach');
        return ( BaseHelper::hasCapability('admin_policies') ? '<a href="'.$newActionUrl.'" class="page-title-action">'.$newActionText.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$newActionText.'</a>' );
    }


    public static function buildDetachPolicyAction() : string
    {
        $newActionUrl = cms_dashboard_page_route('detach-policy');
        $newActionText = \Sindria\Iam\trans('iam.policies.actions.detach');
        return ( BaseHelper::hasCapability('admin_policies') ? '<a href="'.$newActionUrl.'" class="page-title-action">'.$newActionText.'</a>' : '<a href="#" style="cursor: not-allowed;" class="page-title-action button-disabled">'.$newActionText.'</a>' );
    }


    public static function hasEmailVerified($emailVerified) : string
    {
        return $emailVerified ? \Sindria\Iam\trans('iam.users.field.email_verified.true') : \Sindria\Iam\trans('iam.users.field.email_verified.false');
    }


    public static function getUserInfoBack() : string
    {
        return cms_dashboard_page_route('iam');
    }

    public static function getUsersAddFormAction() : string
    {
//        return cms_dashboard_page_route('store-user');
        return "#";
    }

    public static function getUsersAddFormMethod() : string
    {
        return 'post';
    }

    public static function getUsersCancelForm() : string
    {
        return "#";
    }

    public static function getUsersShowFormAction() : string
    {
        return "#";
    }

    public static function getUsersShowFormMethod() : string
    {
        return 'post';
    }

    public static function getPolicyInfoBack() : string
    {
        return "#";
    }

    public static function getPoliciesAddFormAction() : string
    {
        return "#";
    }

    public static function getPoliciesAddFormMethod() : string
    {
        return 'post';
    }

    public static function getPoliciesCancelForm() : string
    {
        return "#";
    }

    public static function getPoliciesAttachFormAction() : string
    {
        return "#";
    }

    public static function getPoliciesAttachFormMethod() : string
    {
        return 'post';
    }


    public static function getPoliciesDetachFormAction() : string
    {
        return "#";
    }

    public static function getPoliciesDetachFormMethod() : string
    {
        return 'post';
    }


}
