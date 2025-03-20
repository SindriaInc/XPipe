<div class="page-wrapper"><div class="notices-wrapper">    <noscript>
            <div class="messages">
                <div class="message message-warning message-noscript">
                    <strong>JavaScript may be disabled in your browser.</strong>
                    To use this website you must first enable JavaScript in your browser.            </div>
            </div>
        </noscript>
        <!--
        /**
         * Copyright &copy; Magento, Inc. All rights reserved.
         * See COPYING.txt for license details.
         */
        --><div class="admin__data-grid-outer-wrap" data-bind="scope: 'notification_area.notification_area'">
            <div data-role="spinner" data-component="notification_area.notification_area.columns" class="admin__data-grid-loading-mask" style="display: none;">
                <div class="spinner">
                    <span></span><span></span><span></span><span></span><span></span><span></span><span></span><span></span>
                </div>
            </div>
            <!-- ko template: getTemplate() -->
            <!-- ko foreach: {data: elems, as: 'element'} -->
            <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
            <div id="system_messages" class="message-system" data-bind="visible: totalRecords, collapsible" style="display: none;">
                <div class="message-system-inner" data-bind="outerClick: fixLoaderHeight.bind($data, true)">
                    <div class="message-system-short">
                        <button class="message-system-action-dropdown" data-bind="toggleCollapsible">
                <span>
                    <!-- ko i18n: 'System Messages' --><span>System Messages</span><!-- /ko -->:
                    <!-- ko text: totalRecords -->0<!-- /ko -->
                </span>
                        </button>
                        <!-- ko if: rows[0] --><!-- /ko -->
                    </div>
                    <div class="message-system-collapsible">
                        <ul class="message-system-list">
                            <!-- ko repeat: {foreach: rows, item: '$row'} --><!-- /ko -->
                        </ul>
                        <!-- ko if: isAllowed --><div class="message-system-summary">
                            <a class="action__message-log" href="#" data-bind="text: dismissAllText, click: dismissAll">Dismiss All Completed Tasks</a>
                            <a class="action__message-log" data-bind="attr: {
                        href: link
                    }, text: linkText" href="https://dev-xpipe.sindria.org/dashboard/bulk/index/index/key/2693e7a225e02a7ed2ed25575bc5fd9d574893ba44066c7986e8f701d8f67ad4/">Bulk Actions Log</a>
                        </div><!-- /ko -->
                    </div>
                </div>
            </div>
            <!-- /ko --><!-- /ko -->

            <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->
            <!-- ko foreach: {data: elems, as: 'element'} -->
            <!-- ko if: hasTemplate() --><!-- ko template: getTemplate() -->

            <!-- /ko --><!-- /ko -->
            <!-- /ko -->
            <!-- /ko --><!-- /ko -->
            <!-- /ko -->
            <!-- /ko -->
        </div>
        <!-- BLOCK notification_window --><!-- /BLOCK notification_window --></div><header class="page-header row"><div class="page-header-hgroup col-l-8 col-m-6">
            <div class="page-title-wrapper">
                <h1 class="page-title">Dashboard</h1>
            </div>
        </div><div class="page-header-actions col-l-4 col-m-6">        <div class="admin-user admin__action-dropdown-wrap">
                <a href="https://dev-xpipe.sindria.org/dashboard/xpipe/system_account/index/key/cae59b846c9f680684f3d597a46274f0ab617caf2e221cf9388c4a0fa4baa48f/" class="admin__action-dropdown" title="My Account" data-toggle="dropdown">
                <span class="admin__action-dropdown-text">
                    <span class="admin-user-account-text">paolo.rossi</span>
                </span>
                </a>
                <ul class="admin__action-dropdown-menu">
                    <li>
                        <a href="https://dev-xpipe.sindria.org/dashboard/xpipe/system_account/index/key/cae59b846c9f680684f3d597a46274f0ab617caf2e221cf9388c4a0fa4baa48f/" data-ui-id="user-user-account-settings" title="Account Setting">
                            Account Setting (<span class="admin-user-name">paolo.rossi</span>)
                        </a>
                    </li>
                    <li>
                        <a href="https://dev-xpipe.sindria.org/" title="Customer View" target="_blank" class="store-front">
                            Customer View                    </a>
                    </li>
                    <li>
                        <a href="https://dev-xpipe.sindria.org/dashboard/xpipe/auth/logout/key/b7acddbb839ac60a2fd89c4273aca1ec27ce0d441a0ce26f56bb9860f1b734a0/" class="account-signout" title="Sign Out">
                            Sign Out                    </a>
                    </li>
                </ul>
            </div>

            <div class="notifications-wrapper admin__action-dropdown-wrap" data-notification-count="0">
                <a class="notifications-action admin__action-dropdown" href="https://dev-xpipe.sindria.org/dashboard/xpipe/notification/index/key/46b2da14bdf63f11cda8c87c16bc467ce190fecdd48e3b4cfa0e17e5790445eb/" title="Notifications">
                </a>
            </div>
            <div class="search-global">
                <form action="#" id="form-search">
                    <div class="search-global-field">
                        <label class="search-global-label" for="search-global"></label>
                        <input type="hidden" name="query"><div class="mage-suggest"><div class="mage-suggest-inner"><input type="text" class="search-global-input" id="search-global" autocomplete="off"><div class="autocomplete-results" style="display: none;"></div></div></div>
                        <button type="submit" class="search-global-action" title="Search"></button>
                    </div>
                </form>
                <script data-template="search-suggest" type="text/x-magento-template">
                    <ul class="search-global-menu">
                        <li class="item">
                            <a id="searchPreviewProducts" href="https://dev-xpipe.sindria.org/dashboard/catalog/product/index/key/2ee4f6b696af3bd8606fb32af7b471f19841d59cbe499a4d8647426845f51ae6/?search=<%- data.term%>" class="title">"<%- data.term%>" in Products</a>
                        </li>
                        <li class="item">
                            <a id="searchPreviewOrders" href="https://dev-xpipe.sindria.org/dashboard/sales/order/index/key/223bcecd8dfa85fcd1a38e1d4eea7d62c94b8b6b013c764dbf422ca5f4bf3a2e/?search=<%- data.term%>" class="title">"<%- data.term%>" in Orders</a>
                        </li>
                        <li class="item">
                            <a id="searchPreviewCustomers" href="https://dev-xpipe.sindria.org/dashboard/customer/index/index/key/7ff39378763bbde15889418577adf4f5f7cd105186cc4eed1b7d1f3e163d31fc/?search=<%- data.term%>" class="title">"<%- data.term%>" in Customers</a>
                        </li>
                        <li class="item">
                            <a id="searchPreviewPages" href="https://dev-xpipe.sindria.org/dashboard/cms/page/index/key/857826731fd0fec9cdbf03f07e4ea1b67a80e0172f5718cdc4592e253a472b6c/?search=<%- data.term%>" class="title">"<%- data.term%>" in Pages</a>
                        </li>
                        <% if (data.items.length) { %>
                        <% _.each(data.items, function(value){ %>
                        <li class="item"
                        <%= data.optionData(value) %>
                        >
                        <a href="<%- value.url %>" class="title"><%- value.name %></a>
                        <span class="type"><%- value.type %></span>
                        <%- value.description || "" %>
                        </li>
                        <% }); %>
                        <% } else { %>
                        <li>
                <span class="mage-suggest-no-records">
                    No records found.                </span>
                        </li>
                        <% } %>
                    </ul>
                </script>
            </div>
        </div></header><div><input name="form_key" type="hidden" value="DcvntOaPxglthznp"></div>
    <main id="anchor-content" class="page-content"><div id="page:main-container" class="page-columns"><div class="admin__old"><div id="container" class="main-col">
                    <section class="dashboard-advanced-reports" data-index="dashboard-advanced-reports">
                        <div class="dashboard-advanced-reports-description">
                            <header class="dashboard-advanced-reports-title">
                                Advanced Reporting        </header>
                            <div class="dashboard-advanced-reports-content">
                                Gain new insights and take command of your business' performance, using our dynamic product, order, and customer reports tailored to your customer data.        </div>
                        </div>
                        <div class="dashboard-advanced-reports-actions">
                            <a href="https://dev-xpipe.sindria.org/dashboard/analytics/reports/show/key/d60af3fb63cd5704611e9e643e89d6b0eadd60ba7a4148fb576889b6edc479e1/" target="_blank" class="action action-advanced-reports" data-index="analytics-service-link" title="Go to Advanced Reporting">
                                <span>Go to Advanced Reporting</span>
                            </a>
                        </div>
                    </section>



                    <script>    define('analyticsPopupConfig', function () {
                            return {
                                analyticsVisible: 0,
                                releaseVisible: 0,
                            }
                        });</script>
                    <div class="dashboard-container row">
                        <div class="dashboard-main col-m-8 col-m-push-4">
                            <div class="dashboard-diagram-container">
                                <div id="diagram_tab_content" class="dashboard-diagram-tab-content"></div>
                            </div>
                            <div class="dashboard-store-stats">
                                <div id="grid_tab_content" class="dashboard-store-stats-content"></div>
                            </div>
                        </div>
                        <div class="dashboard-secondary col-m-4 col-m-pull-8">
                            <div class="dashboard-item">
                                <div class="dashboard-item-title">Last Orders</div>
                            </div>
                            <div class="dashboard-item">
                                <div class="dashboard-item-title">Last Search Terms</div>
                                <div class="dashboard-item-content">
                                    <div class="empty-text">We couldn't find any records.</div>
                                </div>
                                <script>var deps = [];
                                    deps.push('mage/adminhtml/grid');
                                    require(deps, function(){
//TODO: getJsObjectName and getRowClickCallback has unexpected behavior. Should be removed
                                        lastSearchGridJsObject = new varienGrid('lastSearchGrid', 'https\u003A\u002F\u002Fdev\u002Dxpipe.sindria.org\u002Fdashboard\u002Fxpipe\u002Fdashboard\u002Findex\u002Fkey\u002Fb5647109bac1fae00d5e3cbb6cb4ad67a10d27dc157ebe3f6fba3fc7170e8be9\u002F', 'page', 'sort', 'dir', 'filter');
                                        lastSearchGridJsObject.useAjax = '';
                                        lastSearchGridJsObject.rowClickCallback = openGridRow;
                                    });
                                </script>            </div>
                            <div class="dashboard-item">
                                <div class="dashboard-item-title">Top Search Terms</div>
                                <div class="dashboard-item-content">
                                    <div class="empty-text">We couldn't find any records.</div>
                                </div>
                                <script>var deps = [];
                                    deps.push('mage/adminhtml/grid');
                                    require(deps, function(){
//TODO: getJsObjectName and getRowClickCallback has unexpected behavior. Should be removed
                                        topSearchGridJsObject = new varienGrid('topSearchGrid', 'https\u003A\u002F\u002Fdev\u002Dxpipe.sindria.org\u002Fdashboard\u002Fxpipe\u002Fdashboard\u002Findex\u002Fkey\u002Fb5647109bac1fae00d5e3cbb6cb4ad67a10d27dc157ebe3f6fba3fc7170e8be9\u002F', 'page', 'sort', 'dir', 'filter');
                                        topSearchGridJsObject.useAjax = '';
                                        topSearchGridJsObject.rowClickCallback = openGridRow;
                                    });
                                </script>            </div>
                        </div>
                    </div>


                    <div class="xpipe-changeme">
                        <section class="xpipe-chat-avenue-messenger xpipe-chat-avenue-messenger-minimized">
                            <div class="xpipe-chat-menu" style="display: none;">
                                <div class="xpipe-chat-menu-items">
                <span class="active">
                    <a class="xpipe-chat-minimize" href="#" title="Minimize">—</a>
                    <br>
                    <a class="xpipe-chat-close" href="#" title="End Chat">✕</a>
                </span>
                                </div>
                                <div class="xpipe-chat-button active">...</div>
                            </div>
                            <a class="xpipe-chat-toggle-button" href="#" title="Toggle">
                                <div class="xpipe-chat-agent-face">
                                    <div class="xpipe-chat-half">
                                        <img class="xpipe-chat-agent xpipe-chat-circle" src="https://agentpalmer.com/wp-content/uploads/2014/10/Irving-Metzman-as-Richter-standing-next-to-the-WOPR-War-Operations-Plan-Response.jpg" alt="Chat Avatar">
                                    </div>
                                </div>
                            </a>
                            <div class="xpipe-chat" style="display: none;">
                                <div class="xpipe-chat-title">
                                    <h1>Joshua</h1>
                                </div>
                                <div class="xpipe-chat-messages">
                                    <div class="xpipe-chat-messages-content mCustomScrollbar _mCS_1 mCS_no_scrollbar"><div id="mCSB_1" class="mCustomScrollBox mCS-light mCSB_vertical mCSB_inside" style="max-height: 101px;" tabindex="0"><div id="mCSB_1_container" class="mCSB_container mCS_y_hidden mCS_no_scrollbar_y" style="position:relative; top:0; left:0;" dir="ltr"><div class="xpipe-chat-message xpipe-chat-message-loading xpipe-chat-message-new"><figure class="xpipe-chat-avatar"><img src="https://64.media.tumblr.com/c9dfec0eef9493e738265f6a15c129da/283fd3dd4f0601a9-cf/s1280x1920/8a7ff8a6dd7127734b798dc0beb88b05c04653e8.jpg"></figure><span>...</span></div><div class="xpipe-chat-message xpipe-chat-message-new new"><figure class="xpipe-chat-avatar"><img src="https://64.media.tumblr.com/c9dfec0eef9493e738265f6a15c129da/283fd3dd4f0601a9-cf/s1280x1920/8a7ff8a6dd7127734b798dc0beb88b05c04653e8.jpg"></figure>Hi there, I'm Joshua, how can i help you?<div class="xpipe-chat-message-timestamp">15:58</div><div class="xpipe-chat-message-checkmark-sent-delivered">✓</div><div class="xpipe-chat-message-checkmark-read">✓</div></div></div><div id="mCSB_1_scrollbar_vertical" class="mCSB_scrollTools mCSB_1_scrollbar mCS-light mCSB_scrollTools_vertical" style="display: none;"><div class="mCSB_draggerContainer"><div id="mCSB_1_dragger_vertical" class="mCSB_dragger" style="position: absolute; min-height: 30px; top: 0px; height: 0px;" oncontextmenu="return false;"><div class="mCSB_dragger_bar" style="line-height: 30px;"></div></div><div class="mCSB_draggerRail"></div></div></div></div></div>
                                </div>
                                <div class="xpipe-chat-message-box">
                                    <textarea type="text" class="xpipe-chat-message-input" placeholder="Type message..."></textarea>
                                    <button type="submit" class="xpipe-chat-message-submit">Send</button>
                                </div>
                            </div>

                        </section>
                    </div>



                    <!--<div class="xpipe-chat-restore-button">-->
                    <!--<section class="xpipe-chat-avenue-messenger-minimized xpipe-chat-minimized" style="height: 0px !important; max-height: 0px !important; min-height: 0px !important; display: none;">-->
                    <!--    <div class="xpipe-chat-agent-face-minimized" style="top: -80px !important;">-->
                    <!--        <div class="xpipe-chat-half">-->
                    <!--            <img class="xpipe-chat-agent xpipe-chat-circle" src="https://agentpalmer.com/wp-content/uploads/2014/10/Irving-Metzman-as-Richter-standing-next-to-the-WOPR-War-Operations-Plan-Response.jpg" alt="Chat Avatar">-->
                    <!--        </div>-->
                    <!--    </div>-->
                    <!--</section>-->
                    <!--</div>-->


                    <script>

                        (function  () {
                            require(["jquery", "scroll"],function($) {

                                var $messages = $('.xpipe-chat-messages-content'),
                                    d, h, m,
                                    i = 0;


                                function updateScrollbar() {
                                    $messages.mCustomScrollbar("update").mCustomScrollbar('scrollTo', 'bottom', {
                                        scrollInertia: 10,
                                        timeout: 0
                                    });
                                }


                                function setDate(){
                                    d = new Date()
                                    if (m != d.getMinutes()) {
                                        m = d.getMinutes();
                                        $('<div class="xpipe-chat-message-timestamp">' + d.getHours() + ':' + m + '</div>').appendTo($('.xpipe-chat-message:last'));
                                        $('<div class="xpipe-chat-message-checkmark-sent-delivered">&check;</div>').appendTo($('.xpipe-chat-message:last'));
                                        $('<div class="xpipe-chat-message-checkmark-read">&check;</div>').appendTo($('.xpipe-chat-message:last'));
                                    }
                                }

                                var Fake = [
                                    'Hi there, I\'m Joshua, how can i help you?',
                                    'Sure, give me the asset name',
                                    'Ok! Launching pipeline...',
                                    'Pipeline in progress...',
                                    'Almost done...',
                                    'Good! Pipeline completed, your asset is now available at: demo-mc.sindria.org',
                                    'XPipe is a nice place to stay',
                                    'It was a pleasure chat with you',
                                    'Time to make a new pipeline',
                                    'Bye',
                                    ':)'
                                ]


                                function fakeMessage() {
                                    if ($('.xpipe-chat-message-input').val() != '') {
                                        return false;
                                    }
                                    $('<div class="xpipe-chat-message xpipe-chat-message-loading xpipe-chat-message-new"><figure class="xpipe-chat-avatar"><img src="https://64.media.tumblr.com/c9dfec0eef9493e738265f6a15c129da/283fd3dd4f0601a9-cf/s1280x1920/8a7ff8a6dd7127734b798dc0beb88b05c04653e8.jpg" /></figure><span>...</span></div>').appendTo($('.mCSB_container'));
                                    updateScrollbar();

                                    setTimeout(function() {
                                        console.log(Fake[i]);
                                        $('.xpipe-chat-message.loading').remove();
                                        $('<div class="xpipe-chat-message xpipe-chat-message-new"><figure class="xpipe-chat-avatar"><img src="https://64.media.tumblr.com/c9dfec0eef9493e738265f6a15c129da/283fd3dd4f0601a9-cf/s1280x1920/8a7ff8a6dd7127734b798dc0beb88b05c04653e8.jpg" /></figure>' + Fake[i] + '</div>').appendTo($('.mCSB_container')).addClass('new');
                                        setDate();
                                        updateScrollbar();
                                        i++;
                                    }, 1000 + (Math.random() * 20) * 100);

                                }

                                function insertMessage() {
                                    msg = $('.xpipe-chat-message-input').val();
                                    console.log(msg);
                                    if ($.trim(msg) == '') {
                                        return false;
                                    }
                                    $('<div class="xpipe-chat-message xpipe-chat-message-personal">' + msg + '</div>').appendTo($('.mCSB_container')).addClass('new');
                                    setDate();
                                    $('.xpipe-chat-message-input').val(null);
                                    updateScrollbar();
                                    setTimeout(function() {
                                        fakeMessage();
                                    }, 1000 + (Math.random() * 20) * 100);
                                }


                                function toggleChat() {
                                    console.log('toggled');
                                    minimizeChat();
                                    restoreChat();
                                }


                                function minimizeChat() {
                                    console.log('minimized');
                                    $('.xpipe-chat-avenue-messenger').toggleClass('xpipe-chat-avenue-messenger-minimized');
                                    //$('.xpipe-chat-menu').toggleClass('xpipe-chat-minimized');
                                    $('.xpipe-chat-menu').hide();
                                    //$('.xpipe-chat').toggleClass('xpipe-chat-minimized');
                                    $('.xpipe-chat').hide();
                                }

                                function restoreChat() {
                                    console.log('restored');
                                    $('.xpipe-chat-avenue-messenger-minimized').toggleClass('xpipe-chat-avenue-messenger');
                                    //$('.xpipe-chat-menu').toggleClass('xpipe-chat-minimized');
                                    $('.xpipe-chat-menu').show();
                                    //$('.xpipe-chat').toggleClass('xpipe-chat-minimized');
                                    $('.xpipe-chat').show();
                                }

                                function closeChat() {
                                    console.log('closed');
                                    $('.xpipe-chat-avenue-messenger').hide();
                                }


                                $(window).load(function() {
                                    $messages.mCustomScrollbar();
                                    setTimeout(function() {
                                        fakeMessage();
                                    }, 100);
                                });

                                $(document).ready(function() {

                                    $('.xpipe-chat-message-submit').click(function() {
                                        insertMessage();
                                    });

                                    $(window).on('keydown', function(e) {
                                        if (e.which == 13) {
                                            insertMessage();
                                            return false;
                                        }
                                    })


                                    $('.xpipe-chat-button').click(function() {
                                        $('.xpipe-chat-menu .xpipe-chat-menu-items span').toggleClass('active');
                                        $('.xpipe-chat-menu .xpipe-chat-button').toggleClass('active');
                                    });

                                    // Toggle chat
                                    $('.xpipe-chat-toggle-button').click(function() {
                                        toggleChat();
                                    });

                                    // Minimize
                                    $('.xpipe-chat-minimize').click(function() {
                                        minimizeChat();
                                    });

                                    // Close
                                    $('.xpipe-chat-close').click(function() {
                                        closeChat();
                                    });



                                });
                            });
                        })();

                    </script>
                </div></div></div></main><footer class="page-footer"><div class="page-footer-content row"><div class="footer-legal"><p class="copyright col-m-6"><a class="link-copyright" href="https://sindria.org" target="_blank" title="Sindria Inc"></a>
                    Copyright © 2025 Sindria Inc. All rights reserved.</p><div class="footer-legal-system col-m-6"><p class="magento-version">
                        <strong>XPipe</strong>
                        ver. 0.1.0</p>
                </div></div></div></footer></div>
