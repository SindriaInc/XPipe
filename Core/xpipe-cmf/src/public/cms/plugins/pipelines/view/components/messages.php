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


<script>

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
    }

    function delCookie(name) {
        document.cookie = name +'=; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }

    /**
     * General backoffice function
     *
     * @param e
     */
    $(document).ready(function() {

        let success_message = getCookie('success_message');
        let danger_message = getCookie('danger_message');
        let warning_message = getCookie('warning_message');
        let info_message = getCookie('info_message');

        delCookie('success_message');
        delCookie('danger_message');
        delCookie('warning_message');
        delCookie('info_message');

        console.log(success_message)
        console.log(danger_message)
        console.log(warning_message)
        console.log(info_message)

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
