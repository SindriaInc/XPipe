<?php


/**
 * Override wp-admin footer signature
 */
function sindria_admin_footer()
{
    echo '<span id="footer-note">Copyright &copy;<a style="color:inherit; text-decoration: none;" href="https://sindria.org" target="_blank"> Sindria Inc.</a> ' . date('Y') . '</span>';
}
