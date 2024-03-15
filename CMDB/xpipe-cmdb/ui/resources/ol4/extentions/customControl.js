function searchControl(opt_options) {
    var options = opt_options || {};

    //html elements
    this.element = document.createElement('div');
    this.element.className = 'ol-search-collapsed ol-control';

    this.search_icon = document.createElement('button');
    this.search_icon.className = 'ol-search-icon';

    this.input = document.createElement('INPUT');
    this.input.className = 'ol-input-text';
    this.input.type = 'TEXT';

    this.search_action = document.createElement('button');
    this.search_action.className = 'ol-search-action';

    this.delete_action = document.createElement('button');
    this.delete_action.className = 'ol-delete-action';

    this.element.appendChild(this.search_icon);
    this.element.appendChild(this.input);
    this.element.appendChild(this.search_action);
    this.element.appendChild(this.delete_action);


    ol.events.listen(this.search_icon, ol.events.EventType.CLICK,
        searchControl.prototype.handleClick_.bind(this));

    ol.events.listen(this.search_action, ol.events.EventType.CLICK,
        searchControl.prototype.handleSearch_.bind(this));

    ol.events.listen(this.delete_action, ol.events.EventType.CLICK,
        searchControl.prototype.handleDelete_.bind(this));

    ol.events.listen(this.input, ol.events.EventType.KEYDOWN,
        searchControl.prototype.handleKeyDown_.bind(this));
    this._expanded = false;

    //see in documentation        
    ol.control.Control.call(this, {
        element: this.element,
        target: options.target
    });
}
ol.inherits(searchControl, ol.control.Control); //see in documentation

searchControl.prototype.baseUrl = 'https://nominatim.openstreetmap.org';

searchControl.prototype.handleClick_ = function (event) {
    event.preventDefault();

    if (this._isExpanded()) {
        this.collapse();
        this.removeSearchResults();
    } else {
        this.expand();
    }
}

searchControl.prototype._isExpanded = function () {
    return this.element.className.includes('expanded');
}

searchControl.prototype.expand = function () {
    //change icons
    this.element.classList.remove('ol-search-collapsed');
    this.element.classList.add('ol-search-expanded');
}

searchControl.prototype.collapse = function () {
    this.element.classList.remove('ol-search-expanded');
    this.element.classList.add('ol-search-collapsed');
}

searchControl.prototype.handleSearch_ = function () {
    if (Ext.isEmpty(this.input.value.trim())) {
        //HACK: if want to add a message to the empty search
        // this.showSearchResults({
        //     message: CMDBuildUI.locales.Locales.gis.extension.errorCall
        // });
        return;
    }

    var me = this;
    Ext.Ajax.request({
        url: Ext.String.format('{0}/search/?format=json&q={1}', this.baseUrl, this.input.value),
        method: 'GET',
        withCredentials: false,
        success: function (response) {
            var data = JSON.parse(response.responseText);
            me.showSearchResults(data);
        },
        failure: function () {
            me.showSearchResults({
                message: CMDBuildUI.locales.Locales.gis.extension.errorCall
            })
        }
    })
}

searchControl.prototype.handleKeyDown_ = function (event) {
    if (event.key == 'Enter') {
        this.handleSearch_();
    }
}

searchControl.prototype.handleDelete_ = function () {
    this.removeSearchResults();
    this.input.value = "";
}

searchControl.prototype.showSearchResults = function (data) {
    this.removeSearchResults();
    if (data.length != 0) {
        this._createSearchResults(data);
    } else {
        this._createSearchResults({
            message: CMDBuildUI.locales.Locales.gis.extension.noResults
        });
    }
    this.element.appendChild(this.list);
}

searchControl.prototype.removeSearchResults = function () {
    if (this.list) {
        this.list.remove();
    }
}

/**
 * @param {[Object] || Object}
 */
searchControl.prototype._createSearchResults = function (data) {
    this.list = document.createElement('div');
    this.list.className = 'ol-span-container'

    if (Ext.isArray(data)) {
        data.forEach(function (adress) {
            var span = document.createElement('span');
            span.className = 'ol-span';
            span.innerHTML = adress.display_name;
            this.list.appendChild(span);

            ol.events.listen(span, ol.events.EventType.CLICK,
                searchControl.prototype.spanclickhandler_.bind(this, adress));
        }, this);
    } else {
        var span = document.createElement('span');
        span.className = 'ol-span';
        span.innerHTML = data.message;
        this.list.appendChild(span);
    }
}

searchControl.prototype.spanclickhandler_ = function (data, event) {
    var coordinates = ol.proj.transform([Number(data.lon), Number(data.lat)], 'EPSG:4326', 'EPSG:3857')
    this.getMap().getView().setCenter(coordinates);
    this.getMap().getView().setZoom(18);
}

