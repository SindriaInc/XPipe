<?php

namespace Sindria\Toolkit\Form;


abstract class Form
{
    /**
     * @var object
     */
    public object $entry;

    /**
     * @var string
     */
    public string $id;

    /**
     * @var string
     */
    public string $name;

    /**
     * @var string
     */
    public string $action;

    /**
     * @var string
     */
    public string $method;

    /**
     * @var string
     */
    public string $cancel;

    /**
     * Form constructor
     *
     * @param $entry
     * @param $id
     * @param $name
     * @param $action
     * @param $method
     * @param $cancel
     * @param $args
     */
    public function __construct($entry, $id, $name, $action, $method, $cancel, $args = array())
    {
        $this->entry = $entry;

        $this->id = $id;
        $this->name = $name;
        $this->action = $action;
        $this->method = $method;
        $this->cancel = $cancel;

//        parent::__construct($args);
    }

    /**
     * Displays the form.
     *
     * @override
     * @since 3.1.0
     */
    public function display()
    {
//        $this->display_tablenav( 'top' );
        $this->renderForm();
//        $this->display_tablenav( 'bottom' );
    }

    /**
     * Get html part to display required red star symbol alongside label
     *
     * @return string
     */
    public function requiredStar()
    {
        return '<span style="color: #ff0000; font-size: 20px" class="description"> *</span>';
    }

    /**
     * Make generic input hidden
     *
     * @param $name
     * @param $value
     * @return void
     */
    public function inputHidden($name, $value)
    {
        ?>
        <input name="<?= $name ?>" type="hidden" value="<?= $value ?>">
        <?php
    }

    /**
     * Make generic input name - first letter uppercase
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $placeholder
     * @param $value
     * @param $required
     * @return void
     */
    public function inputName($id, $name, $label, $placeholder, $value, $required, $attributes = "")
    {
        ?>

<!--        <tr class="form-field --><?php //= $required ? "form-required" : "" ?><!--">-->
<!--            <th scope="row">-->
<!--                <label for="--><?php //= $name ?><!--">--><?php //= $label ?><!--</label>--><?php //= $required ? $this->requiredStar() : "" ?>
<!--            </th>-->
<!--            <td>-->
<!--                <input id="--><?php //= $id ?><!--" type="text" placeholder="--><?php //= $placeholder ?><!--" name="--><?php //= $name ?><!--" value="--><?php //= $value ?><!--" autocapitalize="words" --><?php //= $required ? "required" : "" ?><!-- --><?php //= " " . $attributes ?><!-->
<!--            </td>-->
<!--        </tr>-->


        <div class="admin__field field field-code  <?= $required ? "required" : "" ?> ">
            <label class="label admin__field-label" for="<?= $id ?>" ><span><?= $label ?></span></label>
            <div class="admin__field-control control">
                <input id="<?= $id ?>" type="text" name="<?= $name ?>"  value="<?= $value ?>" placeholder="<?= $placeholder ?>" class="input-text admin__control-text <?= $required ? "required-entry _required" : "" ?>" aria-required="<?= $required ? "true" : "false" ?>">                            </div>
        </div>


        <?php
    }

    /**
     * Make generic input text
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $placeholder
     * @param $value
     * @param $required
     * @return void
     */
    public function inputText($id, $name, $label, $placeholder, $value, $required, $attributes = "")
    {
        ?>

<!--        <tr class="form-field --><?php //= $required ? "form-required" : "" ?><!--">-->
<!--            <th scope="row">-->
<!--                <label for="--><?php //= $name ?><!--">--><?php //= $label ?><!--</label>--><?php //= $required ? $this->requiredStar() : "" ?>
<!--            </th>-->
<!--            <td>-->
<!--                <input id="--><?php //= $id ?><!--" type="text" placeholder="--><?php //= $placeholder ?><!--" name="--><?php //= $name ?><!--" value="--><?php //= $value ?><!--" --><?php //= $required ? "required" : "" ?><!-- --><?php //= " " . $attributes ?><!-->
<!--            </td>-->
<!--        </tr>-->


        <div class="admin__field field field-code  <?= $required ? "required" : "" ?> ">
            <label class="label admin__field-label" for="<?= $id ?>" ><span><?= $label ?></span></label>
            <div class="admin__field-control control">
                <input id="<?= $id ?>" type="text" name="<?= $name ?>"  value="<?= $value ?>" placeholder="<?= $placeholder ?>" class="input-text admin__control-text <?= $required ? "required-entry _required" : "" ?>" aria-required="<?= $required ? "true" : "false" ?>">                            </div>
        </div>

        <?php
    }

