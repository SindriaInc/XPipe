<?php

/**
 * Permit specific origin iframe embed
 *
 * @return bool
 */
function sindria_access_control_allow_origin() {
    $origin= get_http_origin();
    $allowed_origins = array(//add your domains or keeps empty
        "example.com",
    );
    $allowed= false;
    if (count($allowed_origins) > 0) {
        foreach($allowed_origins as $allowed_origin) {
            if (strstr($origin,$allowed_origin)) {
                $allowed = true;
                break;
            }
        }
    } else {
        $allowed=true;
    }
    if ($allowed) {
        return true;
    } else {
        send_origin_headers();
    }
}

