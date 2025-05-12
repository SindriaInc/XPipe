<?php

namespace Sindria\Iam\DataTable;

use Sindria\Toolkit\Datatable\DataTable;
use Sindria\Toolkit\BaseHelper;
use Sindria\Iam\Helper;
use Sindria\Iam\Modal\WarningModal;

class UserPoliciesDataTable extends DataTable
{

    /**
     * Override the parent columns method. Defines the columns to use in your listing table
     *
     * @override
     * @return array
     */
    public function get_columns()
    {
        $columns = array(
            'name'        => trans('iam.users.policies.field.name'),
            'activities'  => trans('global.text.activities'),
        );

        return $columns;
    }

    /**
     * Define the sortable columns
     *
     * @override
     * @return array
     */
    public function get_sortable_columns()
    {
        return array(
            'name' => array('name', false),
        );
    }


    /**
     * Build table data with activities and modals
     *
     * @override
     * @return array
     */
    protected function tableData() : array
    {
        $data = [];

        foreach ($this->collection as $item) {

            $activities = $this->buildActivities($item);
            //$modal = new WarningModal('users-delete-modal-' . $item->id, 'users-delete-' . $item->id, $item->id, 'delete-user', 'get');

            $data[] = array(
                'name'        => $item->name,
                'activities'  => $activities,
                //'modal'       => $modal
            );
        }

        return $data;
    }

    /**
     * Build activities for a policy row in datatable
     *
     * @override
     * @param object $item
     * @return string
     */
    protected function buildActivities(object $item) : string
    {
        // Info
        $infoUrl = ( BaseHelper::hasCapability('read_policies') ? cms_dashboard_page_route('details-policy') . '&id=' . BaseHelper::encodeSequence($item->id) : "#" );
        $infoButton = ( BaseHelper::hasCapability('read_policies') ? '<button style="color: #666; border-color: #666;" type="button" class="button button-secondary" name="details"><span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-info"></span></button>' : '<button style="color: #666; border-color: #666;" type="button" style="cursor: not-allowed;" class="button button-secondary button-disabled" name="details"><span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-info"></span></button>' );

        // Detach
        $detachUrl = ( BaseHelper::hasCapability('admin_policies') ? cms_dashboard_page_route('delete-user') . '&id=' . BaseHelper::encodeSequence($item->id) : "#" );
        $detachButton = ( BaseHelper::hasCapability('admin_policies') ? '<button style="color: #666; border-color: #666;" id="users-delete-'.BaseHelper::encodeSequence($item->id).'" type="button" class="button button-secondary" name="delete"><span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-dismiss"></span></button>' : '<button style="color: #666; border-color: #666;" id="testBtn" type="button" style="cursor: not-allowed;" class="button button-secondary button-disabled" name="delete"><span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-dismiss"></span></button>' );

        $activities = <<<EOF
            <a style="text-decoration: none;" href="{$infoUrl}">
                {$infoButton}
            </a>

            <a style="text-decoration: none;" data-toggle="modal" data-target="" href="#">
                {$detachButton}
            </a>
            EOF;


        return $activities;
    }

    /**
     * Define what data to show on each column of the table
     *
     * @override
     * @param  array $item        Data
     * @param  String $column_name - Current column name
     * @return mixed
     */
    public function column_default( $item, $column_name )
    {
        switch( $column_name ) {
            case 'name':
            case 'activities':
                return $item[ $column_name ];

            default:
                return print_r( $item, true ) ;
        }
    }

    /**
     * Allows you to sort the data by the variables set in the $_GET
     *
     * @override
     * @return mixed
     */
    protected function sort_data( $a, $b )
    {
        // Set defaults
        $orderby = 'name';
        $order = 'asc';

        // If orderby is set, use this as the sort column
        if(!empty($_GET['orderby']))
        {
            $orderby = $_GET['orderby'];
        }

        // If order is set use this as the order
        if(!empty($_GET['order']))
        {
            $order = $_GET['order'];
        }


        $result = strcmp( $a[$orderby], $b[$orderby] );

        if($order === 'asc')
        {
            return $result;
        }

        return -$result;
    }


//    /**
//     * To show bulk action dropdown
//     *
//     * @override
//     * @return string[]
//     */
//    protected function get_bulk_actions()
//    {
//        $actions = array(
//            'export_all'    => "Export CSV",
//        );
//        return $actions;
//    }


}
