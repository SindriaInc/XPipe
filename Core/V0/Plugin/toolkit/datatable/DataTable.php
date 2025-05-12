<?php

namespace Sindria\Toolkit\Datatable;

use WP_List_Table;

abstract class DataTable extends WP_List_Table
{
    /**
     * @var object
     */
    public object $collection;

    /**
     * @var string
     */
    public string $exportAction;

    /**
     * DataTable constructor
     *
     * @param object $collection
     * @param string $exportAction
     * @param array $args
     */
    public function __construct(object $collection, string $exportAction, array $args = array())
    {
        $this->collection = $collection;
        $this->exportAction = $exportAction;

        parent::__construct($args);
    }


    /**
     * Prepare the items for the table to process
     *
     * @return Void
     */
    public function prepare_items()
    {
        $columns = $this->get_columns();
        $hidden = $this->get_hidden_columns();
        $sortable = $this->get_sortable_columns();

        $data = $this->tableData();
        usort( $data, array( &$this, 'sort_data' ) );

        $perPage = 5;
        $currentPage = $this->get_pagenum();
        $totalItems = count($data);

        $this->set_pagination_args( array(
            'total_items' => $totalItems,
            'per_page'    => $perPage
        ) );

        $data = array_slice($data,(($currentPage-1)*$perPage),$perPage);

        $this->_column_headers = array($columns, $hidden, $sortable);
        $this->items = $data;
    }

    /**
     * Override the parent columns method. Defines the columns to use in your listing table
     *
     * @override
     * @return array
     */
    public function get_columns()
    {
        $columns = array(
            'id'          => 'ID',
            'title'       => 'Title',
            'director'    => 'Director',
            'rating'      => 'Rating',
            'activities'  => 'Activities',
        );

        return $columns;
    }

    /**
     * Define which columns are hidden
     *
     * @return array
     */
    public function get_hidden_columns()
    {
        return array();
    }

    /**
     * Define the sortable columns
     *
     * @return array
     */
    public function get_sortable_columns()
    {
        return array(
            'title' => array('title', false),
            'director' => array('director', false),
        );
    }


    /**
     * Build table data with activities and modals - mock in abstract class
     *
     * @return array
     */
    protected function tableData()  : array
    {
        $data = array();

        $item = new \stdClass();
        $activities = $this->buildActivities($item);

        $data[] = array(
            'id'          => 1,
            'title'       => 'The Shawshank Redemption',
            'description' => 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.',
            'year'        => '1994',
            'director'    => 'Frank Darabont',
            'rating'      => '9.3',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 2,
            'title'       => 'The Godfather',
            'description' => 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.',
            'year'        => '1972',
            'director'    => 'Francis Ford Coppola',
            'rating'      => '9.2',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 3,
            'title'       => 'The Godfather: Part II',
            'description' => 'The early life and career of Vito Corleone in 1920s New York is portrayed while his son, Michael, expands and tightens his grip on his crime syndicate stretching from Lake Tahoe, Nevada to pre-revolution 1958 Cuba.',
            'year'        => '1974',
            'director'    => 'Francis Ford Coppola',
            'rating'      => '9.0',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 4,
            'title'       => 'Pulp Fiction',
            'description' => 'The lives of two mob hit men, a boxer, a gangster\'s wife, and a pair of diner bandits intertwine in four tales of violence and redemption.',
            'year'        => '1994',
            'director'    => 'Quentin Tarantino',
            'rating'      => '9.0',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 5,
            'title'       => 'The Good, the Bad and the Ugly',
            'description' => 'A bounty hunting scam joins two men in an uneasy alliance against a third in a race to find a fortune in gold buried in a remote cemetery.',
            'year'        => '1966',
            'director'    => 'Sergio Leone',
            'rating'      => '9.0',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 6,
            'title'       => 'The Dark Knight',
            'description' => 'When Batman, Gordon and Harvey Dent launch an assault on the mob, they let the clown out of the box, the Joker, bent on turning Gotham on itself and bringing any heroes down to his level.',
            'year'        => '2008',
            'director'    => 'Christopher Nolan',
            'rating'      => '9.0',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 7,
            'title'       => '12 Angry Men',
            'description' => 'A dissenting juror in a murder trial slowly manages to convince the others that the case is not as obviously clear as it seemed in court.',
            'year'        => '1957',
            'director'    => 'Sidney Lumet',
            'rating'      => '8.9',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 8,
            'title'       => 'Schindler\'s List',
            'description' => 'In Poland during World War II, Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.',
            'year'        => '1993',
            'director'    => 'Steven Spielberg',
            'rating'      => '8.9',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 9,
            'title'       => 'The Lord of the Rings: The Return of the King',
            'description' => 'Gandalf and Aragorn lead the World of Men against Sauron\'s army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.',
            'year'        => '2003',
            'director'    => 'Peter Jackson',
            'rating'      => '8.9',
            'activities'  => $activities
        );

        $data[] = array(
            'id'          => 10,
            'title'       => 'Fight Club',
            'description' => 'An insomniac office worker looking for a way to change his life crosses paths with a devil-may-care soap maker and they form an underground fight club that evolves into something much, much more...',
            'year'        => '1999',
            'director'    => 'David Fincher',
            'rating'      => '8.8',
            'activities'  => $activities
        );


        return $data;
    }

