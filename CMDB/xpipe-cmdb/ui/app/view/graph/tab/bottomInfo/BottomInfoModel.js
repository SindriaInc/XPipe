Ext.define('CMDBuildUI.view.graph.tab.bottomInfo.BottomInfoModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-bottominfo-bottominfo',
    data: {
        nodesNumber: 0,
        edgesNumber: 0
    },
    formulas: {
        nodesEdgesNumberChange: {
            bind: {
                cy: '{cy}'
            },
            get: function (data) {
                var cy = data.cy;

                if (cy) {
                    var me = this;

                    cy.on('add', function (eOpts) {
                        var l;
                        if (eOpts.target.isNode()) {
                            l = cy.nodes().length;
                            me.set('nodesNumber', l);

                        } else if (eOpts.target.isEdge()) {
                            l = cy.edges().length;
                            me.set('edgesNumber', l);
                        }
                    });
                }
            }
        }
    }

});
