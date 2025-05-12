Ext.define('CMDBuildUI.view.main.footer.Container', {
    extend: 'CMDBuildUI.view.main.footer.BaseContainer',

    requires: [
        'CMDBuildUI.view.main.footer.ContainerController',
        'CMDBuildUI.view.main.footer.ContainerModel'
    ],

    xtype: 'main-footer-container',
    controller: 'main-footer-container',
    viewModel: {
        type: 'main-footer-container'
    },

    applicationUrlHTML: '<a href="https://sindria.org" target="_blank">www.sindria.org</a>',

    popupHTML:
        '<div class="x-selectable">' +
        '    <p><strong>Version:</strong> {0}</p>' +
        '    <p><strong>License:</strong> the software is released under <a href="http://www.gnu.org/licenses/agpl-3.0.html" target="_blank">AGPL</a> license.</p>' +
        '    <p><strong>Credits:</strong> CMDBuild is developed and maintained by <a href="http://tecnoteca.com" target="_blank">Tecnoteca srl</a>.' +
        '    <br />CMDBuild ® is a registered trademark of <a href="http://tecnoteca.com" target="_blank">Tecnoteca srl</a> and can\'t be removed.</p>' +
        '    <p style="margin-top: 35px; color: #83878b;">For further information please visit <a href="http://www.cmdbuild.org" target="_blank">www.cmdbuild.org</a></p>' +
        '</div>',

    popupHeight: 395
});
