<?php

function sindria_dashboardmeta() {
    $csrf_token = csrf_token();
    echo '<meta name="csrf-token" content="' . $csrf_token . '">';
    echo '<meta http-equiv="Permissions-Policy" content="interest-cohort=()">';
}
