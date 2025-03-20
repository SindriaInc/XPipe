<div id="page:main-container" class="page-columns"><div class="admin__old"><div id="container" class="main-col"><!--
/**
 * Copyright &copy; Magento, Inc. All rights reserved.
 * See COPYING.txt for license details.
 */
--><div>
                <div data-role="spinner" data-component="product_form.product_form" class="admin__form-loading-mask" style="display: none;">
                    <div class="spinner">
                        <span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span>
                    </div>
                </div>
                <div data-bind="scope: 'product_form.product_form'" class="entry-edit form-inline">
                    <!-- ko template: getTemplate() -->
                    <!-- ko foreach: {data: elems, as: 'element'} -->
                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="product-details">
                        <!-- ko if: label --><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="status">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="E2O1NNB">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[WEBSITE]">Enable Product</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <div class="admin__actions-switch" data-role="switcher">
                                            <input type="checkbox" class="admin__actions-switch-checkbox" data-bind="attr: {id: uid, name: inputName}, value: value, disable: disabled, hasFocus: focused, simpleChecked: checked" id="E2O1NNB" name="product[status]" value="1">
                                            <label class="admin__actions-switch-label" data-bind="attr: {for: uid}" for="E2O1NNB">
                                                <span class="admin__actions-switch-text" data-bind="attr: {'data-text-on': toggleLabels.on, 'data-text-off': toggleLabels.off}" data-text-on="Yes" data-text-off="No"></span>
                                            </label>
                                        </div>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="attribute_set_id">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="PRR8A4C">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label">Attribute Set</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->

                                        <!-- ko ifnot: disableLabel --><!-- /ko -->
                                        <div class="admin__action-multiselect-wrap action-select-wrap" tabindex="0" data-bind="
        attr: {
            id: uid
        },
        css: {
            _active: listVisible,
            'admin__action-multiselect-tree': isTree()
        },
        event: {
            focusin: onFocusIn,
            focusout: onFocusOut,
            keydown: keydownSwitcher
        },
        outerClick: outerClick.bind($data)
" id="PRR8A4C">
                                            <!-- ko ifnot: chipsEnabled -->
                                            <div class="action-select admin__action-multiselect" data-role="advanced-select" data-bind="
            css: {_active: listVisible},
            click: function(data, event) {
                toggleListVisible(data, event)
            }
    ">
                                                <!-- ko ifnot: validationLoading --><div class="admin__action-multiselect-text" data-role="selected-option" data-bind="
         css: {warning: warn().length},
         text: setCaption()
    ">Default</div><!-- /ko -->
                                                <!-- ko if: isRemoveSelectedIcon && hasData() || !validationLoading --><!-- /ko -->
                                                <!-- ko if: validationLoading --><!-- /ko -->
                                            </div>
                                            <!-- /ko -->
                                            <!-- ko if: chipsEnabled --><!-- /ko -->
                                            <div class="action-menu" data-bind="css: { _active: listVisible}">
                                                <!-- ko if: loading --><!-- /ko -->
                                                <!-- ko if: filterOptions -->
                                                <div class="admin__action-multiselect-search-wrap">
                                                    <input class="admin__control-text admin__action-multiselect-search" data-role="advanced-select-text" type="text" data-bind="
                event: {
                    keydown: filterOptionsKeydown
                },
                attr: {
                    id: uid+2,
                    placeholder: filterPlaceholder
                },
                valueUpdate: 'afterkeydown',
                value: filterInputValue,
                hasFocus: filterOptionsFocus
                " id="PRR8A4C2" placeholder="">
                                                    <label class="admin__action-multiselect-search-label" data-action="advanced-select-search" data-bind="attr: {for: uid+2}
            " for="PRR8A4C2">
                                                    </label>
                                                    <!-- ko if: itemsQuantity --><!-- /ko -->
                                                </div>
                                                <!-- ko ifnot: options().length --><!-- /ko -->
                                                <!-- /ko -->
                                                <ul class="admin__action-multiselect-menu-inner _root" data-bind="
                event: {
                    scroll: function(data, event){onScrollDown(data, event)}
                }
            ">
                                                    <!-- ko foreach: { data: options, as: 'option'}  -->
                                                    <li class="admin__action-multiselect-menu-inner-item _root" data-bind="css: { _parent: $data.optgroup }" data-role="option-group">
                                                        <div class="action-menu-item _last" data-bind="
                        css: {
                            _selected: $parent.isSelectedValue(option),
                            _hover: $parent.isHovered(option, $element),
                            _expended: $parent.getLevelVisibility($data) &amp;&amp; $parent.showLevels($data),
                            _unclickable: $parent.isLabelDecoration($data),
                            _last: $parent.addLastElement($data),
                            '_with-checkbox': $parent.showCheckbox
                        },
                        click: function(data, event){
                            $parent.toggleOptionSelected($data, $index(), event);
                        },
                        clickBubble: false
                ">
                                                            <!-- ko if: $data.optgroup && $parent.showOpenLevelsActionIcon--><!-- /ko-->
                                                            <!--ko if: $parent.showCheckbox--><!-- /ko-->
                                                            <label class="admin__action-multiselect-label">
                                                                <span data-bind="text: option.label">Default</span>
                                                                <!-- ko if: $parent.getPath(option) --><!-- /ko -->
                                                            </label>
                                                        </div>
                                                        <!-- ko if: $data.optgroup --><!-- /ko-->
                                                    </li>
                                                    <!-- /ko -->
                                                </ul>
                                                <!-- ko if: $data.closeBtn --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field _required" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="name">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="QNEYRVE">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[STORE VIEW]">Product Name</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[name]" aria-describedby="notice-QNEYRVE" id="QNEYRVE" maxlength="255">
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field _required" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="sku">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="JGLJ6L3">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[GLOBAL]">SKU</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[sku]" aria-describedby="notice-JGLJ6L3" id="JGLJ6L3" maxlength="255">
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <fieldset class="admin__field" data-bind="css: {_required: required}, attr: {'data-index': index}, visible: visible" data-index="container_price">
                                    <!-- ko if: showLabel --><legend class="admin__field-label">
                                        <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label"></span>
                                    </legend><!-- /ko -->

                                    <div class="admin__field-control admin__control-grouped" data-bind="css: $data.additionalClasses">
                                        <!-- ko foreach: elems -->
                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field admin__field-small _required" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="price">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="S3HS8AU">
                                                    <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[GLOBAL]">Price</span>
                                                </label><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><div class="admin__control-addon">
                                                    <!-- ko template: elementTmpl -->
                                                    <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[price]" aria-describedby="notice-S3HS8AU" id="S3HS8AU" maxlength="255">
                                                    <!-- /ko -->

                                                    <!-- ko if: $data.addbefore --><label class="admin__addon-prefix" data-bind="attr: {for: uid}" for="S3HS8AU">
                                                        <span data-bind="text: addbefore">€</span>
                                                    </label><!-- /ko -->
                                                    <!-- ko if: $data.addafter --><!-- /ko -->
                                                </div><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: !$data.additionalForGroup --><!-- /ko -->
                                        <!-- /ko -->

                                        <!-- ko foreach: getRegion('insideGroup') --><!-- /ko -->

                                        <!-- ko if: validateWholeGroup --><!-- /ko -->
                                    </div>

                                    <!-- ko foreach: getRegion('outsideGroup') --><!-- ko template: getTemplate() -->
                                    <div class="admin__field admin__field-group-additional" data-bind="css: $data.additionalClasses, visible: visible">
                                        <!-- ko if: $data.label --><!-- /ko -->

                                        <div class="admin__field-control">
                                            <!-- ko template: elementTmpl -->
                                            <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-additional" data-index="advanced_pricing_button">
                                                <span data-bind="text: title">Advanced Pricing</span>
                                            </button>

                                            <!-- ko if: childError --><!-- /ko -->
                                            <!-- /ko -->
                                        </div>
                                    </div>
                                    <!-- /ko --><!-- /ko -->
                                </fieldset>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="tax_class_id">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="WCJU0FN">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[WEBSITE]">Tax Class</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <select class="admin__control-select" data-bind="
    attr: {
        name: inputName,
        id: uid,
        disabled: disabled,
        'aria-describedby': noticeId
    },
    hasFocus: focused,
    optgroup: options,
    value: value,
    optionsCaption: caption,
    optionsValue: 'value',
    optionsText: 'label'" name="product[tax_class_id]" id="WCJU0FN" aria-describedby="notice-WCJU0FN"><option data-title="None" value="0">None</option><option data-title="Taxable Goods" value="2">Taxable Goods</option><option data-title="Refund Adjustments" value="4">Refund Adjustments</option><option data-title="Gift Options" value="5">Gift Options</option><option data-title="Order Gift Wrapping" value="6">Order Gift Wrapping</option><option data-title="Item Gift Wrapping" value="7">Item Gift Wrapping</option><option data-title="Printed Gift Card" value="8">Printed Gift Card</option><option data-title="Reward Points" value="9">Reward Points</option></select>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <fieldset class="admin__field" data-bind="css: {_required: required}, attr: {'data-index': index}, visible: visible" data-index="quantity_and_stock_status_qty">
                                    <!-- ko if: showLabel --><legend class="admin__field-label">
                                        <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label"></span>
                                    </legend><!-- /ko -->

                                    <div class="admin__field-control admin__control-grouped" data-bind="css: $data.additionalClasses">
                                        <!-- ko foreach: elems -->
                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field admin__field-small" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="qty">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="G2LELK2">
                                                    <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[GLOBAL]">Quantity</span>
                                                </label><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                                <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[quantity_and_stock_status][qty]" aria-describedby="notice-G2LELK2" id="G2LELK2" maxlength="255">
                                                <!-- /ko --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: !$data.additionalForGroup --><!-- /ko -->
                                        <!-- /ko -->

                                        <!-- ko foreach: getRegion('insideGroup') --><!-- /ko -->

                                        <!-- ko if: validateWholeGroup --><!-- /ko -->
                                    </div>

                                    <!-- ko foreach: getRegion('outsideGroup') --><!-- ko template: getTemplate() -->
                                    <div class="admin__field admin__field-group-additional" data-bind="css: $data.additionalClasses, visible: visible">
                                        <!-- ko if: $data.label --><!-- /ko -->

                                        <div class="admin__field-control">
                                            <!-- ko template: elementTmpl -->
                                            <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-additional" data-index="advanced_inventory_button">
                                                <span data-bind="text: title">Advanced Inventory</span>
                                            </button>

                                            <!-- ko if: childError --><!-- /ko -->
                                            <!-- /ko -->
                                        </div>
                                    </div>
                                    <!-- /ko --><!-- /ko -->
                                </fieldset>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="quantity_and_stock_status">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="UXDY4UN">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[GLOBAL]">Stock Status</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <select class="admin__control-select" data-bind="
    attr: {
        name: inputName,
        id: uid,
        disabled: disabled,
        'aria-describedby': noticeId
    },
    hasFocus: focused,
    optgroup: options,
    value: value,
    optionsCaption: caption,
    optionsValue: 'value',
    optionsText: 'label'" name="product[quantity_and_stock_status][is_in_stock]" id="UXDY4UN" aria-describedby="notice-UXDY4UN"><option data-title="In Stock" value="1">In Stock</option><option data-title="Out of Stock" value="0">Out of Stock</option></select>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <fieldset class="admin__field" data-bind="css: {_required: required}, attr: {'data-index': index}, visible: visible" data-index="container_weight">
                                    <!-- ko if: showLabel --><legend class="admin__field-label">
                                        <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label"></span>
                                    </legend><!-- /ko -->

                                    <div class="admin__field-control admin__control-grouped" data-bind="css: $data.additionalClasses">
                                        <!-- ko foreach: elems -->
                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field admin__field-small" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="weight">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="I62FJ14">
                                                    <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[GLOBAL]">Weight</span>
                                                </label><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><div class="admin__control-addon">
                                                    <!-- ko template: elementTmpl -->
                                                    <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[weight]" aria-describedby="notice-I62FJ14" id="I62FJ14" maxlength="255">
                                                    <!-- /ko -->

                                                    <!-- ko if: $data.addbefore --><!-- /ko -->
                                                    <!-- ko if: $data.addafter --><label class="admin__addon-suffix" data-bind="attr: {for: uid}" for="I62FJ14">
                                                        <span data-bind="text: addafter">lbs</span>
                                                    </label><!-- /ko -->
                                                </div><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="product_has_weight">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                                <select class="admin__control-select" data-bind="
    attr: {
        name: inputName,
        id: uid,
        disabled: disabled,
        'aria-describedby': noticeId
    },
    hasFocus: focused,
    optgroup: options,
    value: value,
    optionsCaption: caption,
    optionsValue: 'value',
    optionsText: 'label'" name="product[product_has_weight]" id="E48V3VU" aria-describedby="notice-E48V3VU"><option data-title="This item has weight" value="1">This item has weight</option><option data-title="This item has no weight" value="0">This item has no weight</option></select>
                                                <!-- /ko --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->
                                        <!-- /ko -->

                                        <!-- ko foreach: getRegion('insideGroup') --><!-- /ko -->

                                        <!-- ko if: validateWholeGroup --><!-- /ko -->
                                    </div>

                                    <!-- ko foreach: getRegion('outsideGroup') --><!-- /ko -->
                                </fieldset>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <fieldset class="admin__field" data-bind="css: {_required: required}, attr: {'data-index': index}, visible: visible" data-index="container_category_ids">
                                    <!-- ko if: showLabel --><legend class="admin__field-label">
                                        <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label"></span>
                                    </legend><!-- /ko -->

                                    <div class="admin__field-control admin__control-grouped" data-bind="css: $data.additionalClasses">
                                        <!-- ko foreach: elems -->
                                        <!-- ko if: !$data.additionalForGroup --><!-- /ko -->

                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="category_ids">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="LP9NXQG">
                                                    <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[GLOBAL]">Categories</span>
                                                </label><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->

                                                <!-- ko ifnot: disableLabel --><!-- /ko -->
                                                <div class="admin__action-multiselect-wrap action-select-wrap admin__action-multiselect-tree" tabindex="0" data-bind="
        attr: {
            id: uid
        },
        css: {
            _active: listVisible,
            'admin__action-multiselect-tree': isTree()
        },
        event: {
            focusin: onFocusIn,
            focusout: onFocusOut,
            keydown: keydownSwitcher
        },
        outerClick: outerClick.bind($data)