    /**
     * Make generic input textarea
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $placeholder
     * @param $value
     * @param $required
     * @return void
     */
    public function inputTextArea($id, $name, $label, $placeholder, $value, $required, $attributes = "")
    {
        ?>





        <div class="admin__field <?= $required ? "required" : "" ?>">
            <div class="admin__field-label">
                <label for="<?= $name ?>">
                    <span"><?= $label ?></span>
                </label>
            </div>
            <div class="admin__field-control" >
                <textarea class="admin__control-textarea" name="<?= $name ?>" cols="15" rows="2" id="<?= $id ?>" placeholder="<?= $placeholder ?>" <?= " " . $attributes ?>></textarea>


<!--                <div class="admin__field-note" data-bind="attr: {id: noticeId}" id="notice-PWBSMYY">-->
<!--                    <span data-bind="i18n: notice">Maximum 255 chars. Meta Description should optimally be between 150-160 characters</span>-->
<!--                </div>-->
            </div>
        </div>


        <?php
    }

    /**
     * Make generic input editor
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $value
     * @param $required
     * @return void
     */
    public function inputEditor($id, $name, $label, $value, $required)
    {
        ?>

        <tr class="form-field <?= $required ? "form-required" : "" ?>">
            <th scope="row">
                <label for="<?= $name ?>"><?= $label ?></label><?= $required ? $this->requiredStar() : "" ?>
            </th>
            <td>
                <?php wp_editor($value, $id, array()); ?>
            </td>
        </tr>

        <style>

            .form-field input[type=email], .form-field input[type=number], .form-field input[type=password], .form-field input[type=search], .form-field input[type=tel], .form-field input[type=text], .form-field input[type=url], .form-field textarea {
                border-style: solid;
                border-width: 1px;
                width: 100%;
            }

        </style>

        <?php
    }


    /**
     * Make generic input code editor
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $value
     * @param $required
     * @return void
     */
    public function inputCodeEditor($id, $name, $label, $value, $required, $attributes = "")
    {
        ?>

        <tr class="form-field <?= $required ? "form-required" : "" ?>">
            <th scope="row">
                <label for="<?= $name ?>"><?= $label ?></label><?= $required ? $this->requiredStar() : "" ?>
            </th>
            <td>
                <textarea id="<?= $id ?>" name="<?= $name ?>" value="<?= $value ?>" <?= $required ? "required" : "" ?> <?= " " . $attributes ?>></textarea>
                <div id="editor"></div>
            </td>
        </tr>


        <style type="text/css" media="screen">
            #editor {
                /*position: absolute;*/
                top: 0;
                right: 0;
                bottom: 0;
                left: 0;
                min-height: 300px;
                width: 95%;
            }


            /*.form-field input[type=email], .form-field input[type=number], .form-field input[type=password], .form-field input[type=search], .form-field input[type=tel], .form-field input[type=text], .form-field input[type=url], .form-field textarea {*/
            /*    border-style: solid;*/
            /*    border-width: 1px;*/
            /*    width: 100%;*/
            /*}*/

        </style>

        <script src="<?php echo plugin_dir_url( __FILE__ ) . '../static/js/jquery-3.7.1.min.js' ?>" crossorigin="anonymous" referrerpolicy="no-referrer"></script>

        <script src="<?php echo plugin_dir_url( __FILE__ ) . '../static/js/ace/ace.js' ?>" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="<?php echo plugin_dir_url( __FILE__ ) . '../static/js/ace/mode-json.js' ?>" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
        <script src="<?php echo plugin_dir_url( __FILE__ ) . '../static/js/ace/theme-monokai.js' ?>" crossorigin="anonymous" referrerpolicy="no-referrer"></script>


