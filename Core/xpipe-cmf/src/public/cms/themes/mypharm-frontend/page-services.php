<?php get_header(); ?>

<?php
$args = array(
    'category' => 2
);

global $post;
$posts = get_posts($args);
foreach($posts as $post) :
    setup_postdata($post);

    get_template_part( 'components/pages/page', "services");

endforeach; // End of the loop.
?>

<?php get_footer(); ?>