Ext.define('CMDBuildUI.util.bim.ProjectLoader', {
    singleton: true,

    /**
     * getter
     * @returns the provate variables projectModels
     */
    getProjectModels: function () {
        return this.projectModels;
    },

    /**
     * this function creates the initial configuration for using the ProjectLoader file
     * @param {BimServerClient} bimServerClient the bimservrapi passe
     */
    _init: function (bimServerApi) {
        this.bimServerApi = bimServerApi;
        this.projectModels = {};
        this.auxProjectModels = [];
        this.projects = [];
    },

    /**
     * reset the value of the privates variables
     */
    reset: function () {
        this.bimServerApi = null;
        this.projectModels = null;
        this.auxProjectModels = null;
        this.project = null;
    },

    /**
     * @param {Number} poid an information refering to a specific bimProject
     * @param {String} ifcType Ifc4 or Ifc2x3tc1
     * @param {function callback(project, model)} callback the callback
     * @param {Object} callbackScope the this value for the callback
     */
    showProject: function (poid, ifcType, callback, callbackScope) {
        var me = this;
        this.bimServerApi.call("ServiceInterface", "getProjectByPoid", { poid: poid },
            function (project) {
                var sipleSmallProject = project;
                sipleSmallProject.schema = ifcType;
                me.loadRelatedProjectsPromise(sipleSmallProject.oid).done(function () {
                    me.projects.forEach(function (smallProject) {
                        me.getModelByProjectvariant(smallProject, callback, callbackScope);
                    }, me);
                });
            });
    },

    //TODO:make better comment
    /**
     * @param {Number} oid 
     */
    loadRelatedProjectsPromise: function (oid) {
        var promise = new window.BimServerApiPromise();
        this.loadRelatedProjectsVariant(oid, promise);
        return promise;
    },

    //TODO:make better comment
    /**
     * @param  {Number} oid
     * @param  {BimServerApiPromise} promise
     */
    loadRelatedProjectsVariant: function (oid, promise) {
        this.assert(this.projects.length == 0, 'checke the this.projects init');
        var me = this;
        var count;
        this.bimServerApi.call("ServiceInterface", "getAllRelatedProjects", { poid: oid },
            function (list) {
                count = list.length;
                list.forEach(function (smallProject) {
                    if (smallProject.state == 'ACTIVE') {
                        this.projects.push(smallProject);
                    }
                    if (smallProject.lastRevisionId != -1 && smallProject.nrSubProjects == 0) {
                        this.bimServerApi.getModel(smallProject.oid, smallProject.lastRevisionId, smallProject.schema, false, function (model) {
                            me.projectModels[smallProject.lastRevisionId] = model;

                            count--;
                            if (count == 0) {
                                promise.fire();
                            }
                        });
                    } else {
                        count--;
                    }
                }, me);
            });

    },


    /**
     * TODO:SPECIFY THE ROLE OF THIS FUNCTION
     * @param  {} project the project loaded from the bimserver
     * @param  {} callback callback function
     * @param  {} callbackScope the this parameter for the callback
     */
    getModelByProjectvariant: function (project, callback, callbackScope) {
        if (this.projectModels[project.lastRevisionId] != null) {
            var model = this.projectModels[project.lastRevisionId];
            this.preloadModel(project, model, model.roid).done(
                function (model) {
                    model.getAllOfType('IfcProject', true,
                        function (project) {
                            callback.call(callbackScope, project, model);
                        });
                });
        }
    },

    /**
     * @param  {Project} project the project loaded from the bimserver
     * @param  {Model} model the model loaded prom the bimserver
     * @param  {Number} roid information obtained from the model
     * @return {BimServerApiPromise} 
     */
    preloadModel: function (project, model, roid) {
        //assert(model != null, 'check the model here, shouldn"t be null');
        var promise = new BimServerApiPromise();

        // if (model.isPreloaded) {
        //     promise.fire();
        // } else {
        var preloadQuery = CMDBuildUI.util.bim.PreloadQuery.get(project.schema);
        model.query(preloadQuery, function (loaded) {
            //do nothing
        }).done(function () {
            // model.isPreloaded = true;
            promise.fire(model);
        });
        //}

        return promise;
    },

    /**
     * Declaration of privates methods or variables
     */
    privates: {
        /**
         * {bimServerClient} instance
         */
        bimServerApi: null,

        /**
         * {Object} containing a map between a project.lastRevisionId and a model
         */
        projectModels: null,

        /**
         * TODO:make comments
         */
        auxProjectModels: [],

        /**
         * {[Object]} stores in an array all the projects returned form the getProjects() 
         */
        projects: null,

        /**
         * function used for debugging
         * @param {Boolean} condition
         * @param {String} message
         * @throws Error if condition is false
         */
        assert: function (condition, message) {
            if (!condition) {
                message = message || 'Asserton failed';
                throw new Error(message);
            }
        }
    }

});