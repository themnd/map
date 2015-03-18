Atex.namespace("Atex.plugin.map");
(function(global) {
    "use strict";

    Atex.plugin.map = function () {

        var selfWidget = this;
        var mapId = null;
        var mapData = null;
        var mapConfig = null;

        var api = {
        };

        api.initialize = function(p_mapId, p_mapData, p_mapConfig) {
          mapId = p_mapId;
          mapData = p_mapData;
          mapConfig = p_mapConfig;
          api.log('initialize');
          api.log(mapId);
          api.log(mapData);
          api.log(mapConfig);

          if (mapData.points.length > 0) {
            googleMapLoadScript();
          }
        };

        api.log = function(msg) {
          if (console.log) {
            console.log(msg);
          }
        };

        api.initMap = function() {
          api.log('initMap ' + mapId);
          var $mapDiv = $('#' + mapId);
          var mapDiv = $mapDiv[0];

          var pt = mapData.points[0];

          var latitude = pt.latitude && parseFloat(pt.latitude, 10) || mapConfig.latitude || 45.503557;
          var longitude = pt.longitude && parseFloat(pt.longitude, 10) || mapConfig.longitude || 9.184872;

          var location = new google.maps.LatLng(latitude, longitude);

          var mapOptions = {
            zoom: parseInt(pt.zoom, 10) || mapConfig.zoom || 7,
            disableDefaultUI: true,
            panControl: true,
            scaleControl: true,
            streetViewControl: false,
            zoomControl: true,
            center: location,
            mapTypeId: google.maps.MapTypeId.ROADMAP
          };
          google.maps.visualRefresh = true;

          if (mapConfig.mapHeight) {
            $mapDiv.height(mapConfig.mapHeight);
          }
          $mapDiv.show();

          var gmap = new google.maps.Map(mapDiv, mapOptions);

          var marker = new google.maps.Marker({
              position: location,
              map: gmap,
              animation: google.maps.Animation.DROP,
              draggable: false
          });
        };

        var googleMapLoadScript = function() {
          var script = null;
          if (!window.jspMapInitHandlers) {
            window.jspMapInitHandlers = [];

            script = document.createElement('script');
            script.type = 'text/javascript';
            script.src = 'https://maps.googleapis.com/maps/api/js?v=3.exp&callback=jsMapInitGMap';
            if (mapConfig.apiKey) {
              script.src += '&key=' + encodeURIComponent(mapConfig.apiKey);
            }
            if (mapConfig.region) {
              script.src += '&region=' + encodeURIComponent(mapConfig.region);
            }
            if (mapConfig.language) {
              script.src += '&language=' + encodeURIComponent(mapConfig.language);
            }
            window.jspMapInitHandlers.push(googleMapLoadCallback);
            window.jsMapInitGMap = function() {
              api.log('jsMapInitGMap called');
              for (var idx = 0; idx < window.jspMapInitHandlers.length; idx++) {
                var f = window.jspMapInitHandlers[idx];
                api.log('call init idx: ' + idx);
                f.apply(selfWidget, []);
              }
            };
            api.log('add script: ' + script.src);
            document.body.appendChild(script);
          } else {
            api.log('gmap script already loaded, adding new callback');
            window.jspMapInitHandlers.push(googleMapLoadCallback);
          }
        };

        var googleMapLoadCallback = function() {
          api.log('gmap callback called');
          api.initMap();
        };

        return api;
    };
}(this));
