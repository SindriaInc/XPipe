<section class="page-section about-heading">
    <div class="container">
        <?php the_post_thumbnail( 'mypharm-frontend-featured-image', 'class=img-fluid rounded about-heading-img mb-3 mb-lg-0' ); ?>
        <div class="about-heading-content">
            <div class="row">
                <div class="col-xl-9 col-lg-10 mx-auto">
                    <div class="bg-faded rounded p-5" style="margin-top: 50px;">
                        <h2 class="section-heading mb-4">
                            <span class="section-heading-lower"><?php the_title() ?></span>
                        </h2>
                        <?php the_content(); ?>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>