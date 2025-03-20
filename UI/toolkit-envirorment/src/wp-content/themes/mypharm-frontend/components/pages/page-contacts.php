<section class="page-section about-heading">
    <div class="container">
        <!-- Map Column -->
        <div class="col-12">
            <!-- Embedded Google Map -->
            <iframe width="100%" height="400px" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3007.4740529479477!2d9.385854915807409!3d41.08048787929351!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x12d9453fb5882935%3A0x9e850b3690dec173!2sCorso+G.+Garibaldi%2C+5%2C+07021+Arzachena+OT!5e0!3m2!1sit!2sit!4v1544911057344"></iframe>
        </div>
        <?php //the_post_thumbnail( 'mypharm-frontend-featured-image', 'class=img-fluid rounded about-heading-img mb-3 mb-lg-0' ); ?>
        <div class="about-heading-content">
            <div class="row">
                <div class="col-xl-9 col-lg-10 mx-auto">
                    <div class="bg-faded rounded p-5">
                        <h2 class="section-heading mb-4">
                            <span class="section-heading-lower"><?php //the_title() ?></span>
                        </h2>
                        <?php the_content(); ?>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>