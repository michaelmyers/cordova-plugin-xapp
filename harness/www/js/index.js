/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');

        document.getElementById('startSessionButton').addEventListener('click', function () {
            app.startSession();
        });

        document.getElementById('requestAdButton').addEventListener('click', function () {
            app.requestAd();
        });
        
        XAPP.version(function (version) {
            document.getElementById("version").textContent =  version;
        });
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        console.log('Received Event: ' + id);

        document.getElementById(id).setAttribute('style', 'display:block;');
    },
    
    startSession: function () {

        XAPP.startSession('b454146b-0b0f-4f16-91d5-9637ccddca10', '22e1c6db-94a7-4348-a3c4-c3c562f27860', function() {
            document.getElementById("startSessionStatus").textContent = 'XAPP Started';
        }, function (error) {
            document.getElementById("startSessionStatus").textContent = 'Error: ' + error;
        });
    },
    
    requestAd: function () {

        XAPP.requestAd(function () {
            document.getElementById("requestAdStatus").textContent = 'Ad Received';
        }, function (error) {
            document.getElementById("requestAdStatus").textContent = 'Error: ' + error;
        });
    },

    playAd: function () {

        XAPP.playAd(function () {

        }, function (error) {

        });
    }
};

app.initialize();