        <script>

            var editor = ace.edit("editor");
            editor.setTheme("ace/theme/monokai");
            editor.session.setMode("ace/mode/json");

            var textarea = $('textarea[name="content"]').hide();
            console.log(textarea);
            editor.getSession().setValue(textarea.val());
            editor.getSession().on('change', function(){
                textarea.val(editor.getSession().getValue());
            });

        </script>

        <?php
    }


    /**
     * Make generic input email
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $placeholder
     * @param $value
     * @param $required
     * @return void
     */
    public function inputEmail($id, $name, $label, $placeholder, $value, $required, $attributes = "")
    {
        ?>

<!--        <tr class="form-field --><?php //= $required ? "form-required" : "" ?><!--">-->
<!--            <th scope="row">-->
<!--                <label for="--><?php //= $name ?><!--">--><?php //= $label ?><!--</label>--><?php //= $required ? $this->requiredStar() : "" ?>
<!--            </th>-->
<!--            <td>-->
<!--                <input id="--><?php //= $id ?><!--" type="email" placeholder="--><?php //= $placeholder ?><!--" name="--><?php //= $name ?><!--" value="--><?php //= $value ?><!--" --><?php //= $required ? "required" : "" ?><!-- --><?php //= " " . $attributes ?><!-->
<!--            </td>-->
<!--        </tr>-->

        <div class="admin__field field field-code  <?= $required ? "required" : "" ?>">
            <label class="label admin__field-label" for="<?= $id ?>" ><span><?= $label ?></span></label>
            <div class="admin__field-control control">
                <input id="<?= $id ?>" type="email" name="<?= $name ?>"  value="<?= $value ?>" placeholder="<?= $placeholder ?>" class="input-text admin__control-text <?= $required ? "required-entry _required" : "" ?>" aria-required="<?= $required ? "true" : "false" ?> <?= " " . $attributes ?>">
            </div>
        </div>

        <?php
    }

    /**
     * Make generic input number
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $placeholder
     * @param $value
     * @param $required
     * @return void
     */
    public function inputNumber($id, $name, $label, $placeholder, $value, $required, $attributes = "")
    {
        ?>

<!--        <tr class="form-field --><?php //= $required ? "form-required" : "" ?><!--">-->
<!--            <th scope="row">-->
<!--                <label for="--><?php //= $name ?><!--">--><?php //= $label ?><!--</label>--><?php //= $required ? $this->requiredStar() : "" ?>
<!--            </th>-->
<!--            <td>-->
<!--                <input id="--><?php //= $id ?><!--" type="number" placeholder="--><?php //= $placeholder ?><!--" name="--><?php //= $name ?><!--" value="--><?php //= $value ?><!--" --><?php //= $required ? "required" : "" ?><!-- --><?php //= " " . $attributes ?><!-->
<!--            </td>-->
<!--        </tr>-->

        <div class="admin__field field field-code  <?= $required ? "required" : "" ?> ">
            <label class="label admin__field-label" for="<?= $id ?>" ><span><?= $label ?></span></label>
            <div class="admin__field-control control">
                <input id="<?= $id ?>" type="number" name="<?= $name ?>"  value="<?= $value ?>" placeholder="<?= $placeholder ?>" class="input-text admin__control-text <?= $required ? "required-entry _required" : "" ?>" aria-required="<?= $required ? "true" : "false" ?> <?= " " . $attributes ?>">
            </div>
        </div>

        <?php
    }


    /**
     * Make generic input password
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $placeholder
     * @param $value
     * @param $required
     * @return void
     */
    public function inputPassword($id, $name, $label, $placeholder, $value, $required, $attributes = "")
    {
        ?>

        <tr class="form-field <?= $required ? "form-required" : "" ?>">
            <th scope="row">
                <label for="<?= $name ?>"><?= $label ?></label><?= $required ? $this->requiredStar() : "" ?>
            </th>
            <td>
                <input id="<?= $id ?>" type="password" placeholder="<?= $placeholder ?>" name="<?= $name ?>" value="<?= $value ?>" <?= $required ? "required" : "" ?> <?= " " . $attributes ?>>
            </td>
        </tr>

        <?php
    }

    /**
     * Make generic input select
     *
     * @param $id
     * @param $name
     * @param $label
     * @param $options
     * @param $current
     * @param $required
     * @return void
     */
    public function inputSelect($id, $name, $label, $options, $current, $required, $attributes = "")
    {
        ?>


        <div class="admin__field <?= $required ? "required" : "" ?>" >
            <div class="admin__field-label">
                <label for="<?= $name ?>">
                    <span ><?= $label ?></span>
                </label>
            </div>
            <div class="admin__field-control">
                <select class="admin__control-select"  name="<?= $name ?>" id="<?= $id ?>" <?= " " . $attributes ?>>
                    <?php foreach ($options as $key => $option): ?>
                        <option value="<?= $option['value'] ?>" <?= $current == $option['value'] ? " selected" : " " ?>><?= $option['label'] ?></option>
                    <?php endforeach; ?>
                </select>

            </div>
        </div>
        <?php
    }

    public function inputCheckbox($id, $name, $label, $checked, $value, $required)
    {
        ?>

<!--        <tr>-->
<!--            <th scope="row">Send User Notification</th>-->
<!--            <td>-->
<!--                <input type="checkbox" name="send_user_notification" id="send_user_notification" value="1" checked="checked">-->
<!--                <label for="send_user_notification">Send the new user an email about their account.</label>-->
<!--            </td>-->
<!--        </tr>-->

        <div class="admin__field <?= $required ? "required" : "" ?>">
            <div class="admin__field-label">
                <label for="<?= $name ?>">
                    <span><?= $label ?></span>
                </label>
            </div>
            <div class="admin__field-control">
                <div class="admin__field admin__field-option">
                    <input type="checkbox" class="admin__control-checkbox" id="<?= $id ?>" name="<?= $name ?>" value="<?= $value ?>" <?= $checked ? "checked" : "" ?>>

                    <label class="admin__field-label"  for="<?= $name ?>"></label>

                </div>

            </div>
        </div>

        <?php
    }

    public function inputRadio($id, $name, $label, $placeholder, $value, $required)
    {

    }

    public function inputMultiSelect($id, $name, $label, $placeholder, $value, $required)
    {

    }


    /**
     * Default form inputs examples - needs to be overridden in form subclasses
     *
     * @return void
     */
    protected function makeInputs()
    {
        $this->inputName("name", "name", 'Name', 'Pippo', old('name'), true);
        $this->inputEmail('email', 'email', 'Email', 'pippo@example.com', old('email'), true);
        $this->inputText('username', 'username', 'Username', 'pippo', old('username'), true);

        $options = [
            [
                'value' => 0,
                'label' => 'Disabled',
            ],
            [
                'value' => 1,
                'label' => 'Enabled',
            ]
        ];
        $this->inputSelect('enabled', 'enabled', 'Status', $options, 0, false);

    }


    /**
     * Render form with inputs and actions
     *
     * @return void
     */
    private function renderForm()
    {
        ?>

        <style>


        </style>



        <div class="entry-edit form-inline">
            <form id="<?= $this->id ?>" action="<?= $this->action ?>" method="<?= $this->method ?>" novalidate="novalidate">
                <div><input name="<?= $this->name ?>" type="hidden" value="<?= $this->action ?>"></div>

                <fieldset class="fieldset admin__fieldset fieldset-wide" id="base">

                    <div class="messages">
                    </div>


                    <?php $this->makeInputs() ?>

                    <button type="submit" class="action-default primary" name="submit"><?= "Save"  ?></button>
                    <button type="reset" class="button button-secondary" onclick="window.location.href = '<?= $this->cancel ?>'"><?= "Cancel" ?></button>


                </fieldset>

            </form>
        </div>


        <?php
    }



}
