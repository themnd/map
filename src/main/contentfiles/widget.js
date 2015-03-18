atex.onecms.register('ng-directive', 'mapWidget', [], function() {
  return ['$window', '$q', function($window, $q) {
    return {
      replace: false,
      restrict: 'AE',

      scope: {
        'config': '=',
        'baseUrl': '@',
        'domainObjects': '=',
        'widgetId': '@'
      },

      templateUrl: atex.onecms.baseUrl + '/template.html',

      controller: function($scope) {
        $scope.unbindFunctions = [];
        $scope.domainObject = $scope.domainObjects['point'];
        if ($scope.domainObject === undefined) {

          // try to signal how to heal the most common error!

          var aspectData = "\n" +
            "\"aspects\": {\n" +
            "   \"com.atex.plugins.map.MapAspectBean\": {\n" +
            "     \"type\": \"com.atex.plugins.map.MapAspectBean\"\n" +
            "   }\n" +
            "}\n";

          console.log("you should add the following object to your template.json (i.e. article.json)");
          console.log(aspectData);

        } else {

          $scope.data = $scope.domainObject.getData();
          $scope.hasAnyPoints = ($scope.data.points.length > 0);
          $scope.pointIndex = 0;
          $scope.initDefaultLocation = false;
          $scope.geo = {};
        }
      },

      link: function(scope, element, attrs) {

        scope.log = function(msg) {
          if (console.log) {
            console.log(msg);
          }
        }

        scope.loadGoogleMaps = function(f) {

          scope.log('loadGoogleMaps');

          var script = window.document.createElement('script');
          script.type = 'text/javascript';
          script.src = 'https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=initMap';
          document.body.appendChild(script);

          $window.initMap = function() {
            f();
          }
        }

        scope.editMarkerDragCallback = function(scope, myMarker) {
          return function () {
            var pos = myMarker.getPosition();
            scope.log('edit marker dragged');
            scope.data.points[scope.pointIndex].latitude = String(pos.lat());
            scope.data.points[scope.pointIndex].longitude = String(pos.lng());
            scope.log(scope.data.points[scope.pointIndex]);
            if(!scope.$$phase) {
              scope.$apply();
            }
          };
        }

        scope.zoomChangedCallback = function(scope, myMap) {
          return function () {
            var zoom = myMap.getZoom();
            scope.data.points[scope.pointIndex].zoom = String(zoom);
            if(!scope.$$phase) {
              scope.$apply();
            }
          };
        }

        scope.addNewPoint = function() {
          var defLatitude = scope.config.latitude || 45.52976525113116;
          var defLongitude = scope.config.longitude || 9.145197638317882;
          var defZoom = scope.config.zoom || 7;

          if (scope.map) {
            var center = scope.map.getCenter();
            defLatitude = center.lat();
            defLongitude = center.lng();
            defZoom = scope.map.getZoom();
          }

          scope.log("insert default empty point");
          var newPoint = {
            _type: "com.atex.plugins.map.MapPoint",
            latitude: String(defLatitude),
            latitude_type: "java.lang.String",
            longitude: String(defLongitude),
            longitude_type: "java.lang.String",
            zoom: String(defZoom),
            zoom_type: "java.lang.String"
          };

          scope.data.points.push(newPoint);
          scope.domainObject.setData(scope.data);
          scope.domainObject.changed();
          scope.hasAnyPoints = (scope.data.points.length > 0);
          scope.initMap();
        }

        scope.clearPoints = function() {
          while (scope.data.points.length > 0) {
            scope.data.points.pop();
          }
          scope.domainObject.setData(scope.data);
          scope.domainObject.changed();
          scope.hasAnyPoints = (scope.data.points.length > 0);
          scope.initMap();
        }

        scope.setupAutoComplete = function() {
          scope.log('setupAutoComplete');

          var inputClass = 'withautocomplete';
          var map = scope.map;
          var input = $(element).find('.mapWidget-location');

          // make sure we did not initialize twice.

          if (input.hasClass(inputClass)) {
            return;
          }

          var autocomplete = new google.maps.places.Autocomplete(input[0]);
          autocomplete.bindTo('bounds', map);

          input.keypress(function(e) {
            if (e.which == 13) {
              e.preventDefault();
              scope.log('fire place_changed');
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

              scope.log(address);
            }
          });

          input.addClass(inputClass);
        }

        scope.watchPoint = function() {
          scope.unwatchPoint();
          var pointWatched = 'data.points[' + scope.pointIndex + ']';
          var unbindLongitude = scope.$watch(pointWatched + '.longitude', function(newValue, oldValue) {
            if (newValue !== oldValue) {
              scope.domainObject.setData(scope.data);
              scope.domainObject.changed();
            }
          });
          var unbindLatitude = scope.$watch(pointWatched + '.latitude', function(newValue, oldValue) {
            if (newValue !== oldValue) {
              scope.domainObject.setData(scope.data);
              scope.domainObject.changed();
            }
          });
          var unbindZoom = scope.$watch(pointWatched + '.zoom', function(newValue, oldValue) {
            if (newValue !== oldValue) {
              scope.domainObject.setData(scope.data);
              scope.domainObject.changed();
            }
          });
          scope.unbindFunctions = [
            unbindLongitude,
            unbindLatitude,
            unbindZoom
          ];
        }

        scope.unwatchPoint = function() {
          for (var idx = 0; idx < scope.unbindFunctions.length; idx++) {
            var f = scope.unbindFunctions[idx];
            f();
          }
          scope.unbindFunctions = [];
        }

        scope.addPoint = function() {
          scope.log('addpoint');
          scope.addNewPoint();
        }

        scope.delPoint = function() {
          scope.log('delpoint');
          scope.clearPoints();
        }

        scope.changeLocation = function() {
          scope.geodecodeLocation(scope.locationModel);
        }

        scope.geodecodeLocation = function(location) {
          var atexError = $window.atex.onecms.error;

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
                  if (scope.hasAnyPoints) {
                    scope.data.points[scope.pointIndex].latitude = String(pos.lat());
                    scope.data.points[scope.pointIndex].longitude = String(pos.lng());
                  }

                  if (scope.map) {
                    scope.map.setCenter(pos);
                  }
                  if (scope.marker) {
                    scope.marker.setPosition(pos);
                  }
                  if(!scope.$$phase) {
                    scope.$apply();
                  }
                } else {

                  var e = atexError.Error({
                    origin: 'atex.onecms.Widget-mapWidget',
                    severity: atexError.severities.NORMAL,
                    type: atexError.types.NOT_FOUND,

                    cause: "No results for '" + location + "'."
                  });

                  scope.log('nothing found for ' + location + ": " + e);

                  throw e;

                }
              } else {

                var e = atexError.Error({
                  origin: 'atex.onecms.Widget-mapWidget',
                  severity: atexError.severities.CRITICAL,
                  type: atexError.types.SERVER_ERROR,

                  cause: "No results for '" + location + "' due to '" + status + "'."
                });

                scope.log("Geocode was unsuccessful due to: " + status + ": " + e);

                throw e;
              }
            }
          );
        }

        scope.initMap = function() {

          scope.log('scope.initMap');

          var pt;
          if (scope.hasAnyPoints) {
            pt = scope.data.points[scope.pointIndex];
            scope.watchPoint();
          } else {
            pt = {
              latitude: (scope.geo.latitude || scope.config.latitude || 45.503557) + '',
              longitude: (scope.geo.longitude || scope.config.longitude || 9.184872) + '',
              zoom: (scope.config.zoom || 7) + ''
            };
            scope.unwatchPoint();

            if (!scope.initDefaultLocation) {
              if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                  if (position) {
                    scope.geo.latitude = position.coords.latitude;
                    scope.geo.longitude = position.coords.longitude;
                    scope.initDefaultLocation = true;
                    scope.initMap();
                  }
                });
              } else {
                scope.log("geolocation not available");
              }
            }
          }

          var latitude = pt.latitude && parseFloat(pt.latitude, 10) || 45.503557;
          var longitude = pt.longitude && parseFloat(pt.longitude, 10) || 9.184872;

          var location = new google.maps.LatLng(latitude, longitude);

          var mapOptions = {
            zoom: parseInt(pt.zoom, 10) || 7,
            disableDefaultUI: true,
            panControl: true,
            scaleControl: true,
            streetViewControl: false,
            zoomControl: true,
            center: location,
            mapTypeId: google.maps.MapTypeId.ROADMAP
          };
          google.maps.visualRefresh = true;
          var el = $(element).find('.mapWidget-google-map')[0];
          $(el).height(scope.config.height);
          scope.map = new google.maps.Map(el, mapOptions);

          if (scope.hasAnyPoints) {
            scope.marker = new google.maps.Marker({
              position: location,
              map: scope.map,
              draggable: true
            });

            google.maps.event.addListener(scope.marker, 'dragend', scope.editMarkerDragCallback(scope, scope.marker));
            google.maps.event.addListener(scope.map, 'zoom_changed', scope.zoomChangedCallback(scope, scope.map));
          }
          scope.drawButtons();
        }

        scope.drawButtons = function() {
          var addBtn = $(element).find('.mapWidget-addPoint')[0];
          var delBtn = $(element).find('.mapWidget-delPoint')[0];
          scope.log('hasAnyPoints: ' + scope.hasAnyPoints);
          if (scope.hasAnyPoints) {
            $(addBtn).hide();
            $(delBtn).show();
          } else {
            $(addBtn).show();
            $(delBtn).hide();
          }
        }

        if (window.google && window.google.maps) {
          scope.log('gmaps already loaded');

          // Google map already loaded
          scope.initMap();
        } else {

          scope.log('gmaps to be loaded');

          scope.loadGoogleMaps(function() {
              scope.initMap();
          });
        }

        scope.domainChangeFinalizer = scope.domainObject.on('onecms:changed', function(event, modifierId) {
          if (modifierId !== scope.widgetId) {
            scope.data = scope.domainObject.getData();
          }
        });

        scope.$on('$destroy', function() {
          if (typeof scope.domainChangeFinalizer !== 'undefined') {
            scope.domainChangeFinalizer();
          }
        });

      }
    };
  }];
});