    /**
     * Build activities for an entity row in datatable
     *
     * @param object $item
     * @return string
     */
    protected function buildActivities(object $item) : string
    {
        // Info
        $infoUrl = "#";

        // Edit
        $editUrl = "#";
        $editButton = '<button style="color: #666; border-color: #666;" type="button" class="button button-secondary" name="edit"><span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-edit"></span></button>';

        // Delete
        $deleteUrl = "#";
        $deleteButton = '<button style="color: #666; border-color: #666;" id="entity-delete-sequence" type="button" class="button button-secondary" name="delete"><span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-dismiss"></span></button>';

        $activities = <<<EOF
            <a style="text-decoration: none;" href="{$infoUrl}">
                <button style="color: #666; border-color: #666;" type="button" class="button button-secondary" name="details">
                    <span style="margin: 5px 0px 5px 0px;" class="dashicons dashicons-info"></span>
                </button>
            </a>

            <a style="text-decoration: none;" href="{$editUrl}">
                {$editButton}
            </a>

            <a style="text-decoration: none;" data-toggle="modal" data-target="" href="#">
                {$deleteButton}
            </a>
            EOF;


        return $activities;
    }

    /**
     * Define what data to show on each column of the table
     *
     * @param  array $item        Data
     * @param  String $column_name - Current column name
     *
     * @return Mixed
     */
    public function column_default( $item, $column_name )
    {
        switch( $column_name ) {
            case 'id':
            case 'title':
            case 'director':
            case 'rating':
            case 'activities':
                return $item[ $column_name ];

            default:
                return print_r( $item, true ) ;
        }
    }

    /**
     * Allows you to sort the data by the variables set in the $_GET
     *
     * @return mixed
     */
    protected function sort_data( $a, $b )
    {
        // Set defaults
        $orderby = 'title';
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

    /**
     * Generates the table navigation above or below the table
     *
     * @override
     * @since 3.1.0
     * @param string $which
     */
    protected function display_tablenav( $which ) {

        if ( 'top' === $which ) {
            ?>
            <div class="tablenav <?php echo esc_attr( $which ); ?>">

                <?php

                $this->extra_tablenav( $which );

                // TODO: check integration if useful
                //$this->search_box('search', 'users');

                // Disable pagination links from the top
                //$this->pagination( $which );
                ?>

                <br class="clear" />
            </div>
            <?php
        } else {
            ?>
            <div class="tablenav <?php echo esc_attr( $which ); ?>">

                <?php if ( $this->has_items() ) : ?>
                    <div class="alignleft actions bulkactions">
                        <form id="bulk-actions-form" name="bulk-actions-form" class="validate" method="post">
                            <?= csrf_field() ?>
                            <input name="page" type="hidden" value="<?= $this->exportAction ?>">
                            <?php $this->bulk_actions( $which ); ?>
                        </form>
                    </div>
                <?php
                endif;
                $this->extra_tablenav( $which );
                $this->pagination( $which );
                ?>

                <br class="clear" />
            </div>
            <?php
        }

    }



    /**
     * Displays the search box.
     *
     * @since 3.1.0
     *
     * @param string $text     The 'submit' button label.
     * @param string $input_id ID attribute value for the search input field.
     */
    public function search_box( $text, $input_id ) {
        if ( empty( $_REQUEST['s'] ) && ! $this->has_items() ) {
            return;
        }

        $input_id = $input_id . '-search-input';

        if ( ! empty( $_REQUEST['orderby'] ) ) {
            echo '<input type="hidden" name="orderby" value="' . esc_attr( $_REQUEST['orderby'] ) . '" />';
        }
        if ( ! empty( $_REQUEST['order'] ) ) {
            echo '<input type="hidden" name="order" value="' . esc_attr( $_REQUEST['order'] ) . '" />';
        }
        if ( ! empty( $_REQUEST['post_mime_type'] ) ) {
            echo '<input type="hidden" name="post_mime_type" value="' . esc_attr( $_REQUEST['post_mime_type'] ) . '" />';
        }
        if ( ! empty( $_REQUEST['detached'] ) ) {
            echo '<input type="hidden" name="detached" value="' . esc_attr( $_REQUEST['detached'] ) . '" />';
        }
        ?>
        <p class="search-box">
            <label class="screen-reader-text" for="<?php echo esc_attr( $input_id ); ?>"><?php echo $text; ?>:</label>
            <input type="search" id="<?php echo esc_attr( $input_id ); ?>" name="s" value="<?php _admin_search_query(); ?>" />
            <button type="submit" class="button button-secondary" name="submit"><span style="margin: 3px 0px 3px 0px;" class="dashicons dashicons-search"></span><?php //echo trans('global.button.save')  ?></button>
        </p>

        <?php
    }


}
