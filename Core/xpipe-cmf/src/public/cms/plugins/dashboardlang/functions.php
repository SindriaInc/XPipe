<?php

function sindria_dashboardlang($admin_bar) {

    ?>

    <style>

        #wp-toolbar>ul>li#wp-admin-bar-lang {
            margin: 8px 0px 0px 0px;
        }

        select#language-selector {
            border: none;
            padding: 0px 25px 0px 0px;
            background-color: transparent;
            color: #666;
            /*-webkit-appearance: none !important;*/
            /*-moz-appearance: none !important;*/
            /*text-indent: 1px;*/
            /*text-overflow: '' !important;*/
            appearance: none !important;
            /*font-family: inherit !important;*/
            /*outline: none !important;*/
        }

        .language-select {
            appearance: none !important;
            font-family: inherit !important;
            outline: none !important;
        }

        .language-select-container select {
            appearance: none !important;
            /* for Firefox */
            -moz-appearance: none;
            /* for Safari, Chrome, Opera */
            -webkit-appearance: none;
        }

        /* for IE10 */
        .language-select-container select::-ms-expand {
            appearance: none !important;
            display: none;
        }

        /* Desktop only */
        @media screen and (min-width: 783px) {

            .language-select-container {
                padding: calc( (var(--bar-height) - 32px)/2 ) 10px !important;
            }

        }




    </style>

    <?php

    $action = route('locale');
    $hidden = csrf_field();

    $callback = function () {
        $options = [];
        foreach (config('app.locales') as $availableLocale) {
            $attribute = session('locale')  == $availableLocale ? " selected" : "";
            $label = strtoupper($availableLocale);
            $option = "<option class=\"dropdown-item language-option\" value=\"$availableLocale\" $attribute>$label</option>";
            $options[] = $option;
        }

        return $options;
    };

    $options = $callback();



    $html = <<<EOF
        <form class="language-select-container" action="{$action}" method="post">
            {$hidden}
          <select id="language-selector" onchange="this.form.submit()" class="dropdown-toggle language-select" name="lang">
             {$options[0]}
             {$options[1]}
             {$options[2]}
          </select>
        </form>
        EOF;



    $args = array(
        'id'        => 'lang',
        'title'     =>  $html,
        'parent'    =>  'top-secondary',
        'href'      =>  '',
        'group'     =>  '',
        'meta'      =>  array(
            'html'     =>  '',
            'class'     =>  '',
            'rel'       =>  '',
            'lang'      =>  '',
            'dir'       =>  '',
            'onclick'   =>  '',
            'title'     =>  'Select Language'
        ),
    );
    $admin_bar->add_node($args);

}
