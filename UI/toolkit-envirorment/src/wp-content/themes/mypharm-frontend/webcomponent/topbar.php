<div class="page-main-actions">    <div class="store-switcher store-view">
        <span class="store-switcher-label">Scope:</span>
        <div class="actions dropdown closable">
            <input type="hidden" name="store_switcher" id="store_switcher" data-role="store-view-id" data-param="store" value="0" data-ui-id="store-switcher">
            <script type="text/javascript">    function eventListenerVXXieg8dgP () {
                    switchScope(this);;
                }
                var listenedElementVXXieg8dgPArray = document.querySelectorAll("#store_switcher");
                if(listenedElementVXXieg8dgPArray.length !== 'undefined'){
                    listenedElementVXXieg8dgPArray.forEach(function(element) {
                        if (element) {
                            element.onchange = function (event) {
                                var targetElement = element;
                                if (event && event.target) {
                                    targetElement = event.target;
                                }
                                eventListenerVXXieg8dgP.apply(targetElement);
                            };
                        }
                    });
                }</script>            <input type="hidden" name="store_group_switcher" id="store_group_switcher" data-role="store-group-id" data-param="group" value="0" data-ui-id="store-switcher">
            <script type="text/javascript">    function eventListener7xvIqKDbGX () {
                    switchScope(this);;
                }
                var listenedElement7xvIqKDbGXArray = document.querySelectorAll("#store_group_switcher");
                if(listenedElement7xvIqKDbGXArray.length !== 'undefined'){
                    listenedElement7xvIqKDbGXArray.forEach(function(element) {
                        if (element) {
                            element.onchange = function (event) {
                                var targetElement = element;
                                if (event && event.target) {
                                    targetElement = event.target;
                                }
                                eventListener7xvIqKDbGX.apply(targetElement);
                            };
                        }
                    });
                }</script>            <input type="hidden" name="website_switcher" id="website_switcher" data-role="website-id" data-param="website" value="0" data-ui-id="store-switcher">
            <script type="text/javascript">    function eventListenerJfkYh0FOP1 () {
                    switchScope(this);;
                }
                var listenedElementJfkYh0FOP1Array = document.querySelectorAll("#website_switcher");
                if(listenedElementJfkYh0FOP1Array.length !== 'undefined'){
                    listenedElementJfkYh0FOP1Array.forEach(function(element) {
                        if (element) {
                            element.onchange = function (event) {
                                var targetElement = element;
                                if (event && event.target) {
                                    targetElement = event.target;
                                }
                                eventListenerJfkYh0FOP1.apply(targetElement);
                            };
                        }
                    });
                }</script>            <button type="button" class="admin__action-dropdown" data-toggle="dropdown" aria-haspopup="true" id="store-change-button">
                All Store Views            </button>
            <ul class="dropdown-menu" data-role="stores-list">
                <li class="store-switcher-all disabled ">
                    <span>All Store Views</span>
                </li>
                <li class="store-switcher-website disabled ">
                    <span>Main Website</span>
                </li>
                <li class="store-switcher-store disabled ">
                    <span>Main Website Store</span>
                </li>
                <li class="store-switcher-store-view  ">
                    <a data-role="store-view-id" data-value="1" href="#">
                        Default Store View                                    </a>
                </li>
            </ul>
        </div>
        <div class="admin__field-tooltip tooltip"><a href="https://docs.magento.com/user-guide/configuration/scope.html" onclick="this.target='_blank'" title="What is this?" class="admin__field-tooltip-action action-help"><span>What is this?</span></a></div>    </div>


    <div class="page-actions-placeholder"></div><div class="page-actions" data-ui-id="page-actions-toolbar-content-header"><div class="page-actions-inner" data-title="New Custom Variable"><div class="page-actions-buttons">
                <button id="back" title="Back" type="button" class="action-default scalable back" backend-button-widget-hook-id="buttonIdGRAZWJIdni" data-ui-id="system-variable-edit-0-back-button">
                    <span>Back</span>
                </button>
                <button id="reset" title="Reset" type="button" class="action-default scalable reset" backend-button-widget-hook-id="buttonIdiKNHn1vgdl" data-ui-id="system-variable-edit-0-reset-button">
                    <span>Reset</span>
                </button>
                <button id="save" title="Save" type="button" class="action-default scalable save primary ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" backend-button-widget-hook-id="buttonIdvZgLwDbiwv" data-ui-id="system-variable-edit-0-save-button" role="button" aria-disabled="false"><span class="ui-button-text">
    <span>Save</span>
</span></button>
                <button id="save_and_edit" title="Save and Continue Edit" type="button" class="action-default scalable save ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" backend-button-widget-hook-id="buttonIdBF0ifk2wqh" data-ui-id="system-variable-edit-0-save-and-edit-button" role="button" aria-disabled="false"><span class="ui-button-text">
    <span>Save and Continue Edit</span>
</span></button>
            </div></div></div>
</div>
