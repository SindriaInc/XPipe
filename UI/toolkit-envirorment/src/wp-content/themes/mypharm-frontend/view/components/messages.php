<?php
/**
 * @file messages.php
 *
 * @var $errors
 */
?>

<?php if (isset($errors)): ?>
    <?php if (count($errors) > 0): ?>
        <div class="form-group">
            <?php foreach ($errors->all() as $error): ?>
                <div class="alert alert-danger" role="alert">
                    <em><?= $error ?></em>
                </div>
            <?php endforeach; ?>
        </div>
    <?php endif; ?>
<?php endif; ?>


<!-- Server side -->

<?php if (session()->has('success_message')): ?>
	<div class="alert alert-success" role="alert">
		<em><?= session()->get('success_message') ?></em>
	</div>
<?php endif; ?>

<?php if (session()->has('danger_message')): ?>
	<div class="alert alert-danger" role="alert">
		<em><?= session()->get('danger_message') ?></em>
	</div>
<?php endif; ?>

<?php if (session()->has('warning_message')): ?>
    <div class="alert alert-warning" role="alert">
        <em><?= session()->get('warning_message') ?></em>
    </div>
<?php endif; ?>

<?php if (session()->has('info_message')): ?>
    <div class="alert alert-info" role="alert">
        <em><?= session()->get('info_message') ?></em>
    </div>
<?php endif; ?>



<!-- Client side -->

<div id="success_message" style="display: none" class="alert alert-success" role="alert">
    <em></em>
</div>

<div id="danger_message" style="display: none" class="alert alert-danger" role="alert">
    <em></em>
</div>

<div id="warning_message" style="display: none" class="alert alert-warning" role="alert">
    <em></em>
</div>

<div id="info_message" style="display: none" class="alert alert-info" role="alert">
    <em></em>
</div>


<style>

    .alert {
        position: relative;
        padding: 0.75rem 1.25rem;
        margin-bottom: 1rem;
        border: 1px solid transparent;
        border-radius: 0.25rem;
    }


    .alert-heading {
        color: inherit;
    }

    .alert-link {
        font-weight: 700;
    }

    .alert-dismissible {
        padding-right: 3.85rem;
    }
    .alert-dismissible .close {
        position: absolute;
        top: 0;
        right: 0;
        z-index: 2;
        padding: 0.75rem 1.25rem;
        color: inherit;
    }

    .alert-primary {
        color: #1b4b72;
        background-color: #d6e9f8;
        border-color: #c6e0f5;
    }
    .alert-primary hr {
        border-top-color: #b0d4f1;
    }
    .alert-primary .alert-link {
        color: #113049;
    }

    .alert-secondary {
        color: #383d41;
        background-color: #e2e3e5;
        border-color: #d6d8db;
    }
    .alert-secondary hr {
        border-top-color: #c8cbcf;
    }
    .alert-secondary .alert-link {
        color: #202326;
    }

    .alert-success {
        color: #1d643b;
        background-color: #d7f3e3;
        border-color: #c7eed8;
    }
    .alert-success hr {
        border-top-color: #b3e8ca;
    }
    .alert-success .alert-link {
        color: #123c24;
    }

    .alert-info {
        color: #385d7a;
        background-color: #e2f0fb;
        border-color: #d6e9f9;
    }
    .alert-info hr {
        border-top-color: #c0ddf6;
    }
    .alert-info .alert-link {
        color: #284257;
    }

    .alert-warning {
        color: #857b26;
        background-color: #fffbdb;
        border-color: #fffacc;
    }
    .alert-warning hr {
        border-top-color: #fff8b3;
    }
    .alert-warning .alert-link {
        color: #5d561b;
    }

    .alert-danger {
        color: #761b18;
        background-color: #f9d6d5;
        border-color: #f7c6c5;
    }
    .alert-danger hr {
        border-top-color: #f4b0af;
    }
    .alert-danger .alert-link {
        color: #4c110f;
    }

    .alert-light {
        color: #818182;
        background-color: #fefefe;
        border-color: #fdfdfe;
    }
    .alert-light hr {
        border-top-color: #ececf6;
    }
    .alert-light .alert-link {
        color: #686868;
    }

    .alert-dark {
        color: #1b1e21;
        background-color: #d6d8d9;
        border-color: #c6c8ca;
    }
    .alert-dark hr {
        border-top-color: #b9bbbe;
    }
    .alert-dark .alert-link {
        color: #040505;
    }

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

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }

    function eraseCookie(name) {
        document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }

    function createParagraph(words) {
        return words.join(' ');
    }


    /**
     * Execute
     */
    ready(function() {

        // Validation messages
        let validation = getCookie('validation');

        let success_message = getCookie('success_message');
        let danger_message = getCookie('danger_message');
        let warning_message = getCookie('warning_message');
        let info_message = getCookie('info_message');

        // Validation messages
        eraseCookie('validation');

        eraseCookie('success_message');
        eraseCookie('danger_message');
        eraseCookie('warning_message');
        eraseCookie('info_message');

        //console.log(validation);

        //console.log(success_message);
        //console.log(danger_message);
        //console.log(warning_message);
        //console.log(info_message);

        // Validation messages
        if (validation !== undefined) {

            var json = JSON.parse(validation);
            var errors = [];

            for (var i in json) {
                errors.push([i, json [i]]);
            }

            var messages = [];

            for (var error of errors) {
                let message = error[1][0];
                //console.log(message);
                messages.push(message);
            }

            //console.log(createParagraph(messages));

            $("#danger_message").show().text(createParagraph(messages))
        }

        if (success_message !== undefined) {
            $("#success_message").show().text(success_message)
        }

        if (danger_message !== undefined) {
            $("#danger_message").show().text(danger_message)
        }

        if (warning_message !== undefined) {
            $("#warning_message").show().text(warning_message)
        }

        if (info_message !== undefined) {
            $("#info_message").show().text(info_message)
        }

        setTimeout(function(){
            $('#success_message, #danger_message, #warning_message, #info_message').slideUp();
        }, 3500);


    });

</script>
