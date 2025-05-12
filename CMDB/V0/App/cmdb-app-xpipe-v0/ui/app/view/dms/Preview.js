Ext.define('CMDBuildUI.view.dms.Preview', {
    extend: 'Ext.Img',
    alias: 'widget.dms-preview',

    config: {
        /**
         * @cfg {String} attachmentUrl
         */
        attachmentUrl: null,

        /**
         * @cfg {String} proxyUrl
         */
        proxyUrl: null,

        /**
         * @cfg {String} attachmentId
         */
        attachmentId: null,

        /**
         * @cfg {String} DMSCategoryType
         */
        DMSCategoryType: null,

        /**
         * @cfg {Numeric} DMSCategoryTypeValue
         */
        DMSCategoryTypeValue: null,

        /**
         * @cfg {String} fileName
         */
        fileName: null,

        /**
         * @cfg {String} fileMimeType
         */
        fileMimeType: null
    },

    maxHeight: 75,
    maxWidth: 75,

    src: null,
    genericSrc: CMDBuildUI.util.helper.AttachmentsHelper.genericPreview,
    alt: CMDBuildUI.locales.Locales.attachments.preview,

    /**
     * 
     * @param {String} newValue 
     * @param {String} oldValue 
     */
    updateAttachmentUrl: function (newValue, oldValue) {
        var me = this,
            vm = this.getViewModel(),
            record = vm.getData().record;
        if (newValue) {
            CMDBuildUI.util.helper.AttachmentsHelper.getPreview(
                vm.get("objectType"),
                vm.get("objectTypeName"),
                vm.get("objectId"),
                record.getId(),
                record.get("_category_name")
            ).then(function (src) {
                me.setSrc(src);
                me.updateLayout();
                me.on({
                    click: {
                        element: 'el', //bind to the underlying el property on the panel
                        fn: 'clickhandler',
                        scope: me
                    }
                });
            })
        } else {
            me.setSrc(me.genericSrc);
            me.setStyle("cursor", "auto");
        }
    },

    clickhandler: function () {
        if (this.getAttachmentUrl()) {
            var metatype = this.getFileMimeType(),
                fileName = this.getFileName(),
                fileUrl = this.getAttachmentUrl() + '/' + encodeURI(fileName).match(/(?:"[^"]*"|^[^"]*$)/)[0].replace(/"/g, "").replace(/\+/g, ' '),
                popupTitle = Ext.String.format(
                    "{0} - {1}",
                    CMDBuildUI.locales.Locales.attachments.fileview,
                    fileName
                );

            function openFile(type) {
                var popup = CMDBuildUI.util.Utilities.openPopup(
                    null,
                    popupTitle,
                    {
                        xtype: 'dms-file-view',
                        fileUrl: fileUrl,
                        fileType: type,
                        fileName: fileName,
                        // close popup fn
                        closePopup: function () {
                            popup.close();
                        }
                    }
                );
            }
            if (Ext.String.startsWith(metatype, "image/", true)) {
                openFile(CMDBuildUI.view.dms.file.View.types.image);
            } else if (Ext.String.startsWith(metatype, "text/", true)) {
                openFile(CMDBuildUI.view.dms.file.View.types.text);
            } else if (metatype === "application/pdf") {
                openFile(CMDBuildUI.view.dms.file.View.types.pdf);
            } else {
                CMDBuildUI.util.File.download(
                    fileUrl,
                    fileName,
                    undefined,
                    undefined,
                    {
                        skipUrlEncode: true
                    }
                );
            }
        }
    },

    updateProxyUrl: function (newValue, oldValue) {
        this.setAttachmentUrlFromId(this.getAttachmentId());
    },

    /**
     * 
     * @param {String} value 
     * @param {String} oldValue 
     * 
     * This function sets the attachmentid config only if the vm record (passed by the rowWidget) has a valid id. 
     * Useful when expanding the row and collapsing the parent grouping.
     */
    applyAttachmentId: function (value, oldValue) {
        return value;
    },

    updateAttachmentId: function (newValue, oldValue) {
        this.setAttachmentUrlFromId(newValue);
    },

    initComponent: function () {
        if (Ext.isEmpty(this.src)) {
            this.setSrc(this.genericSrc);
        }
        return this.callParent(arguments)
    },

    privates: {
        composeAttachmentUrl: function (proxyUrl, attachmentId) {
            return CMDBuildUI.util.Config.baseUrl + proxyUrl + '/' + attachmentId;
        },

        setAttachmentUrlFromId: function (attachmentId) {
            var record = this.lookupViewModel().get("record");
            if (!Ext.isEmpty(attachmentId) && (record && !record.phantom)) {
                var proxyUrl = this.getProxyUrl();
                if (!Ext.isEmpty(proxyUrl)) {
                    var attachmentUrl = this.composeAttachmentUrl(proxyUrl, attachmentId);
                    this.setAttachmentUrl(attachmentUrl);
                    this.setStyle("cursor", "pointer");
                }
            } else {
                this.setAttachmentUrl();
            }
        }
    }
});