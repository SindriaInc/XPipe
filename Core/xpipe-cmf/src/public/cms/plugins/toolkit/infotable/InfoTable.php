<?php

namespace Sindria\Toolkit\InfoTable;

use WP_List_Table;

abstract class InfoTable extends WP_List_Table
{
    /**
     * @var object
     */
    public object $entry;

    /**
     * @var string
     */
    public string $back;

    /**
     * InfoTable constructor
     *
     * @param object $entry
     * @param string $back
     * @param string $args
     */
    public function __construct(object $entry, string $back, $args = array())
    {
        $this->entry = $entry;
        $this->back = $back;

        parent::__construct($args);
    }

    /**
     * Displays the table.
     *
     * @override
     * @since 3.1.0
     */
    public function display() {
        $singular = $this->_args['singular'];

        $this->display_tablenav( 'top' );

        ?>
        <table class="wp-list-table <?php echo implode( ' ', $this->get_table_classes() ); ?>">
            <thead>

            </thead>

            <tbody id="the-list"
                <?php
                if ( $singular ) {
                    echo " data-wp-lists='list:$singular'";
                }
                ?>
            >

            <?php $this->renderFields() ?>

            </tbody>


        </table>
        <?php
        $this->display_tablenav( 'bottom' );
    }

    /**
     * Generates the table navigation above or below the table
     *
     * @override
     * @since 3.1.0
     * @param string $which
     */
    protected function display_tablenav( $which ) {

        ?>

        <div class="tablenav <?php echo esc_attr( $which ); ?>">

            <?php if ($which == 'bottom'): ?>

                <button type="reset" class="button button-primary" onclick="window.location.href = '<?= $this->back ?>'">
                    <span class="dashicons-before dashicons-arrow-left-alt2"></span>
                </button>

            <?php endif; ?>

            <br class="clear" />
        </div>

        <style>
            .dashicons-arrow-left-alt2:before {
                margin: 5px 0px 5px 0px;
            }
        </style>

        <?php
    }

    /**
     * Default info table fields examples - needs to be overridden in form subclasses
     *
     * @return array
     */
    protected function fields()
    {
        return [
            [
                'label' => 'ID',
                'value' => 0,
            ],
            [
                'label' => 'Username',
                'value' => 'paolo.rossi',
            ],
            [
                'label' => 'Email',
                'value' => 'paolo.rossi@example.com',
            ],
            [
                'label' => 'Name',
                'value' => 'Paolo',
            ],
            [
                'label' => 'Surname',
                'value' => 'Rossi',
            ],
        ];
    }


    /**
     * Render fields with label and value
     *
     * @return void
     */
    private function renderFields()
    {
        foreach ($this->fields() as $key => $entry) {

            ?>

            <tr>
                <th scope="col"><?= $entry['label'] ?></th>
                <td><?= $entry['value']  ?></td>
            </tr>

            <?php

        }
    }



}
