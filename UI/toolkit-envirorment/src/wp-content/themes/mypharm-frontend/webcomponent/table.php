<main id="anchor-content" class="page-content"><div id="page:main-container" class="page-columns"><div class="admin__old"><div id="container" class="main-col">
                <div id="notificationGrid" data-grid-id="notificationGrid">

                    <div class="admin__data-grid-header admin__data-grid-toolbar">
                        <div class="admin__data-grid-header-row">

                        </div>
                        <div class="_massaction admin__data-grid-header-row">
                            <div id="notificationGrid_massaction" class="admin__grid-massaction">

                                <form action="" id="notificationGrid_massaction-form" method="post" novalidate="novalidate">
                                    <div class="admin__grid-massaction-form">
                                        <div><input name="form_key" type="hidden" value="DcvntOaPxglthznp"></div>
                                        <select id="notificationGrid_massaction-select" class="required-entry local-validation admin__control-select " data-ui-id="adminhtml-block-notification-massactions-select">
                                            <option class="admin__control-select-placeholder" value="" selected="">
                                                Actions</option>
                                            <option value="mark_as_read">
                                                Mark as Read                    </option>
                                            <option value="remove">
                                                Remove                    </option>
                                        </select>
                                        <span class="outer-span" id="notificationGrid_massaction-form-hiddens"></span>
                                        <span class="outer-span" id="notificationGrid_massaction-form-additional"></span>
                                        <button id="id_XW2leTpFl9QLy5iofk9lBj5zQrofEthf" title="Submit" type="button" class="action-default scalable" backend-button-widget-hook-id="buttonId4YOzOGLDC3" data-ui-id="widget-button-2">
                                            <span>Submit</span>
                                        </button>
                                        <script type="text/javascript">    function eventListenerUAfPf9w1xd () {
                                                notificationGrid_massactionJsObject.apply();
                                            }
                                            var listenedElementUAfPf9w1xdArray = document.querySelectorAll("*[backend-button-widget-hook-id='buttonId4YOzOGLDC3']");
                                            if(listenedElementUAfPf9w1xdArray.length !== 'undefined'){
                                                listenedElementUAfPf9w1xdArray.forEach(function(element) {
                                                    if (element) {
                                                        element.onclick = function (event) {
                                                            var targetElement = element;
                                                            if (event && event.target) {
                                                                targetElement = event.target;
                                                            }
                                                            eventListenerUAfPf9w1xd.apply(targetElement);
                                                        };
                                                    }
                                                });
                                            }</script>        </div>
                                </form>
                                <div class="no-display">
                                    <div id="notificationGrid_massaction-item-mark_as_read-block">
                                    </div>
                                    <div id="notificationGrid_massaction-item-remove-block">
                                    </div>
                                </div>

                                <div class="mass-select-wrap">
                                    <select id="notificationGrid_massaction-mass-select" class="action-select-multiselect" data-menu="grid-mass-select">
                                        <optgroup label="Mass Actions">
                                            <option disabled="" selected=""></option>
                                            <option value="selectAll">
                                                Select All                    </option>
                                            <option value="unselectAll">
                                                Unselect All                    </option>
                                            <option value="selectVisible">
                                                Select Visible                </option>
                                            <option value="unselectVisible">
                                                Unselect Visible                </option>
                                        </optgroup>
                                    </select>
                                    <label for="notificationGrid_massaction-mass-select"></label>
                                </div>

                                <script>    require(['jquery', 'domReady!'], function($){
                                        'use strict';$('#notificationGrid_massaction-mass-select')            .removeClass('_disabled')
                                            .prop('disabled', false)
                                            .change(function () {
                                                var massAction = $('option:selected', this).val();
                                                this.blur();
                                                switch (massAction) {
                                                    case 'selectAll':
                                                        return notificationGrid_massactionJsObject.selectAll();
                                                        break;
                                                    case 'unselectAll':
                                                        return notificationGrid_massactionJsObject.unselectAll();
                                                        break;
                                                    case 'selectVisible':
                                                        return  notificationGrid_massactionJsObject.selectVisible();
                                                        break;
                                                    case 'unselectVisible':
                                                        return notificationGrid_massactionJsObject.unselectVisible();
                                                        break;
                                                }
                                            });
                                    });</script></div>
                            <div class="admin__control-support-text">
                        <span id="notificationGrid-total-count" data-ui-id="adminhtml-notification-container-grid-total-count">
                            1                        </span>
                                records found                        <span id="notificationGrid_massaction-count" class="mass-select-info _empty"><strong data-role="counter">0</strong>
                            <span>selected</span>
                        </span>
                            </div>
                            <div class="admin__data-grid-pager-wrap">
                                <select name="limit" id="notificationGrid_page-limit" onchange="notificationGridJsObject.loadByElement(this)" data-ui-id="adminhtml-notification-container-grid-per-page" class="admin__control-select">
                                    <option value="20" selected="selected">20
                                    </option>
                                    <option value="30">30
                                    </option>
                                    <option value="50">50
                                    </option>
                                    <option value="100">100
                                    </option>
                                    <option value="200">200
                                    </option>
                                </select>
                                <label for="notificationGrid_page-limit" class="admin__control-support-text">per page</label>
                                <div class="admin__data-grid-pager">

                                    <button type="button" class="action-previous disabled">
                                        <span>Previous page</span>
                                    </button>

                                    <input type="text" id="notificationGrid_page-current" name="page" value="1" class="admin__control-text" data-ui-id="adminhtml-notification-container-grid-current-page">

                                    <script type="text/javascript">    function eventListenerke1GL1C4uU () {
                                            notificationGridJsObject.inputPage(event, '1');
                                        }
                                        var listenedElementke1GL1C4uUArray = document.querySelectorAll("#notificationGrid_page-current");
                                        if(listenedElementke1GL1C4uUArray.length !== 'undefined'){
                                            listenedElementke1GL1C4uUArray.forEach(function(element) {
                                                if (element) {
                                                    element.onkeypress = function (event) {
                                                        var targetElement = element;
                                                        if (event && event.target) {
                                                            targetElement = event.target;
                                                        }
                                                        eventListenerke1GL1C4uU.apply(targetElement);
                                                    };
                                                }
                                            });
                                        }</script>
                                    <label class="admin__control-support-text" for="notificationGrid_page-current">
                                        of <span>1</span>                            </label>
                                    <button type="button" class="action-next disabled">
                                        <span>Next page</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="admin__data-grid-wrap admin__data-grid-wrap-static">

                        <table class="data-grid" id="notificationGrid_table">
                            <!-- Rendering column set -->
                            <thead>
                            <tr>
                                <th class="data-grid-th" data-column="massaction">&nbsp;</th>
                                <th data-sort="severity" data-direction="asc" class="data-grid-th _sortable not-sort  col-severity"><span>Severity</span></th>                                                                                                    <th data-sort="date_added" data-direction="asc" class="data-grid-th _sortable _descend col-date col-date_added"><span>Date Added</span></th>                                                                                                    <th data-sort="title" data-direction="asc" class="data-grid-th _sortable not-sort  col-title"><span>Message</span></th>                                                                                                    <th class="data-grid-th last no-link col-actions"><span>Actions</span></th>                                                    </tr>
                            </thead>

                            <tbody>

                            <tr data-role="row" title="#" class="even">
                                <td data-column="massaction" class=" col-select col-massaction data-grid-checkbox-cell ">
                                    <label class="data-grid-checkbox-cell-inner" for="id_337"><input type="checkbox" name="notification" id="id_337" data-role="select-row" value="1" class="admin__control-checkbox"><label for="id_337"></label></label>                            </td>
                                <td data-column="severity" class=" col-severity  ">
                                    <span class="grid-severity-notice"><span>notice</span></span>                            </td>
                                <td data-column="date_added" class=" col-date col-date_added  ">
                                    Sep 6, 2024, 12:44:23 PM                            </td>
                                <td data-column="title" class=" col-title  ">
                                    <span class="grid-row-title">Disable Notice</span><br>To improve performance, collecting statistics for the Magento Report module is disabled by default.
                                    You can enable it in System Config.                            </td>
                                <td data-column="actions" class=" col-actions  last">
                                    <a class="action-delete" href="https://dev-xpipe.sindria.org/dashboard/xpipe/notification/remove/id/1/uenc/aHR0cHM6Ly9kZXYteHBpcGUuc2luZHJpYS5vcmcvZGFzaGJvYXJkL3hwaXBlL25vdGlmaWNhdGlvbi9pbmRleC9rZXkvNDZiMmRhMTRiZGY2M2YxMWNkYThjODdjMTZiYzQ2N2NlMTkwZmVjZGQ0OGUzYjRjZmEwZTE3ZTU3OTA0NDVlYi8%2C/key/04c74cc6c009d50045a7f4bc983541626ecb209780f1dbd69b3c42913f8c2c3a/" onclick="deleteConfirm('Are you sure?', this.href); return false;">Remove</a>                            </td>
                            </tr>
                            </tbody>

                        </table>


                    </div>
                </div>
                <script>var deps = [];
                    deps.push('mage/adminhtml/grid');

                    require(deps, function(){
                        notificationGridJsObject = new varienGrid('notificationGrid', 'https\u003A\u002F\u002Fdev\u002Dxpipe.sindria.org\u002Fdashboard\u002Fxpipe\u002Fnotification\u002Findex\u002Fkey\u002F46b2da14bdf63f11cda8c87c16bc467ce190fecdd48e3b4cfa0e17e5790445eb\u002F', 'page', 'sort', 'dir', 'filter');

                        notificationGridJsObject.useAjax = false;
                        notificationGridJsObject.rowClickCallback = openGridRow;
                        notificationGridJsObject.bindSortable();
                        notificationGrid_massactionJsObject = new varienGridMassaction('notificationGrid_massaction', notificationGridJsObject, '', 'internal_notification', 'notification');notificationGrid_massactionJsObject.setItems({"mark_as_read":{"label":"Mark as Read","url":"https:\/\/dev-xpipe.sindria.org\/dashboard\/xpipe\/notification\/massMarkAsRead\/key\/bcf280d9c904316ae213be442bbaf3a11d5fb45cabb114d5f969b20a1423374d\/","id":"mark_as_read"},"remove":{"label":"Remove","url":"https:\/\/dev-xpipe.sindria.org\/dashboard\/xpipe\/notification\/massRemove\/key\/18c7794fc9991e69cfd683dd3d0dbda20d723282319c0178480d682959022359\/","confirm":"Are you sure?","id":"remove"}}); notificationGrid_massactionJsObject.setGridIds('1');notificationGrid_massactionJsObject.setUseSelectAll(true);notificationGrid_massactionJsObject.errorText = 'An item needs to be selected. Select and try again.';
                        window.notificationGrid_massactionJsObject = notificationGrid_massactionJsObject;

                    });
                </script>


            </div></div></div></main>
