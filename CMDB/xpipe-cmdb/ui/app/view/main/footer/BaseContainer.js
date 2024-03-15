Ext.define('CMDBuildUI.view.main.footer.BaseContainer',{
    extend: 'Ext.container.Container',

    alias: 'widget.main-footer-basecontainer',

    dock: 'bottom',
    padding: '5px 10px',
    cls: 'main-footer',
    layout: 'hbox',

    // add data-testid attribute to element
    autoEl: {
        'data-testid' : 'main-footer-container'
    },

    config: {
        /**
         * @cfg {String} applicationUrlHTML
         */
        applicationUrlHTML: null,

        /**
         * @cfg {String} popupHTML
         */
        popupHTML: null,

        /**
         * @cfg {String} popupHeight
         */
        popupHeight: null
    },

    style: {
        textAlign: 'center'
    },

    items: [{
        xtype: 'component',
        flex: 1
    },{
        xtype: 'component',
        itemId: 'urlComponent',
        width: 200,
        style: {
            textAlign: 'right'
        }
    },{
        xtype: 'component',
        html: '&middot',
        width: 40
    },{
        xtype: 'component',
        itemId: 'infoComponent',
        html: CMDBuildUI.locales.Locales.main.info,
        localized: {
            html: 'CMDBuildUI.locales.Locales.main.info'
        },
        style: {
            cursor: 'pointer'
        },
        listeners: {
            click: {
                element: 'el',
                fn: 'onInfoComponentClick'
            }
        }
    },{
        xtype: 'component',
        html: '&middot',
        width: 40
    },{
        xtype: 'component',
        itemId: 'copyrightComponent',
        html: '<a href="http://www.tecnoteca.com" target="_blank">Copyright &copy; Tecnoteca srl</a>',
        width: 200,
        style: {
            textAlign: 'left'
        }
    },{
        xtype: 'component',
        flex: 1
    }]
});