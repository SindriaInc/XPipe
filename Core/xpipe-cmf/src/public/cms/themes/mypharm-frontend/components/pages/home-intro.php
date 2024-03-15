<section class="page-section clearfix">
    <div class="container">
        <div class="intro">
            <?php the_post_thumbnail( 'mypharm-frontend-featured-image', 'class=intro-img img-fluid mb-3 mb-lg-0 rounded' ); ?>
            <div class="intro-text left-0 text-center bg-faded p-5 rounded" style="margin-top: 150px;">
                <h2 class="section-heading mb-4">
                    <span class="section-heading-lower"><?php the_title(); ?></span>
                </h2>
                <?php the_content();?>
            </div>
        </div>
    </div>
</section>
