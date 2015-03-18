var JSMapWidget = function() {

  var selfWidget = this;
  var widgetId;
  var jsonContentId;
  var jsonConfigContentId;
  var isEditMode;
  var hasAnyPoints;
  var scope = {};
  var api = { initParams : [] };

  api.log = function(msg) {
    if (console.log) {
      console.log(msg);
    }
  };

  var googleMapLoadScript = function() {
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = 'https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=jsMapWidgetInitGMap';
    if (scope.config.apiKey) {
      script.src += '&key=' + encodeURIComponent(scope.config.apiKey);
    }
    if (scope.config.region) {
      script.src += '&region=' + encodeURIComponent(scope.config.region);
    }
    if (scope.config.language) {
      script.src += '&language=' + encodeURIComponent(scope.config.language);
    }
    window.jsMapWidgetInitGMap = function() {
      api.log('initgmap called');
      var handlers = window.jsMapWidgetInitGMapHandlers || [];
      api.log('handlers');
      api.log(handlers);
      handlers.push(googleMapLoadCallback);
      api.log('now');
      api.log(handlers)
      window.jsMapWidgetInitGMapHandlers = handlers;
      var handler = window.jsMapWidgetInitGMapHandlers.pop();

      handler.apply(selfWidget, []);
    };
    document.body.appendChild(script);

  };

  var googleMapLoadCallback = function() {
    api.log('gmap callback called');
    api.initMap();
  };

  api.initSelf = function() {
    api.log('initSelf called');
    api.log(this.initParams);

    this.widgetId = this.initParams[0];
    this.jsonContentId = this.initParams[1];
    this.jsonConfigContentId = this.initParams[2];
    this.isEditMode = (this.initParams[3].toLowerCase() === 'true');
    this.hasAnyPoints = false;
    this.initDefaultLocation = false;
    this.geo = {};

    api.load();

    googleMapLoadScript();
    return api;
  };

  api.load = function() {
    var content = $('#' + this.jsonContentId).val();
    api.log('json content: ' + content);
    scope.data = JSON.parse(content);
    scope.pointIndex = 0;
    var configContent = $('#' + this.jsonConfigContentId).val();
    api.log('json config content: ' + configContent);
    scope.config = JSON.parse(configContent);
    api.log('bean');
    api.log(scope.data);
    this.hasAnyPoints = (scope.data.points.length > 0);
  };

  api.addNewDefaultPoint = function() {
    var defLatitude = scope.config.latitude || 45.52976525113116;
    var defLongitude = scope.config.longitude || 9.145197638317882;
    var defZoom = scope.config.zoom || 7;

    api.log("insert default empty point");
    var newPoint = {
      latitude: defLatitude + '',
      longitude: defLongitude + '',
      zoom: defZoom + ''
    };
    api.log(newPoint);
    scope.data.points.push(newPoint);
    if (this.isEditMode) {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
          if (position) {
            var pt = scope.data.points[scope.pointIndex];
            pt.latitude = position.coords.latitude;
            pt.longitude = position.coords.longitude;
            api.initMap();
          }
        });
      } else {
        api.log("geolocation not available");
      }
    }
  };

  api.addNewPoint = function() {
    var defLatitude = scope.config.latitude || 45.52976525113116;
    var defLongitude = scope.config.longitude || 9.145197638317882;
    var defZoom = scope.config.zoom || 7;

    if (scope.map) {
      var center = scope.map.getCenter();
      defLatitude = center.lat();
      defLongitude = center.lng();
      defZoom = scope.map.getZoom();
    }

    api.log("insert default empty point");
    var newPoint = {
      _type: "com.atex.plugins.map.MapPoint",
      latitude: String(defLatitude),
      latitude_type: "java.lang.String",
      longitude: String(defLongitude),
      longitude_type: "java.lang.String",
      zoom: String(defZoom),
      zoom_type: "java.lang.String"
    };
    api.log(newPoint);
    scope.data.points.push(newPoint);
  };

  api.store = function() {
    api.log('bean');
    api.log(scope.data);
    var content = JSON.stringify(scope.data);
    api.log('json content: ' + content);
    $('#' + this.jsonContentId).val(content);
  };

  api.initMap = function() {
    api.log('initMap');
    var mapDiv = $('#' + this.widgetId).find('.mapWidget')[0];
    api.log('hasAnyPoints: ' + this.hasAnyPoints);
    api.log('isEditMode: ' + this.isEditMode);

    if (this.isEditMode) {
      api.drawButtons();
    }

    if (this.hasAnyPoints || this.isEditMode) {
      var pt;
      if (this.hasAnyPoints) {
        pt = scope.data.points[scope.pointIndex];
      } else {
        pt = {
          latitude: (scope.config.latitude || 45.503557) + '',
          longitude: (scope.config.longitude || 9.184872) + '',
          zoom: (scope.config.zoom || 7) + ''
        };

        if (!this.initDefaultLocation) {
          if (navigator.geolocation) {
            var self = this;
            navigator.geolocation.getCurrentPosition(function(position) {
              if (position) {
                self.geo.latitude = position.coords.latitude;
                self.geo.longitude = position.coords.longitude;
                self.initDefaultLocation = true;
                api.initMap();
              }
            });
          } else {
            api.log("geolocation not available");
          }
        }

      }
      var latitude = pt.latitude && parseFloat(pt.latitude, 10) || scope.config.latitude || 45.503557;
      var longitude = pt.longitude && parseFloat(pt.longitude, 10) || scope.config.longitude || 9.184872;

      var location = new google.maps.LatLng(latitude, longitude);

      var mapOptions = {
        zoom: parseInt(pt.zoom, 10) || scope.config.zoom || 7,
        disableDefaultUI: true,
        panControl: true,
        scaleControl: true,
        streetViewControl: false,
        zoomControl: true,
        center: location,
        mapTypeId: google.maps.MapTypeId.ROADMAP
      };
      google.maps.visualRefresh = true;
      $(mapDiv).height('300px');

      scope.map = new google.maps.Map(mapDiv, mapOptions);

      if (this.hasAnyPoints) {
        scope.marker = new google.maps.Marker({
            position: location,
            map: scope.map,
            draggable: this.isEditMode
        });
      }

      if (this.isEditMode) {
        if (this.hasAnyPoints) {
          google.maps.event.addListener(scope.marker, 'dragend', function() { api.editMarkerDragCallback(scope.marker); });
          google.maps.event.addListener(scope.map, 'zoom_changed', function() { api.zoomChangedCallback(scope.map); });
        }
        this.setupAutoComplete();
      }
    } else {
      $(mapDiv).html('<i>no points on the map</i>');
    }

  };

  api.setupAutoComplete = function() {
    api.log('setupAutoComplete');

    var inputClass = 'withautocomplete';
    var map = scope.map;
    var input = $('#' + this.widgetId + ' .location');

    // make sure we did not initialize twice.

    if (input.hasClass(inputClass)) {
      return;
    }

    var autocomplete = new google.maps.places.Autocomplete(input[0]);
    autocomplete.bindTo('bounds', map);

    input.keypress(function(e) {
      if (e.which == 13) {
        e.preventDefault();
        api.log('fire place_changed');
        google.maps.event.trigger(autocomplete, 'place_changed');
        return false;
      }
    });

    google.maps.event.addListener(autocomplete, 'place_changed', function() {
      var place = autocomplete.getPlace();
      if (!place || !place.geometry) {
        return;
      }

      // If the place has a geometry, then present it on a map.
      if (place.geometry.viewport) {
        map.fitBounds(place.geometry.viewport);
      } else {
        map.setCenter(place.geometry.location);
      }

      var address = '';
      if (place.address_components) {
        address = [
          (place.address_components[0] && place.address_components[0].short_name || ''),
          (place.address_components[1] && place.address_components[1].short_name || ''),
          (place.address_components[2] && place.address_components[2].short_name || '')
        ].join(' ');

        api.log(address);
      }
    });

    input.addClass(inputClass);
  }

  api.editMarkerDragCallback = function(myMarker) {
    var pos = myMarker.getPosition();
    scope.data.points[scope.pointIndex].latitude = pos.lat();
    scope.data.points[scope.pointIndex].longitude = pos.lng();
    api.store();
  };

  api.zoomChangedCallback = function(myMap) {
    var zoom = myMap.getZoom();
    scope.data.points[scope.pointIndex].zoom = zoom;
    api.store();
  };

  api.drawButtons = function()  {
    $('#' + this.widgetId + ' .delpoint').remove();
    $('#' + this.widgetId + ' .addpoint').remove();
    $('#' + this.widgetId + ' .location').remove();
    $('#' + this.widgetId + ' .changelocation').remove();
    var mapDiv = $('#' + this.widgetId).find('.mapWidget')[0];
    var html = '';
    if (this.hasAnyPoints) {
      html = '<button type=\"button\" class=\"delpoint\">del point</button>';
    } else {
      html = '<button type=\"button\" class=\"addpoint\">add point</button>';
    }
    html += '<input class=\"location\" type=\"text\" placeholder=\"insert location...\" size=\"40\"/>';
    html += '<button type=\"button\" class=\"changelocation\">change location</button>';
    $(mapDiv).before(html);
    if (this.isEditMode) {
      $('#' + this.widgetId + ' .delpoint').click(function() {
        api.delpoint();
      });
      $('#' + this.widgetId + ' .addpoint').click(function() {
        api.addpoint();
      });
      $('#' + this.widgetId + ' .changelocation').click(function() {
        api.changelocation();
      });
    }
  };

  api.delpoint = function() {
    scope.data.points = [];
    api.store();
    this.hasAnyPoints = false;
    api.initMap();
    api.drawButtons();
    api.setupAutoComplete();
  };

  api.addpoint = function() {
    this.hasAnyPoints = true;
    api.addNewPoint();
    api.initMap();
    api.drawButtons();
    api.setupAutoComplete();
    api.store();
  };

  api.changelocation = function() {
    var newLocation = $('#' + this.widgetId + ' .location').val();
    api.log('newLocation: ' + newLocation);
    api.geodecodeLocation(newLocation);
  };

  api.geodecodeLocation = function(location) {

    var self = this;
    var geocoder = new google.maps.Geocoder();

    geocoder.geocode(
      {
        address: "'" + location + "'"
      },
      function(results, status) {

        if (results.length) {
          // You should always check that a result was returned, as it is
          // possible to return an empty results object.
          if (status != google.maps.GeocoderStatus.ZERO_RESULTS) {
            var pos = results[0].geometry.location;
            if (self.hasAnyPoints) {
              scope.data.points[scope.pointIndex].latitude = String(pos.lat());
              scope.data.points[scope.pointIndex].longitude = String(pos.lng());
            }

            if (scope.map) {
              scope.map.setCenter(pos);
            }
            if (scope.marker) {
              scope.marker.setPosition(pos);
            }
          } else {
            alert('No results for ' + location + '.');
          }
        } else {
          alert('No results for ' + location + ' due to ' + status + '.');
        }
      }
    );
  };

  return api;
}
