<?php get_header(); ?>

<?php

global $post;
$post = get_post(get_post_id_by_name('home-intro'));
setup_postdata($post);

get_template_part( 'components/pages/home', 'intro');

wp_reset_postdata();

$post = get_post(get_post_id_by_name('home-outro'));
setup_postdata($post);

get_template_part( 'components/pages/home', 'outro');

?>

<?php get_footer(); ?>



