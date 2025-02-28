<section class="page-section">
    <div class="container">
        <div class="product-item">
            <div class="product-item-title d-flex">
                <div class="bg-faded p-5 d-flex ml-auto rounded">
                    <h2 class="section-heading mb-0">
                        <span class="section-heading-lower"><?php the_title(); ?></span>
                    </h2>
                </div>
            </div>
            <?php the_post_thumbnail( 'mypharm-frontend-featured-image', 'class=product-item-img mx-auto d-flex rounded img-fluid mb-3 mb-lg-0' ); ?>
            <div class="product-item-description d-flex mr-auto">
                <div class="bg-faded p-5 rounded">
                    <?php the_content(); ?>
                </div>
            </div>
        </div>
    </div>
</section>
