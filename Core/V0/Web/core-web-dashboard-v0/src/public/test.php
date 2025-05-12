<?php

require __DIR__.'/../vendor/autoload.php';


function ArrayChallenge($arr)
{

    sort($arr);
    $lenght = count($arr);

    //dd($arr);


    for ($i = 0; $i <= $lenght; $i++) {
        if ($arr[$i] + 1 == $arr[$i+1]) {
            $consecutive['start'] = $arr[$i];
            $consecutive[] = $arr[$i+1];
        }
    }


    dd($consecutive);


//    $lenght = count($arr);
//    $consecutive = [];
//    $sequence = [];
//    $min= [];
//
//    for ($i = 0; $i <= $lenght; $i++) {
//        if ($arr[$i] + 1 == $arr[$i+1]) {
//            $consecutive['foward'][] = $arr[$i+1];
//        }
//
//        if ($arr[$i] < $arr[$i+1]) {
//            $consecutive['back'][] = $arr[$i];
//        }
//    }
//
//    dd($consecutive);
//
//    foreach ($arr as $key => $value) {
//
//        if ($consecutive['foward'][0] == $consecutive['back'][$key]+1) {
//            $sequence[0] = $consecutive['back'][$key];
//            $sequence[1] = $consecutive['foward'][0];
//        }
//    }
//
//    dd($sequence);
//
//    foreach ($arr as $key => $value) {
//
//        for ($i = 0; $i <= $lenght; $i++) {
//
//            if ($sequence[1] + $i == $value) {
//                $sequence[] = $value;
//            }
//        }
//
////        if ($sequence[0] > $value) {
////            $min[] = $value;
////        }
//
//    }
//
//
//    sort($sequence);
//    //dd($sequence);
//    $unique = array_unique($sequence);
//    dd($unique);
//    $result = count($unique);
//
//    return $result;


    return $arr;
}

// keep this function call here
//echo ArrayChallenge(array(6, 7, 3, 1, 100, 102, 6, 12));
echo ArrayChallenge(array(5, 6, 1, 2, 8, 9, 7));
//echo ArrayChallenge(array(9, 2, 3, 7, 7, 6, 1, 2, 8, 9, 5));
//echo ArrayChallenge(array(56,78,79,80, 32,31,1,2,88,90,34,35,9, 2, 3, 7, 7, 6, 1, 2, 8, 9,33, 5));

