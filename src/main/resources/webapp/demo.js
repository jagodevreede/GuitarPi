/*jslint browser:true */
(function () {
    "use strict";

    var demos = {};

    var OSMD;
    // The folder of the demo files
    var folder = "/api/music/load/",
        zoom = 1.0,
    // HTML Elements in the page
        err,
        error_tr,
        canvas,
        select,
        zoomIn,
        zoomOut,
        custom,
        startBtn,
        stopBtn,
        firstNote;

    // Initialization code
    function init() {
        initSocket();

        var name, option;

        err = document.getElementById("error-td");
        error_tr = document.getElementById("error-tr");
        custom = document.createElement("option");
        select = document.getElementById("select");
        zoomIn = document.getElementById("zoom-in-btn");
        zoomOut = document.getElementById("zoom-out-btn");
        canvas = document.createElement("div");
        startBtn = document.getElementById("start-btn");
        stopBtn = document.getElementById("stop-btn");

        // Hide error
        error();

        // Create select
        for (name in demos) {
            if (demos.hasOwnProperty(name)) {
                option = document.createElement("option");
                option.value = demos[name];
                option.textContent = name;
            }
            select.appendChild(option);
        }
        select.onchange = selectOnChange;

        custom.appendChild(document.createTextNode("Custom"));

        // Create zoom controls
        zoomIn.onclick = function () {
            zoom *= 1.2;
            scale();
        };
        zoomOut.onclick = function () {
            zoom /= 1.2;
            scale();
        };

        // Create OSMD object and canvas
        OSMD = new opensheetmusicdisplay.OSMD(canvas);
        OSMD.setLogLevel('info');
        document.body.appendChild(canvas);

        // Set resize event handler
        new Resize(
            function(){
                disable();
            },
            function() {
                var width = document.body.clientWidth;
                canvas.width = width;
                try {
                OSMD.render();
                } catch (e) {}
                enable();
            }
        );

        window.addEventListener("keydown", function(e) {
            var event = window.event ? window.event : e;
            if (event.keyCode === 39) {
                OSMD.cursor.next();
            }
        });
        startBtn.addEventListener("click", function() {
          $.post( "/api/music/start");
        });
        stopBtn.addEventListener("click", function() {
            $.post( "/api/music/stop");
        });
    }

    function initSocket() {
        var loc = window.location, new_uri;
        if (loc.protocol === "https:") {
            new_uri = "wss:";
        } else {
            new_uri = "ws:";
        }
        new_uri += "//" + loc.host;
        new_uri += loc.pathname + "status-ws";
        var connection = new WebSocket(new_uri);

        connection.onmessage = function (e) {
          console.log('Server: ' + e.data);
          if ("start" === e.data) {
             OSMD.cursor.show();
             OSMD.cursor.reset();
             firstNote = true;
          } else if ("stop" === e.data) {
             OSMD.cursor.hide();
          } else if ("next" === e.data) {
             if (!firstNote) {
                OSMD.cursor.next();
             } else {
                firstNote = false;
             }
          } else {
             console.log('Unknown command ' + e.data);
          }
        };
    }

    function Resize(startCallback, endCallback) {

      var rtime;
      var timeout = false;
      var delta = 200;

      function resizeEnd() {
        timeout = window.clearTimeout(timeout);
        if (new Date() - rtime < delta) {
          timeout = setTimeout(resizeEnd, delta);
        } else {
          endCallback();
        }
      }

      window.addEventListener("resize", function () {
        rtime = new Date();
        if (!timeout) {
          startCallback();
          rtime = new Date();
          timeout = window.setTimeout(resizeEnd, delta);
        }
      });

      window.setTimeout(startCallback, 0);
      window.setTimeout(endCallback, 1);
    }

    function selectOnChange(str) {
        error();
        disable();
        var isCustom = typeof str === "string";
        if (!isCustom) {
            str = folder + select.value;
        }
        zoom = 1.0;

        OSMD.load(str).then(
            function() {
                return OSMD.render();
            },
            function(e) {
                error("Error reading sheet: " + e);
            }
        ).then(
            function() {
                return onLoadingEnd(isCustom);
            }, function(e) {
                error("Error rendering sheet: " + e);
                onLoadingEnd(isCustom);
            }
        );
    }

    function onLoadingEnd(isCustom) {
        // Remove option from select
        if (!isCustom && custom.parentElement === select) {
            select.removeChild(custom);
        }
        // Enable controls again
        enable();
    }

    function scale() {
        disable();
        window.setTimeout(function(){
            OSMD.zoom = zoom;
            OSMD.render();
            enable();
        }, 0);
    }

    function error(errString) {
        if (!errString) {
            error_tr.style.display = "none";
        } else {
            err.textContent = errString;
            error_tr.style.display = "";
            canvas.width = canvas.height = 0;
            enable();
        }
    }

    // Enable/Disable Controls
    function disable() {
        document.body.style.opacity = 0.3;
        select.disabled = zoomIn.disabled = zoomOut.disabled = "disabled";
    }
    function enable() {
        document.body.style.opacity = 1;
        select.disabled = zoomIn.disabled = zoomOut.disabled = "";
    }

    // Register events: load, drag&drop
    window.addEventListener("load", function() {
        $.get( "/api/music/list", function( data ) {
            var arrayLength = data.length;
            for (var i = 0; i < arrayLength; i++) {
                demos[data[i]] = data[i]
            }
            init();
            selectOnChange();
        });

    });

}());
