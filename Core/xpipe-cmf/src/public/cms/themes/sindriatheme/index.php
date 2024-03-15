<?php

header('Location: http://' . $_SERVER['HTTP_HOST'] . '/_/wp-admin');

//if (session()->has('locale')) {
//
//    if (session()->get('locale') == 'en') {
//        header('Location: http://' . $_SERVER['HTTP_HOST'] . '/en/wp-admin');
//    }
//
//    if (session()->get('locale') == 'it') {
//        header('Location: http://' . $_SERVER['HTTP_HOST'] . '/it/wp-admin');
//    }
//
//    if (session()->get('locale') == 'es') {
//        header('Location: http://' . $_SERVER['HTTP_HOST'] . '/es/wp-admin');
//    }
//
//}

//$current_locale = substr($_SERVER["HTTP_ACCEPT_LANGUAGE"],0,2);
//
//if ($current_locale == 'en') {
//    header('Location: http://' . $_SERVER['HTTP_HOST'] . '/en/wp-admin');
//}
//
//if ($current_locale == 'it') {
//    header('Location: http://' . $_SERVER['HTTP_HOST'] . '/it/wp-admin');
//}
//
//if ($current_locale == 'es') {
//    header('Location: http://' . $_SERVER['HTTP_HOST'] . '/es/wp-admin');
//}