" id="LP9NXQG">
                                                    <!-- ko ifnot: chipsEnabled --><!-- /ko -->
                                                    <!-- ko if: chipsEnabled -->
                                                    <div class="action-select admin__action-multiselect" data-role="advanced-select" data-bind="
            css: {_active: listVisible},
            click: function(data, event) {
                toggleListVisible(data, event)
            }
    ">
                                                        <div class="admin__action-multiselect-text" data-bind="
                visible: !hasData(),
                i18n: selectedPlaceholders.defaultPlaceholder
        ">Select...</div>
                                                        <!-- ko foreach: { data: getSelected(), as: 'option'}  --><!-- /ko -->
                                                    </div>
                                                    <!-- /ko -->
                                                    <div class="action-menu" data-bind="css: { _active: listVisible}">
                                                        <!-- ko if: loading --><!-- /ko -->
                                                        <!-- ko if: filterOptions -->
                                                        <div class="admin__action-multiselect-search-wrap">
                                                            <input class="admin__control-text admin__action-multiselect-search" data-role="advanced-select-text" type="text" data-bind="
                event: {
                    keydown: filterOptionsKeydown
                },
                attr: {
                    id: uid+2,
                    placeholder: filterPlaceholder
                },
                valueUpdate: 'afterkeydown',
                value: filterInputValue,
                hasFocus: filterOptionsFocus
                " id="LP9NXQG2" placeholder="">
                                                            <label class="admin__action-multiselect-search-label" data-action="advanced-select-search" data-bind="attr: {for: uid+2}
            " for="LP9NXQG2">
                                                            </label>
                                                            <!-- ko if: itemsQuantity --><!-- /ko -->
                                                        </div>
                                                        <!-- ko ifnot: options().length --><!-- /ko -->
                                                        <!-- /ko -->
                                                        <ul class="admin__action-multiselect-menu-inner _root" data-bind="
                event: {
                    scroll: function(data, event){onScrollDown(data, event)}
                }
            ">
                                                            <!-- ko foreach: { data: options, as: 'option'}  -->
                                                            <li class="admin__action-multiselect-menu-inner-item _root _parent" data-bind="css: { _parent: $data.optgroup }" data-role="option-group">
                                                                <div class="action-menu-item _expended _with-checkbox" data-bind="
                        css: {
                            _selected: $parent.isSelectedValue(option),
                            _hover: $parent.isHovered(option, $element),
                            _expended: $parent.getLevelVisibility($data) &amp;&amp; $parent.showLevels($data),
                            _unclickable: $parent.isLabelDecoration($data),
                            _last: $parent.addLastElement($data),
                            '_with-checkbox': $parent.showCheckbox
                        },
                        click: function(data, event){
                            $parent.toggleOptionSelected($data, $index(), event);
                        },
                        clickBubble: false
                ">
                                                                    <!-- ko if: $data.optgroup && $parent.showOpenLevelsActionIcon-->
                                                                    <div class="admin__action-multiselect-dropdown" data-bind="
                            click: function(event){
                                $parent.showLevels($data);
                                $parent.openChildLevel($data, $element, event);
                            },
                            clickBubble: false
                         ">
                                                                    </div>
                                                                    <!-- /ko-->
                                                                    <!--ko if: $parent.showCheckbox-->
                                                                    <input class="admin__control-checkbox" type="checkbox" tabindex="-1" data-bind="attr: { 'checked': $parent.isSelected(option.value) }">
                                                                    <!-- /ko-->
                                                                    <label class="admin__action-multiselect-label">
                                                                        <span data-bind="text: option.label">Default Category</span>
                                                                        <!-- ko if: $parent.getPath(option) --><!-- /ko -->
                                                                    </label>
                                                                </div>
                                                                <!-- ko if: $data.optgroup -->
                                                                <!-- ko template: {name: $parent.optgroupTmpl, data: {root: $parent, current: $data}} -->
                                                                <ul class="admin__action-multiselect-menu-inner" data-bind="
    visible: $data.root.showLevels($data.current),
    attr: {
        'data-level': $data.current.level++
    }" data-level="3">
                                                                    <!-- ko if: $data.current.visible() || $data.current.isVisited  -->
                                                                    <!-- ko foreach: { data: $data.current.optgroup, as: 'option'}  -->
                                                                    <li class="admin__action-multiselect-menu-inner-item" data-bind="css: { _parent: $data.optgroup }">
                                                                        <div class="action-menu-item _last _with-checkbox" data-bind="
                css: {
                    _selected: $parent.root.isSelected(option.value),
                    _hover: $parent.root.isHovered(option, $element),
                    _expended: $parent.root.getLevelVisibility($data) || $data.visible,
                    _unclickable: $parent.root.isLabelDecoration($data),
                    _last: $parent.root.addLastElement($data),
                    '_with-checkbox': $parent.root.showCheckbox
                },
                click: function(data, event){
                    $parent.root.toggleOptionSelected($data, $index(), event);
                },
                clickBubble: false

            ">
                                                                            <!-- ko if: $data.optgroup && $parent.root.showOpenLevelsActionIcon--><!-- /ko-->
                                                                            <!--ko if: $parent.root.showCheckbox-->
                                                                            <input class="admin__control-checkbox" type="checkbox" tabindex="-1" data-bind="attr: { 'checked': $parent.root.isSelected(option.value) }">
                                                                            <!--/ko-->
                                                                            <label class="admin__action-multiselect-label" data-bind="text: option.label">New Products</label>
                                                                        </div>
                                                                        <!-- ko if: $data.optgroup --><!-- /ko-->
                                                                    </li>
                                                                    <!-- /ko -->
                                                                    <!-- /ko -->
                                                                </ul>
                                                                <!-- /ko -->
                                                                <!-- /ko-->
                                                            </li>
                                                            <!-- /ko -->
                                                        </ul>
                                                        <!-- ko if: $data.closeBtn -->
                                                        <div class="admin__action-multiselect-actions-wrap">
                                                            <button class="action-default" data-action="close-advanced-select" type="button" data-bind="click: outerClick">
                                                                <span data-bind="i18n: closeBtnLabel">Done</span>
                                                            </button>
                                                        </div>
                                                        <!-- /ko -->
                                                    </div>
                                                </div>
                                                <!-- /ko --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->
                                        <!-- /ko -->

                                        <!-- ko foreach: getRegion('insideGroup') --><!-- ko template: getTemplate() -->
                                        <div class="admin__field admin__field-group-additional admin__field-small" data-bind="css: $data.additionalClasses, visible: visible">
                                            <!-- ko if: $data.label --><!-- /ko -->

                                            <div class="admin__field-control">
                                                <!-- ko template: elementTmpl -->
                                                <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="create_category_button">
                                                    <span data-bind="text: title">New Category</span>
                                                </button>

                                                <!-- ko if: childError --><!-- /ko -->
                                                <!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: validateWholeGroup --><!-- /ko -->
                                    </div>

                                    <!-- ko foreach: getRegion('outsideGroup') --><!-- /ko -->
                                </fieldset>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="visibility">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="NTLMYR7">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[STORE VIEW]">Visibility</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <select class="admin__control-select" data-bind="
    attr: {
        name: inputName,
        id: uid,
        disabled: disabled,
        'aria-describedby': noticeId
    },
    hasFocus: focused,
    optgroup: options,
    value: value,
    optionsCaption: caption,
    optionsValue: 'value',
    optionsText: 'label'" name="product[visibility]" id="NTLMYR7" aria-describedby="notice-NTLMYR7"><option data-title="Not Visible Individually" value="1">Not Visible Individually</option><option data-title="Catalog" value="2">Catalog</option><option data-title="Search" value="3">Search</option><option data-title="Catalog, Search" value="4">Catalog, Search</option></select>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <fieldset class="admin__field" data-bind="css: {_required: required}, attr: {'data-index': index}, visible: visible" data-index="container_news_from_date">
                                    <!-- ko if: showLabel --><legend class="admin__field-label">
                                        <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label"></span>
                                    </legend><!-- /ko -->

                                    <div class="admin__field-control admin__control-grouped-date admin__control-grouped" data-bind="css: $data.additionalClasses">
                                        <!-- ko foreach: elems -->
                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field admin__field-date" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="news_from_date">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="SWDLYV8">
                                                    <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[WEBSITE]">Set Product as New From</span>
                                                </label><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                                <input class="admin__control-text _has-datepicker" type="text" data-bind="
    hasFocus: focused,
    datepicker: { storage: shiftedValue, options: options },
    valueUpdate: valueUpdate,
    attr: {
        value: shiftedValue,
        name: inputName,
        placeholder: placeholder,
        'aria-describedby': noticeId,
        disabled: disabled
    }" value="" name="product[news_from_date]" aria-describedby="notice-SWDLYV8" id="dp1742481902553" autocomplete="on"><button type="button" class="ui-datepicker-trigger v-middle"><span>Select Date</span></button>
                                                <!-- /ko --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: !$data.additionalForGroup --><!-- ko if: visible() -->
                                        <!-- ko if: element.input_type != 'checkbox' || element.input_type != 'radio' --><!-- ko template: $parent.fieldTemplate -->
                                        <div class="admin__field admin__field-date" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="news_to_date">
                                            <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                                <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="WF4VBWW">
                                                    <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label">To</span>
                                                </label><!-- /ko -->
                                            </div>
                                            <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                                <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                                <input class="admin__control-text _has-datepicker" type="text" data-bind="
    hasFocus: focused,
    datepicker: { storage: shiftedValue, options: options },
    valueUpdate: valueUpdate,
    attr: {
        value: shiftedValue,
        name: inputName,
        placeholder: placeholder,
        'aria-describedby': noticeId,
        disabled: disabled
    }" value="" name="product[news_to_date]" aria-describedby="notice-WF4VBWW" id="dp1742481902554" autocomplete="on"><button type="button" class="ui-datepicker-trigger v-middle"><span>Select Date</span></button>
                                                <!-- /ko --><!-- /ko -->

                                                <!-- ko if: hasAddons() --><!-- /ko -->

                                                <!-- ko if: $data.tooltip --><!-- /ko -->

                                                <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                                <!-- ko if: error --><!-- /ko -->

                                                <!-- ko if: $data.notice --><!-- /ko -->

                                                <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                                <!-- ko if: $data.hasService() --><!-- /ko -->
                                            </div>
                                        </div>
                                        <!-- /ko --><!-- /ko -->
                                        <!-- ko if: element.input_type == 'checkbox' || element.input_type == 'radio' --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->
                                        <!-- /ko -->

                                        <!-- ko foreach: getRegion('insideGroup') --><!-- /ko -->

                                        <!-- ko if: validateWholeGroup --><!-- /ko -->
                                    </div>

                                    <!-- ko foreach: getRegion('outsideGroup') --><!-- /ko -->
                                </fieldset>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="country_of_manufacture">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="YLJAQUS">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[WEBSITE]">Country of Manufacture</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <select class="admin__control-select" data-bind="
    attr: {
        name: inputName,
        id: uid,
        disabled: disabled,
        'aria-describedby': noticeId
    },
    hasFocus: focused,
    optgroup: options,
    value: value,
    optionsCaption: caption,
    optionsValue: 'value',
    optionsText: 'label'" name="product[country_of_manufacture]" id="YLJAQUS" aria-describedby="notice-YLJAQUS"><option value=""> </option><option data-title="Afghanistan" value="AF">Afghanistan</option><option data-title="Albania" value="AL">Albania</option><option data-title="Algeria" value="DZ">Algeria</option><option data-title="American Samoa" value="AS">American Samoa</option><option data-title="Andorra" value="AD">Andorra</option><option data-title="Angola" value="AO">Angola</option><option data-title="Anguilla" value="AI">Anguilla</option><option data-title="Antarctica" value="AQ">Antarctica</option><option data-title="Antigua &amp; Barbuda" value="AG">Antigua &amp; Barbuda</option><option data-title="Argentina" value="AR">Argentina</option><option data-title="Armenia" value="AM">Armenia</option><option data-title="Aruba" value="AW">Aruba</option><option data-title="Australia" value="AU">Australia</option><option data-title="Austria" value="AT">Austria</option><option data-title="Azerbaijan" value="AZ">Azerbaijan</option><option data-title="Bahamas" value="BS">Bahamas</option><option data-title="Bahrain" value="BH">Bahrain</option><option data-title="Bangladesh" value="BD">Bangladesh</option><option data-title="Barbados" value="BB">Barbados</option><option data-title="Belarus" value="BY">Belarus</option><option data-title="Belgium" value="BE">Belgium</option><option data-title="Belize" value="BZ">Belize</option><option data-title="Benin" value="BJ">Benin</option><option data-title="Bermuda" value="BM">Bermuda</option><option data-title="Bhutan" value="BT">Bhutan</option><option data-title="Bolivia" value="BO">Bolivia</option><option data-title="Bosnia &amp; Herzegovina" value="BA">Bosnia &amp; Herzegovina</option><option data-title="Botswana" value="BW">Botswana</option><option data-title="Bouvet Island" value="BV">Bouvet Island</option><option data-title="Brazil" value="BR">Brazil</option><option data-title="British Indian Ocean Territory" value="IO">British Indian Ocean Territory</option><option data-title="British Virgin Islands" value="VG">British Virgin Islands</option><option data-title="Brunei" value="BN">Brunei</option><option data-title="Bulgaria" value="BG">Bulgaria</option><option data-title="Burkina Faso" value="BF">Burkina Faso</option><option data-title="Burundi" value="BI">Burundi</option><option data-title="Cambodia" value="KH">Cambodia</option><option data-title="Cameroon" value="CM">Cameroon</option><option data-title="Canada" value="CA">Canada</option><option data-title="Cape Verde" value="CV">Cape Verde</option><option data-title="Caribbean Netherlands" value="BQ">Caribbean Netherlands</option><option data-title="Cayman Islands" value="KY">Cayman Islands</option><option data-title="Central African Republic" value="CF">Central African Republic</option><option data-title="Chad" value="TD">Chad</option><option data-title="Chile" value="CL">Chile</option><option data-title="China" value="CN">China</option><option data-title="Christmas Island" value="CX">Christmas Island</option><option data-title="Cocos (Keeling) Islands" value="CC">Cocos (Keeling) Islands</option><option data-title="Colombia" value="CO">Colombia</option><option data-title="Comoros" value="KM">Comoros</option><option data-title="Congo - Brazzaville" value="CG">Congo - Brazzaville</option><option data-title="Congo - Kinshasa" value="CD">Congo - Kinshasa</option><option data-title="Cook Islands" value="CK">Cook Islands</option><option data-title="Costa Rica" value="CR">Costa Rica</option><option data-title="Croatia" value="HR">Croatia</option><option data-title="Cuba" value="CU">Cuba</option><option data-title="Curaçao" value="CW">Curaçao</option><option data-title="Cyprus" value="CY">Cyprus</option><option data-title="Czechia" value="CZ">Czechia</option><option data-title="Côte d’Ivoire" value="CI">Côte d’Ivoire</option><option data-title="Denmark" value="DK">Denmark</option><option data-title="Djibouti" value="DJ">Djibouti</option><option data-title="Dominica" value="DM">Dominica</option><option data-title="Dominican Republic" value="DO">Dominican Republic</option><option data-title="Ecuador" value="EC">Ecuador</option><option data-title="Egypt" value="EG">Egypt</option><option data-title="El Salvador" value="SV">El Salvador</option><option data-title="Equatorial Guinea" value="GQ">Equatorial Guinea</option><option data-title="Eritrea" value="ER">Eritrea</option><option data-title="Estonia" value="EE">Estonia</option><option data-title="Eswatini" value="SZ">Eswatini</option><option data-title="Ethiopia" value="ET">Ethiopia</option><option data-title="Falkland Islands" value="FK">Falkland Islands</option><option data-title="Faroe Islands" value="FO">Faroe Islands</option><option data-title="Fiji" value="FJ">Fiji</option><option data-title="Finland" value="FI">Finland</option><option data-title="France" value="FR">France</option><option data-title="French Guiana" value="GF">French Guiana</option><option data-title="French Polynesia" value="PF">French Polynesia</option><option data-title="French Southern Territories" value="TF">French Southern Territories</option><option data-title="Gabon" value="GA">Gabon</option><option data-title="Gambia" value="GM">Gambia</option><option data-title="Georgia" value="GE">Georgia</option><option data-title="Germany" value="DE">Germany</option><option data-title="Ghana" value="GH">Ghana</option><option data-title="Gibraltar" value="GI">Gibraltar</option><option data-title="Greece" value="GR">Greece</option><option data-title="Greenland" value="GL">Greenland</option><option data-title="Grenada" value="GD">Grenada</option><option data-title="Guadeloupe" value="GP">Guadeloupe</option><option data-title="Guam" value="GU">Guam</option><option data-title="Guatemala" value="GT">Guatemala</option><option data-title="Guernsey" value="GG">Guernsey</option><option data-title="Guinea" value="GN">Guinea</option><option data-title="Guinea-Bissau" value="GW">Guinea-Bissau</option><option data-title="Guyana" value="GY">Guyana</option><option data-title="Haiti" value="HT">Haiti</option><option data-title="Heard &amp; McDonald Islands" value="HM">Heard &amp; McDonald Islands</option><option data-title="Honduras" value="HN">Honduras</option><option data-title="Hong Kong SAR China" value="HK">Hong Kong SAR China</option><option data-title="Hungary" value="HU">Hungary</option><option data-title="Iceland" value="IS">Iceland</option><option data-title="India" value="IN">India</option><option data-title="Indonesia" value="ID">Indonesia</option><option data-title="Iran" value="IR">Iran</option><option data-title="Iraq" value="IQ">Iraq</option><option data-title="Ireland" value="IE">Ireland</option><option data-title="Isle of Man" value="IM">Isle of Man</option><option data-title="Israel" value="IL">Israel</option><option data-title="Italy" value="IT">Italy</option><option data-title="Jamaica" value="JM">Jamaica</option><option data-title="Japan" value="JP">Japan</option><option data-title="Jersey" value="JE">Jersey</option><option data-title="Jordan" value="JO">Jordan</option><option data-title="Kazakhstan" value="KZ">Kazakhstan</option><option data-title="Kenya" value="KE">Kenya</option><option data-title="Kiribati" value="KI">Kiribati</option><option data-title="Kosovo" value="XK">Kosovo</option><option data-title="Kuwait" value="KW">Kuwait</option><option data-title="Kyrgyzstan" value="KG">Kyrgyzstan</option><option data-title="Laos" value="LA">Laos</option><option data-title="Latvia" value="LV">Latvia</option><option data-title="Lebanon" value="LB">Lebanon</option><option data-title="Lesotho" value="LS">Lesotho</option><option data-title="Liberia" value="LR">Liberia</option><option data-title="Libya" value="LY">Libya</option><option data-title="Liechtenstein" value="LI">Liechtenstein</option><option data-title="Lithuania" value="LT">Lithuania</option><option data-title="Luxembourg" value="LU">Luxembourg</option><option data-title="Macao SAR China" value="MO">Macao SAR China</option><option data-title="Madagascar" value="MG">Madagascar</option><option data-title="Malawi" value="MW">Malawi</option><option data-title="Malaysia" value="MY">Malaysia</option><option data-title="Maldives" value="MV">Maldives</option><option data-title="Mali" value="ML">Mali</option><option data-title="Malta" value="MT">Malta</option><option data-title="Marshall Islands" value="MH">Marshall Islands</option><option data-title="Martinique" value="MQ">Martinique</option><option data-title="Mauritania" value="MR">Mauritania</option><option data-title="Mauritius" value="MU">Mauritius</option><option data-title="Mayotte" value="YT">Mayotte</option><option data-title="Mexico" value="MX">Mexico</option><option data-title="Micronesia" value="FM">Micronesia</option><option data-title="Moldova" value="MD">Moldova</option><option data-title="Monaco" value="MC">Monaco</option><option data-title="Mongolia" value="MN">Mongolia</option><option data-title="Montenegro" value="ME">Montenegro</option><option data-title="Montserrat" value="MS">Montserrat</option><option data-title="Morocco" value="MA">Morocco</option><option data-title="Mozambique" value="MZ">Mozambique</option><option data-title="Myanmar (Burma)" value="MM">Myanmar (Burma)</option><option data-title="Namibia" value="NA">Namibia</option><option data-title="Nauru" value="NR">Nauru</option><option data-title="Nepal" value="NP">Nepal</option><option data-title="Netherlands" value="NL">Netherlands</option><option data-title="New Caledonia" value="NC">New Caledonia</option><option data-title="New Zealand" value="NZ">New Zealand</option><option data-title="Nicaragua" value="NI">Nicaragua</option><option data-title="Niger" value="NE">Niger</option><option data-title="Nigeria" value="NG">Nigeria</option><option data-title="Niue" value="NU">Niue</option><option data-title="Norfolk Island" value="NF">Norfolk Island</option><option data-title="North Korea" value="KP">North Korea</option><option data-title="North Macedonia" value="MK">North Macedonia</option><option data-title="Northern Mariana Islands" value="MP">Northern Mariana Islands</option><option data-title="Norway" value="NO">Norway</option><option data-title="Oman" value="OM">Oman</option><option data-title="Pakistan" value="PK">Pakistan</option><option data-title="Palau" value="PW">Palau</option><option data-title="Palestinian Territories" value="PS">Palestinian Territories</option><option data-title="Panama" value="PA">Panama</option><option data-title="Papua New Guinea" value="PG">Papua New Guinea</option><option data-title="Paraguay" value="PY">Paraguay</option><option data-title="Peru" value="PE">Peru</option><option data-title="Philippines" value="PH">Philippines</option><option data-title="Pitcairn Islands" value="PN">Pitcairn Islands</option><option data-title="Poland" value="PL">Poland</option><option data-title="Portugal" value="PT">Portugal</option><option data-title="Qatar" value="QA">Qatar</option><option data-title="Romania" value="RO">Romania</option><option data-title="Russia" value="RU">Russia</option><option data-title="Rwanda" value="RW">Rwanda</option><option data-title="Réunion" value="RE">Réunion</option><option data-title="Samoa" value="WS">Samoa</option><option data-title="San Marino" value="SM">San Marino</option><option data-title="Saudi Arabia" value="SA">Saudi Arabia</option><option data-title="Senegal" value="SN">Senegal</option><option data-title="Serbia" value="RS">Serbia</option><option data-title="Seychelles" value="SC">Seychelles</option><option data-title="Sierra Leone" value="SL">Sierra Leone</option><option data-title="Singapore" value="SG">Singapore</option><option data-title="Sint Maarten" value="SX">Sint Maarten</option><option data-title="Slovakia" value="SK">Slovakia</option><option data-title="Slovenia" value="SI">Slovenia</option><option data-title="Solomon Islands" value="SB">Solomon Islands</option><option data-title="Somalia" value="SO">Somalia</option><option data-title="South Africa" value="ZA">South Africa</option><option data-title="South Georgia &amp; South Sandwich Islands" value="GS">South Georgia &amp; South Sandwich Islands</option><option data-title="South Korea" value="KR">South Korea</option><option data-title="Spain" value="ES">Spain</option><option data-title="Sri Lanka" value="LK">Sri Lanka</option><option data-title="St. Barthélemy" value="BL">St. Barthélemy</option><option data-title="St. Helena" value="SH">St. Helena</option><option data-title="St. Kitts &amp; Nevis" value="KN">St. Kitts &amp; Nevis</option><option data-title="St. Lucia" value="LC">St. Lucia</option><option data-title="St. Martin" value="MF">St. Martin</option><option data-title="St. Pierre &amp; Miquelon" value="PM">St. Pierre &amp; Miquelon</option><option data-title="St. Vincent &amp; Grenadines" value="VC">St. Vincent &amp; Grenadines</option><option data-title="Sudan" value="SD">Sudan</option><option data-title="Suriname" value="SR">Suriname</option><option data-title="Svalbard &amp; Jan Mayen" value="SJ">Svalbard &amp; Jan Mayen</option><option data-title="Sweden" value="SE">Sweden</option><option data-title="Switzerland" value="CH">Switzerland</option><option data-title="Syria" value="SY">Syria</option><option data-title="São Tomé &amp; Príncipe" value="ST">São Tomé &amp; Príncipe</option><option data-title="Taiwan, Province of China" value="TW">Taiwan, Province of China</option><option data-title="Tajikistan" value="TJ">Tajikistan</option><option data-title="Tanzania" value="TZ">Tanzania</option><option data-title="Thailand" value="TH">Thailand</option><option data-title="Timor-Leste" value="TL">Timor-Leste</option><option data-title="Togo" value="TG">Togo</option><option data-title="Tokelau" value="TK">Tokelau</option><option data-title="Tonga" value="TO">Tonga</option><option data-title="Trinidad &amp; Tobago" value="TT">Trinidad &amp; Tobago</option><option data-title="Tunisia" value="TN">Tunisia</option><option data-title="Turkey" value="TR">Turkey</option><option data-title="Turkmenistan" value="TM">Turkmenistan</option><option data-title="Turks &amp; Caicos Islands" value="TC">Turks &amp; Caicos Islands</option><option data-title="Tuvalu" value="TV">Tuvalu</option><option data-title="U.S. Outlying Islands" value="UM">U.S. Outlying Islands</option><option data-title="U.S. Virgin Islands" value="VI">U.S. Virgin Islands</option><option data-title="Uganda" value="UG">Uganda</option><option data-title="Ukraine" value="UA">Ukraine</option><option data-title="United Arab Emirates" value="AE">United Arab Emirates</option><option data-title="United Kingdom" value="GB">United Kingdom</option><option data-title="United States" value="US">United States</option><option data-title="Uruguay" value="UY">Uruguay</option><option data-title="Uzbekistan" value="UZ">Uzbekistan</option><option data-title="Vanuatu" value="VU">Vanuatu</option><option data-title="Vatican City" value="VA">Vatican City</option><option data-title="Venezuela" value="VE">Venezuela</option><option data-title="Vietnam" value="VN">Vietnam</option><option data-title="Wallis &amp; Futuna" value="WF">Wallis &amp; Futuna</option><option data-title="Western Sahara" value="EH">Western Sahara</option><option data-title="Yemen" value="YE">Yemen</option><option data-title="Zambia" value="ZM">Zambia</option><option data-title="Zimbabwe" value="ZW">Zimbabwe</option><option data-title="Åland Islands" value="AX">Åland Islands</option></select>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper _show" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="sources" style="display: none;">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="open">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Sources</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _show" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                <div class="admin__field-complex admin__control-grouped" data-bind="css: $data.additionalClasses, attr: {'data-index': index}" data-index="assign_sources_container">

                                    <!-- ko if: label --><!-- /ko -->

                                    <div class="admin__field-complex-elements" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                        <!-- ko if: visible --><!-- ko template: elementTmpl -->
                                        <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="advanced_inventory_button">
                                            <span data-bind="text: title">Advanced Inventory</span>
                                        </button>

                                        <!-- ko if: childError --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->
                                        <!-- /ko --><!-- ko template: getTemplate() -->
                                        <!-- ko if: visible --><!-- ko template: elementTmpl -->
                                        <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="assign_sources_button">
                                            <span data-bind="text: title">Assign Sources</span>
                                        </button>

                                        <!-- ko if: childError --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->
                                        <!-- /ko --></div>

                                    <!-- ko if: $data.content --><!-- /ko -->

                                    <!-- ko if: $data.text --><!-- /ko -->
                                </div>
                                <!-- /ko --><!-- ko template: getTemplate() -->

                                <div class="admin__field admin__field-wide _empty _no-header" data-bind="css: $data.setClasses($data), attr: {'data-index': index}, disable: disabled, visible: visible" data-index="assigned_sources">
                                    <!-- ko if: $data.label --><!-- /ko -->

                                    <div class="admin__field-control" data-role="grid-wrapper">
                                        <div class="admin__control-table-pagination" data-bind="visible: !!$data.getRecordCount()" style="display: none;">
                                            <div class="admin__data-grid-pager-wrap">
                                                <select class="admin__control-select" data-bind="value:pageSize, event:{change: updatePageSize}">
                                                    <option value="5">5</option>
                                                    <option value="20" selected="selected">20</option>
                                                    <option value="30">30</option>
                                                    <option value="50">50</option>
                                                    <option value="100">100</option>
                                                    <option value="200">200</option>
                                                    <option value="500">500</option>
                                                </select>
                                                <label class="admin__control-support-text" data-bind="text: $t('per page')">per page</label>
                                                <div class="admin__data-grid-pager">
                                                    <button class="action-previous" type="button" data-bind="attr: {title: $t('Previous Page')}, click: previousPage, disable: isFirst()" title="Previous Page" disabled=""></button>
                                                    <input class="admin__control-text" type="number" data-bind="attr: {id: ++ko.uid}, value: currentPage" id="1">
                                                    <label class="admin__control-support-text" data-bind="attr: {for: ko.uid}, text: 'of ' + pages()" for="1">of 1</label>
                                                    <button class="action-next" type="button" data-bind="attr: {title: $t('Next Page')}, click: nextPage, disable: isLast()" title="Next Page" disabled=""></button>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="admin__control-table-wrapper">
                                            <!-- ko if: $data.showSpinner --><!-- /ko -->
                                            <table class="admin__dynamic-rows data-grid" data-role="grid">
                                                <!-- ko if: $data.columnsHeader --><!-- /ko -->

                                                <tbody>
                                                <!-- ko repeat: {foreach: elems, item: '$record'} --><!-- /ko -->
                                                </tbody>
                                            </table>
                                        </div>

                                        <!-- ko if: $data.addButton --><!-- /ko -->
                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- ko template: getTemplate() -->

                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="content">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Content</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="configurable">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Configurations</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                <div class="admin__field-complex" data-bind="css: $data.additionalClasses, attr: {'data-index': index}" data-index="configurable_products_button_set">

                                    <!-- ko if: label --><!-- /ko -->

                                    <div class="admin__field-complex-elements" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                        <!-- ko if: visible --><!-- /ko -->
                                        <!-- /ko --><!-- ko template: getTemplate() -->
                                        <!-- ko if: visible --><!-- ko template: elementTmpl -->
                                        <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="create_configurable_products_button">
                                            <span data-bind="text: title">Create Configurations</span>
                                        </button>

                                        <!-- ko if: childError --><!-- /ko -->
                                        <!-- /ko --><!-- /ko -->
                                        <!-- /ko --></div>

                                    <!-- ko if: $data.content --><div class="admin__field-complex-content" data-bind="html: $data.content">Configurable products allow customers to choose options (Ex: shirt color). You need to create a simple product for each configuration (Ex: a product for each color).</div><!-- /ko -->

                                    <!-- ko if: $data.text --><!-- /ko -->
                                </div>
                                <!-- /ko --><!-- ko template: getTemplate() -->

                                <div class="admin__field admin__field-wide _empty _no-header" data-bind="css: $data.setClasses($data), attr: {'data-index': index}, disable: disabled, visible: visible" data-index="configurable-matrix" style="display: none;">
                                    <!-- ko if: $data.label --><label class="admin__field-label" data-bind="attr: {for: $data.uid}">
                                        <span data-bind="i18n: $data.label">Current Variations</span>
                                    </label><!-- /ko -->

                                    <div class="admin__field-control" data-role="grid-wrapper">
                                        <div class="admin__control-table-pagination" data-bind="visible: !!$data.getRecordCount()" style="display: none;">
                                            <div class="admin__data-grid-pager-wrap">
                                                <select class="admin__control-select" data-bind="value:pageSize, event:{change: updatePageSize}">
                                                    <option value="5">5</option>
                                                    <option value="20" selected="selected">20</option>
                                                    <option value="30">30</option>
                                                    <option value="50">50</option>
                                                    <option value="100">100</option>
                                                    <option value="200">200</option>
                                                    <option value="500">500</option>
                                                </select>
                                                <label class="admin__control-support-text" data-bind="text: $t('per page')">per page</label>
                                                <div class="admin__data-grid-pager">
                                                    <button class="action-previous" type="button" data-bind="attr: {title: $t('Previous Page')}, click: previousPage, disable: isFirst()" title="Previous Page" disabled=""></button>
                                                    <input class="admin__control-text" type="number" data-bind="attr: {id: ++ko.uid}, value: currentPage" id="2">
                                                    <label class="admin__control-support-text" data-bind="attr: {for: ko.uid}, text: 'of ' + pages()" for="2">of 1</label>
                                                    <button class="action-next" type="button" data-bind="attr: {title: $t('Next Page')}, click: nextPage, disable: isLast()" title="Next Page" disabled=""></button>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="admin__control-table-wrapper">
                                            <!-- ko if: $data.showSpinner --><!-- /ko -->
                                            <table class="admin__dynamic-rows data-grid" data-role="grid">
                                                <!-- ko if: $data.columnsHeader --><!-- /ko -->

                                                <tbody>
                                                <!-- ko repeat: {foreach: elems, item: '$record'} --><!-- /ko -->
                                                </tbody>
                                            </table>
                                        </div>

                                        <!-- ko if: $data.addButton --><!-- /ko -->
                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="gallery">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Images And Videos</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                <div data-bind="css: $data.additionalClasses, html: content, visible: visible" class="admin__scope-old">
                                    <div class="row">
                                        <div class="add-video-button-container">
                                            <button id="add_video_button" title="Add Video" data-role="add-video-button" type="button" class="action-secondary" data-ui-id="widget-button-1">
                                                <span>Add Video</span>
                                            </button>
                                        </div>
                                    </div>

                                    <div id="media_gallery_content" class="gallery ui-sortable" data-parent-component="product_form.product_form.block_gallery.block_gallery" data-images="[]" data-types="{&quot;image&quot;:{&quot;code&quot;:&quot;image&quot;,&quot;value&quot;:null,&quot;label&quot;:&quot;Base&quot;,&quot;scope&quot;:&quot;[STORE VIEW]&quot;,&quot;name&quot;:&quot;product[image]&quot;},&quot;small_image&quot;:{&quot;code&quot;:&quot;small_image&quot;,&quot;value&quot;:null,&quot;label&quot;:&quot;Small&quot;,&quot;scope&quot;:&quot;[STORE VIEW]&quot;,&quot;name&quot;:&quot;product[small_image]&quot;},&quot;thumbnail&quot;:{&quot;code&quot;:&quot;thumbnail&quot;,&quot;value&quot;:null,&quot;label&quot;:&quot;Thumbnail&quot;,&quot;scope&quot;:&quot;[STORE VIEW]&quot;,&quot;name&quot;:&quot;product[thumbnail]&quot;},&quot;swatch_image&quot;:{&quot;code&quot;:&quot;swatch_image&quot;,&quot;value&quot;:null,&quot;label&quot;:&quot;Swatch&quot;,&quot;scope&quot;:&quot;[STORE VIEW]&quot;,&quot;name&quot;:&quot;product[swatch_image]&quot;}}">
                                        <div class="image image-placeholder">

                                            <div id="id_kSyDDzwqSbVOy9bEQaA4LW371WtxFKbB_Uploader" class="uploader">
                                                <div class="fileinput-button form-buttons button">
                                                    <span>Browse Files...</span>
                                                    <input id="fileupload" type="file" name="image" data-url="https://dev-xpipe.sindria.org/dashboard/catalog/product_gallery/upload/key/73ebe1762d377de24117052e73da14be4c1cb139e5d55018c0c518122245831c/" multiple="multiple">
                                                </div>
                                                <div class="clear"></div>
                                                <script id="id_kSyDDzwqSbVOy9bEQaA4LW371WtxFKbB_Uploader-template" type="text/x-magento-template" data-template="uploader">
                                                    <div id="<%- data.id %>" class="file-row">
                                                        <span class="file-info"><%- data.name %> (<%- data.size %>)</span>
                                                        <div class="progressbar-container">
                                                            <div class="progressbar upload-progress" style="width: 0%;"></div>
                                                        </div>
                                                        <div class="clear"></div>
                                                    </div>
                                                </script>
                                            </div>

                                            <div class="product-image-wrapper">
                                                <p class="image-placeholder-text">
                                                    Browse to find or drag image here                </p>
                                            </div>
                                        </div>
                                        <input name="product[image]" data-form-part="product_form" class="image-image" type="hidden" value="">
                                        <input name="product[small_image]" data-form-part="product_form" class="image-small_image" type="hidden" value="">
                                        <input name="product[thumbnail]" data-form-part="product_form" class="image-thumbnail" type="hidden" value="">
                                        <input name="product[swatch_image]" data-form-part="product_form" class="image-swatch_image" type="hidden" value="">
                                        <script id="media_gallery_content-template" data-template="image" type="text/x-magento-template">
                                            <div class="image item <% if (data.disabled == 1) { %>hidden-for-front<% } %>
                <% if (data.video_url) { %>video-item<% } %>"
                                                 data-role="image">
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][position]"
                                                       value="<%- data.position %>"
                                                       data-form-part="product_form"
                                                       class="position"/>
                                                <% if (data.media_type !== 'external-video') {%>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][media_type]"
                                                       data-form-part="product_form"
                                                       value="image"/>
                                                <% } else { %>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][media_type]"
                                                       value="<%- data.media_type %>"
                                                       data-form-part="product_form"/>
                                                <% } %>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][video_provider]"
                                                       value="<%- data.video_provider %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][file]"
                                                       value="<%- data.file %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][value_id]"
                                                       value="<%- data.value_id %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][label]"
                                                       value="<%- data.label %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][disabled]"
                                                       value="<%- data.disabled %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][removed]"
                                                       value="" class="is-removed"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][video_url]"
                                                       value="<%- data.video_url %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][video_title]"
                                                       value="<%- data.video_title %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][video_description]"
                                                       value="<%- data.video_description %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][video_metadata]"
                                                       value="<%- data.video_metadata %>"
                                                       data-form-part="product_form"/>
                                                <input type="hidden"
                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][role]"
                                                       value="<%- data.video_description %>"
                                                       data-form-part="product_form"/>

                                                <div class="product-image-wrapper">
                                                    <img class="product-image"
                                                         data-role="image-element"
                                                         src="<%- data.url %>"
                                                         alt="<%- data.label %>"/>

                                                    <div class="actions">
                                                        <div class="tooltip">
                        <span class="delete-tooltiptext">
                            Delete image in all store views                        </span>
                                                            <button type="button"
                                                                    class="action-remove"
                                                                    data-role="delete-button"
                                                                    title="<% if (data.media_type == 'external-video') {%>
                                Delete&#x20;video                            <%} else {%>
                                Delete&#x20;image                            <%}%>">
                        <span>
                            <% if (data.media_type == 'external-video') { %>
                            Delete video                            <% } else {%>
                            Delete image                            <%} %>
                        </span>
                                                            </button>
                                                        </div>
                                                        <div class="draggable-handle"></div>
                                                    </div>
                                                    <div class="image-fade"><span>Hidden</span></div>
                                                </div>

                                                <div class="item-description">
                                                    <% if (data.media_type !== 'external-video') {%>
                                                    <div class="item-title" data-role="img-title"><%- data.label %></div>
                                                    <div class="item-size">
                                                        <span data-role="image-dimens"></span>, <span data-role="image-size"><%- data.sizeLabel %></span>
                                                    </div>
                                                    <% } else { %>
                                                    <div class="item-title" data-role="img-title"><%- data.video_title %></div>
                                                    <% } %>
                                                </div>

                                                <ul class="item-roles" data-role="roles-labels">
                                                    <li data-role-code="image"
                                                        class="item-role item-role-image">
                                                        Base                    </li>
                                                    <li data-role-code="small_image"
                                                        class="item-role item-role-small_image">
                                                        Small                    </li>
                                                    <li data-role-code="thumbnail"
                                                        class="item-role item-role-thumbnail">
                                                        Thumbnail                    </li>
                                                    <li data-role-code="swatch_image"
                                                        class="item-role item-role-swatch_image">
                                                        Swatch                    </li>
                                                </ul>
                                            </div>
                                        </script>

                                        <script data-role="img-dialog-container-tmpl" type="text/x-magento-template">
                                            <div class="image-panel" data-role="dialog">
                                            </div>
                                        </script>

                                        <script data-role="img-dialog-tmpl" type="text/x-magento-template">
                                            <div class="image-panel-preview">
                                                <img src="<%- data.url %>" alt="<%- data.label %>" />
                                            </div>
                                            <div class="image-panel-controls">
                                                <strong class="image-name"><%- data.label %></strong>

                                                <fieldset class="admin__fieldset fieldset-image-panel">
                                                    <div class="admin__field field-image-description">
                                                        <label class="admin__field-label" for="image-description">
                                                            <span>Alt Text</span>
                                                        </label>

                                                        <div class="admin__field-control">
                            <textarea data-role="image-description"
                                      rows="3"
                                      class="admin__control-textarea"
                                      name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][label]"><%- data.label %></textarea>
                                                        </div>
                                                    </div>

                                                    <div class="admin__field field-image-role">
                                                        <label class="admin__field-label">
                                                            <span>Role</span>
                                                        </label>
                                                        <div class="admin__field-control">
                                                            <ul class="multiselect-alt">
                                                                <li class="item">
                                                                    <label>
                                                                        <input class="image-type"
                                                                               data-role="type-selector"
                                                                               data-form-part="product_form"
                                                                               type="checkbox"
                                                                               value="image"
                                                                        />
                                                                        Base                                    </label>
                                                                </li>
                                                                <li class="item">
                                                                    <label>
                                                                        <input class="image-type"
                                                                               data-role="type-selector"
                                                                               data-form-part="product_form"
                                                                               type="checkbox"
                                                                               value="small_image"
                                                                        />
                                                                        Small                                    </label>
                                                                </li>
                                                                <li class="item">
                                                                    <label>
                                                                        <input class="image-type"
                                                                               data-role="type-selector"
                                                                               data-form-part="product_form"
                                                                               type="checkbox"
                                                                               value="thumbnail"
                                                                        />
                                                                        Thumbnail                                    </label>
                                                                </li>
                                                                <li class="item">
                                                                    <label>
                                                                        <input class="image-type"
                                                                               data-role="type-selector"
                                                                               data-form-part="product_form"
                                                                               type="checkbox"
                                                                               value="swatch_image"
                                                                        />
                                                                        Swatch                                    </label>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>

                                                    <div class="admin__field admin__field-inline field-image-size" data-role="size">
                                                        <label class="admin__field-label">
                                                            <span>Image Size</span>
                                                        </label>
                                                        <div class="admin__field-value" data-message="&#x7B;size&#x7D;"></div>
                                                    </div>

                                                    <div class="admin__field admin__field-inline field-image-resolution" data-role="resolution">
                                                        <label class="admin__field-label">
                                                            <span>Image Resolution</span>
                                                        </label>
                                                        <div class="admin__field-value" data-message="&#x7B;width&#x7D;&#x5E;&#x7B;height&#x7D;&#x20;px"></div>
                                                    </div>

                                                    <div class="admin__field field-image-hide">
                                                        <div class="admin__field-control">
                                                            <div class="admin__field admin__field-option">
                                                                <input type="checkbox"
                                                                       id="hide-from-product-page"
                                                                       data-role="visibility-trigger"
                                                                       data-form-part="product_form"
                                                                       value="1"
                                                                       class="admin__control-checkbox"
                                                                       name="product&#x5B;media_gallery&#x5D;[images][<%- data.file_id %>][disabled]"
                                                                <% if (data.disabled == 1) { %>checked="checked"<% } %> />

                                                                <label for="hide-from-product-page" class="admin__field-label">
                                                                    Hide from Product Page                            </label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </fieldset>
                                            </div>
                                        </script>
                                        <div id="new_video_media_gallery_content558907849" style="display: none;">
                                            <div id="video-player-preview-location" class="video-player-sidebar">
                                                <div class="video-player-container"></div>
                                                <div class="video-information title">
                                                    <label>Title: </label><span></span>
                                                </div>
                                                <div class="video-information uploaded">
                                                    <label>Uploaded: </label><span></span>
                                                </div>
                                                <div class="video-information uploader">
                                                    <label>Uploader: </label><span></span>
                                                </div>
                                                <div class="video-information duration">
                                                    <label>Duration: </label><span></span>
                                                </div>
                                            </div>
                                        </div>
                                        <script type="text/javascript">var elemMGTZJ4Yj = document.querySelector('div#new_video_media_gallery_content558907849');
                                            if (elemMGTZJ4Yj) {
                                                elemMGTZJ4Yj.style.display = 'none';
                                            }</script>

                                        <script type="text/javascript">var elem6Qb3Vf2A = document.querySelector('div#new\u002Dvideo');
                                            if (elem6Qb3Vf2A) {
                                                elem6Qb3Vf2A.style.display = 'none';
                                            }</script></div>
                                    <script>jQuery('body').trigger('contentUpdated');</script></div>

                                <!-- ko if: showSpinner --><!-- /ko -->
                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="search-engine-optimization">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Search Engine Optimization</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="url_key">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="V7DHDNO">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[STORE VIEW]">URL Key</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[url_key]" aria-describedby="notice-V7DHDNO" id="V7DHDNO" maxlength="255">
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="meta_title">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="J5YSWDX">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[STORE VIEW]">Meta Title</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->
                                        <input class="admin__control-text" type="text" data-bind="
        event: {change: userChanges},
        value: value,
        hasFocus: focused,
        valueUpdate: valueUpdate,
        attr: {
            name: inputName,
            placeholder: placeholder,
            'aria-describedby': noticeId,
            id: uid,
            disabled: disabled,
            maxlength: 255
    }" name="product[meta_title]" aria-describedby="notice-J5YSWDX" id="J5YSWDX" maxlength="255">
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="meta_keyword">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="RUKOEPN">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[STORE VIEW]">Meta Keywords</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->

                                        <textarea class="admin__control-textarea" data-bind="
    value: value,
    valueUpdate: valueUpdate,
    hasFocus: focused,
    attr: {
        name: inputName,
        cols: cols,
        rows: rows,
        'aria-describedby': noticeId,
        placeholder: placeholder,
        id: uid,
        disabled: disabled
    }" name="product[meta_keyword]" cols="15" rows="2" aria-describedby="notice-RUKOEPN" id="RUKOEPN"></textarea>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <!-- ko foreach: {data: elems, as: 'element'} -->
                                <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                                <div class="admin__field" data-bind="css: $data.additionalClasses, attr: {'data-index': index}, visible: visible" data-index="meta_description">
                                    <div class="admin__field-label" data-bind="visible: $data.labelVisible">
                                        <!-- ko if: $data.label --><label data-bind="attr: {for: uid}" for="CMC3DV6">
                                            <span data-bind="attr: {'data-config-scope': $data.scopeLabel}, i18n: label" data-config-scope="[STORE VIEW]">Meta Description</span>
                                        </label><!-- /ko -->
                                    </div>
                                    <div class="admin__field-control" data-bind="css: {'_with-tooltip': $data.tooltip, '_with-reset': $data.showFallbackReset &amp;&amp; $data.isDifferedFromDefault}">
                                        <!-- ko ifnot: hasAddons() --><!-- ko template: elementTmpl -->

                                        <textarea class="admin__control-textarea" data-bind="
    value: value,
    valueUpdate: valueUpdate,
    hasFocus: focused,
    attr: {
        name: inputName,
        cols: cols,
        rows: rows,
        'aria-describedby': noticeId,
        placeholder: placeholder,
        id: uid,
        disabled: disabled
    }" name="product[meta_description]" cols="15" rows="2" aria-describedby="notice-CMC3DV6" id="CMC3DV6"></textarea>
                                        <!-- /ko --><!-- /ko -->

                                        <!-- ko if: hasAddons() --><!-- /ko -->

                                        <!-- ko if: $data.tooltip --><!-- /ko -->

                                        <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->

                                        <!-- ko if: error --><!-- /ko -->

                                        <!-- ko if: $data.notice --><div class="admin__field-note" data-bind="attr: {id: noticeId}" id="notice-CMC3DV6">
                                            <span data-bind="i18n: notice">Maximum 255 chars. Meta Description should optimally be between 150-160 characters</span>
                                        </div><!-- /ko -->

                                        <!-- ko if: $data.additionalInfo --><!-- /ko -->

                                        <!-- ko if: $data.hasService() --><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- /ko -->
                                <!-- /ko -->
                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="related">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Related Products, Up-Sells, and Cross-Sells</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                <div class="fieldset-wrapper admin__fieldset-section" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="related">
                                    <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="-1">

                                        <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="title">
                                            <span data-bind="i18n: label">Related Products</span>
                                            <!-- ko if: collapsible --><!-- /ko -->
                                        </strong>
                                    </div><!-- /ko -->

                                    <div class="admin__fieldset-wrapper-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                                        <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                            <div class="admin__field-complex" data-bind="css: $data.additionalClasses, attr: {'data-index': index}" data-index="button_set">

                                                <!-- ko if: label --><!-- /ko -->

                                                <div class="admin__field-complex-elements" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                                    <!-- ko if: visible --><!-- ko template: elementTmpl -->
                                                    <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="button_related">
                                                        <span data-bind="text: title">Add Related Products</span>
                                                    </button>

                                                    <!-- ko if: childError --><!-- /ko -->
                                                    <!-- /ko --><!-- /ko -->
                                                    <!-- /ko --></div>

                                                <!-- ko if: $data.content --><div class="admin__field-complex-content" data-bind="html: $data.content">Related products are shown to customers in addition to the item the customer is looking at.</div><!-- /ko -->

                                                <!-- ko if: $data.text --><!-- /ko -->
                                            </div>
                                            <!-- /ko --><!-- ko template: getTemplate() -->

                                            <!-- /ko --><!-- ko template: getTemplate() -->

                                            <div class="admin__field admin__field-wide _empty _no-header" data-bind="css: $data.setClasses($data), attr: {'data-index': index}, disable: disabled, visible: visible" data-index="related">
                                                <!-- ko if: $data.label --><!-- /ko -->

                                                <div class="admin__field-control" data-role="grid-wrapper">
                                                    <div class="admin__control-table-pagination" data-bind="visible: !!$data.getRecordCount()" style="display: none;">
                                                        <div class="admin__data-grid-pager-wrap">
                                                            <select class="admin__control-select" data-bind="value:pageSize, event:{change: updatePageSize}">
                                                                <option value="5">5</option>
                                                                <option value="20" selected="selected">20</option>
                                                                <option value="30">30</option>
                                                                <option value="50">50</option>
                                                                <option value="100">100</option>
                                                                <option value="200">200</option>
                                                                <option value="500">500</option>
                                                            </select>
                                                            <label class="admin__control-support-text" data-bind="text: $t('per page')">per page</label>
                                                            <div class="admin__data-grid-pager">
                                                                <button class="action-previous" type="button" data-bind="attr: {title: $t('Previous Page')}, click: previousPage, disable: isFirst()" title="Previous Page" disabled=""></button>
                                                                <input class="admin__control-text" type="number" data-bind="attr: {id: ++ko.uid}, value: currentPage" id="3">
                                                                <label class="admin__control-support-text" data-bind="attr: {for: ko.uid}, text: 'of ' + pages()" for="3">of 1</label>
                                                                <button class="action-next" type="button" data-bind="attr: {title: $t('Next Page')}, click: nextPage, disable: isLast()" title="Next Page" disabled=""></button>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="admin__control-table-wrapper">
                                                        <!-- ko if: $data.showSpinner --><!-- /ko -->
                                                        <table class="admin__dynamic-rows data-grid" data-role="grid">
                                                            <!-- ko if: $data.columnsHeader --><!-- /ko -->

                                                            <tbody>
                                                            <!-- ko repeat: {foreach: elems, item: '$record'} --><!-- /ko -->
                                                            </tbody>
                                                        </table>
                                                    </div>

                                                    <!-- ko if: $data.addButton --><!-- /ko -->
                                                    <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->
                                                </div>
                                            </div>
                                            <!-- /ko --></fieldset><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <div class="fieldset-wrapper admin__fieldset-section" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="upsell">
                                    <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="-1">

                                        <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="title">
                                            <span data-bind="i18n: label">Up-Sell Products</span>
                                            <!-- ko if: collapsible --><!-- /ko -->
                                        </strong>
                                    </div><!-- /ko -->

                                    <div class="admin__fieldset-wrapper-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                                        <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                            <div class="admin__field-complex" data-bind="css: $data.additionalClasses, attr: {'data-index': index}" data-index="button_set">

                                                <!-- ko if: label --><!-- /ko -->

                                                <div class="admin__field-complex-elements" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                                    <!-- ko if: visible --><!-- ko template: elementTmpl -->
                                                    <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="button_upsell">
                                                        <span data-bind="text: title">Add Up-Sell Products</span>
                                                    </button>

                                                    <!-- ko if: childError --><!-- /ko -->
                                                    <!-- /ko --><!-- /ko -->
                                                    <!-- /ko --></div>

                                                <!-- ko if: $data.content --><div class="admin__field-complex-content" data-bind="html: $data.content">An up-sell item is offered to the customer as a pricier or higher-quality alternative to the product the customer is looking at.</div><!-- /ko -->

                                                <!-- ko if: $data.text --><!-- /ko -->
                                            </div>
                                            <!-- /ko --><!-- ko template: getTemplate() -->

                                            <!-- /ko --><!-- ko template: getTemplate() -->

                                            <div class="admin__field admin__field-wide _empty _no-header" data-bind="css: $data.setClasses($data), attr: {'data-index': index}, disable: disabled, visible: visible" data-index="upsell">
                                                <!-- ko if: $data.label --><!-- /ko -->

                                                <div class="admin__field-control" data-role="grid-wrapper">
                                                    <div class="admin__control-table-pagination" data-bind="visible: !!$data.getRecordCount()" style="display: none;">
                                                        <div class="admin__data-grid-pager-wrap">
                                                            <select class="admin__control-select" data-bind="value:pageSize, event:{change: updatePageSize}">
                                                                <option value="5">5</option>
                                                                <option value="20" selected="selected">20</option>
                                                                <option value="30">30</option>
                                                                <option value="50">50</option>
                                                                <option value="100">100</option>
                                                                <option value="200">200</option>
                                                                <option value="500">500</option>
                                                            </select>
                                                            <label class="admin__control-support-text" data-bind="text: $t('per page')">per page</label>
                                                            <div class="admin__data-grid-pager">
                                                                <button class="action-previous" type="button" data-bind="attr: {title: $t('Previous Page')}, click: previousPage, disable: isFirst()" title="Previous Page" disabled=""></button>
                                                                <input class="admin__control-text" type="number" data-bind="attr: {id: ++ko.uid}, value: currentPage" id="4">
                                                                <label class="admin__control-support-text" data-bind="attr: {for: ko.uid}, text: 'of ' + pages()" for="4">of 1</label>
                                                                <button class="action-next" type="button" data-bind="attr: {title: $t('Next Page')}, click: nextPage, disable: isLast()" title="Next Page" disabled=""></button>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="admin__control-table-wrapper">
                                                        <!-- ko if: $data.showSpinner --><!-- /ko -->
                                                        <table class="admin__dynamic-rows data-grid" data-role="grid">
                                                            <!-- ko if: $data.columnsHeader --><!-- /ko -->

                                                            <tbody>
                                                            <!-- ko repeat: {foreach: elems, item: '$record'} --><!-- /ko -->
                                                            </tbody>
                                                        </table>
                                                    </div>

                                                    <!-- ko if: $data.addButton --><!-- /ko -->
                                                    <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->
                                                </div>
                                            </div>
                                            <!-- /ko --></fieldset><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --><!-- ko template: getTemplate() -->
                                <div class="fieldset-wrapper admin__fieldset-section" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="crosssell">
                                    <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="-1">

                                        <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="title">
                                            <span data-bind="i18n: label">Cross-Sell Products</span>
                                            <!-- ko if: collapsible --><!-- /ko -->
                                        </strong>
                                    </div><!-- /ko -->

                                    <div class="admin__fieldset-wrapper-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                                        <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                            <div class="admin__field-complex" data-bind="css: $data.additionalClasses, attr: {'data-index': index}" data-index="button_set">

                                                <!-- ko if: label --><!-- /ko -->

                                                <div class="admin__field-complex-elements" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->
                                                    <!-- ko if: visible --><!-- ko template: elementTmpl -->
                                                    <button type="button" data-bind="css: buttonClasses, attr: {'data-index': index}, click: action, disable: disabled" class="action-basic" data-index="button_crosssell">
                                                        <span data-bind="text: title">Add Cross-Sell Products</span>
                                                    </button>

                                                    <!-- ko if: childError --><!-- /ko -->
                                                    <!-- /ko --><!-- /ko -->
                                                    <!-- /ko --></div>

                                                <!-- ko if: $data.content --><div class="admin__field-complex-content" data-bind="html: $data.content">These "impulse-buy" products appear next to the shopping cart as cross-sells to the items already in the shopping cart.</div><!-- /ko -->

                                                <!-- ko if: $data.text --><!-- /ko -->
                                            </div>
                                            <!-- /ko --><!-- ko template: getTemplate() -->

                                            <!-- /ko --><!-- ko template: getTemplate() -->

                                            <div class="admin__field admin__field-wide _empty _no-header" data-bind="css: $data.setClasses($data), attr: {'data-index': index}, disable: disabled, visible: visible" data-index="crosssell">
                                                <!-- ko if: $data.label --><!-- /ko -->

                                                <div class="admin__field-control" data-role="grid-wrapper">
                                                    <div class="admin__control-table-pagination" data-bind="visible: !!$data.getRecordCount()" style="display: none;">
                                                        <div class="admin__data-grid-pager-wrap">
                                                            <select class="admin__control-select" data-bind="value:pageSize, event:{change: updatePageSize}">
                                                                <option value="5">5</option>
                                                                <option value="20" selected="selected">20</option>
                                                                <option value="30">30</option>
                                                                <option value="50">50</option>
                                                                <option value="100">100</option>
                                                                <option value="200">200</option>
                                                                <option value="500">500</option>
                                                            </select>
                                                            <label class="admin__control-support-text" data-bind="text: $t('per page')">per page</label>
                                                            <div class="admin__data-grid-pager">
                                                                <button class="action-previous" type="button" data-bind="attr: {title: $t('Previous Page')}, click: previousPage, disable: isFirst()" title="Previous Page" disabled=""></button>
                                                                <input class="admin__control-text" type="number" data-bind="attr: {id: ++ko.uid}, value: currentPage" id="5">
                                                                <label class="admin__control-support-text" data-bind="attr: {for: ko.uid}, text: 'of ' + pages()" for="5">of 1</label>
                                                                <button class="action-next" type="button" data-bind="attr: {title: $t('Next Page')}, click: nextPage, disable: isLast()" title="Next Page" disabled=""></button>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class="admin__control-table-wrapper">
                                                        <!-- ko if: $data.showSpinner --><!-- /ko -->
                                                        <table class="admin__dynamic-rows data-grid" data-role="grid">
                                                            <!-- ko if: $data.columnsHeader --><!-- /ko -->

                                                            <tbody>
                                                            <!-- ko repeat: {foreach: elems, item: '$record'} --><!-- /ko -->
                                                            </tbody>
                                                        </table>
                                                    </div>

                                                    <!-- ko if: $data.addButton --><!-- /ko -->
                                                    <!-- ko if: $data.showFallbackReset && $data.isDifferedFromDefault --><!-- /ko -->
                                                </div>
                                            </div>
                                            <!-- /ko --></fieldset><!-- /ko -->
                                    </div>
                                </div>
                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="custom_options">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Customizable Options</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__fieldset-product-websites admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="websites">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Product in Websites</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="salable_quantity" style="display: none;">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Product Salable Quantity</span>
                                <div class="admin__field-saleable-qty admin__field-tooltip">
                                    <a class="admin__field-tooltip-action action-help" target="_blank" tabindex="1" data-bind="attr: {href: tooltip.link}"></a>
                                    <div class="admin__field-tooltip-content" data-bind="text: tooltip.description">Aggregated inventory available to purchase for a stock. The amount aggregates assigned source's Quantity subtracting the Out-of-Stock Threshold (or MinQty).</div>
                                </div>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="design">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Design</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="schedule-design-update">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Schedule Design Update</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="gift-options">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Gift Options</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper admin__collapsible-block-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="downloadable">
                        <!-- ko if: label --><div class="fieldset-wrapper-title" data-bind="attr: {tabindex: !collapsible ? -1 : 0,
               'data-state-collapsible': collapsible ? opened() ? 'open' : 'closed' : null}, click: toggleOpened, keyboard: {13: toggleOpened}" tabindex="0" data-state-collapsible="closed">

                            <strong data-bind="css: {'admin__collapsible-title': collapsible,
                      title: !collapsible,
                      '_changed': changed,
                      '_loading': loading,
                      '_error': error}" class="admin__collapsible-title">
                                <span data-bind="i18n: label">Downloadable Information</span>
                                <!-- ko if: collapsible --><span class="admin__page-nav-item-messages">
                <span class="admin__page-nav-item-message _changed">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'Changes have been made to this section that have not been saved.'">Changes have been made to this section that have not been saved.</span>
                </span>
                <span class="admin__page-nav-item-message _error">
                    <span class="admin__page-nav-item-message-icon"></span>
                    <span class="admin__page-nav-item-message-tooltip" data-bind="i18n: 'This tab contains invalid data. Please resolve this before saving.'">This tab contains invalid data. Please resolve this before saving.</span>
                </span>
                <span class="admin__page-nav-item-message-loader">
                    <span class="spinner">
                       <!-- ko repeat: 8 --><span data-repeat-index="0"></span><span data-repeat-index="1"></span><span data-repeat-index="2"></span><span data-repeat-index="3"></span><span data-repeat-index="4"></span><span data-repeat-index="5"></span><span data-repeat-index="6"></span><span data-repeat-index="7"></span><!-- /ko -->
                    </span>
               </span>
            </span><!-- /ko -->
                            </strong>
                        </div><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content admin__collapsible-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
                    <div class="fieldset-wrapper" data-bind="visible: $data.visible === undefined ? true: $data.visible, css: $data.additionalClasses, attr: {'data-level': $data.level, 'data-index': index}" data-level="0" data-index="product.form.configurable.matrix.content">
                        <!-- ko if: label --><!-- /ko -->

                        <div class="admin__fieldset-wrapper-content _hide" data-bind="css: {'admin__collapsible-content': collapsible, '_show': opened, '_hide': !opened()}">
                            <!-- ko if: opened() || _wasOpened || initializeFieldsetDataByDefault --><fieldset class="admin__fieldset" data-bind="foreach: {data: elems, as: 'element'}"><!-- ko template: getTemplate() -->

                                <div data-bind="css: $data.additionalClasses, html: content, visible: visible" class="admin__scope-old"><div class="productFormConfigurable" data-role="step-wizard-dialog" data-bind="scope: 'product_form.product_form.configurableModal'">
                                        <!-- ko template: getTemplate() -->

                                        <!-- /ko -->
                                    </div>
                                    <div class="productFormConfigurable" id="product-variations-matrix" data-role="product-variations-matrix">
                                        <div data-bind="scope: 'configurableVariations'"></div>
                                    </div>

                                    <script>    require(['jquery', 'mage/apply/main'], function ($, main) {
                                            main.apply();
                                            $('.productFormConfigurable[data-role=step-wizard-dialog]').applyBindings();
                                            $('.productFormConfigurable[data-role=product-variations-matrix]').applyBindings();
                                        })</script></div>

                                <!-- ko if: showSpinner --><!-- /ko -->
                                <!-- /ko --></fieldset><!-- /ko -->
                        </div>
                    </div>
                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->

                    <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

                    <!-- /ko --><!-- /ko -->
                    <!-- /ko -->
                    <!-- /ko -->
                </div>
            </div>

        </div></div></div>
