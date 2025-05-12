<?php

/**
 * @var \Sindria\Monitoring\ViewModel\IndexViewModel $viewModel
 */
$viewModel = \Sindria\Monitoring\ViewModel\IndexViewModel::getInstance();

?>


<div class="wrap">

    <h1>Monitoring</h1>



</div>

<style>

</style>


<script>

    function ready(callback) {
        // in case the document is already rendered
        if (document.readyState!='loading') callback();
        // modern browsers
        else if (document.addEventListener) document.addEventListener('DOMContentLoaded', callback);
        // IE <= 8
        else document.attachEvent('onreadystatechange', function() {
                if (document.readyState=='complete') callback();
            });
    }





    /**
     * Execute
     */
    ready(function() {



    });


</script>
