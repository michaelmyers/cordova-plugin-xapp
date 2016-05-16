cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-xapp/www/XAPP.js",
        "id": "cordova-plugin-xapp.XAPP",
        "pluginId": "cordova-plugin-xapp",
        "clobbers": [
            "window.XAPP"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.2.2",
    "cordova-plugin-xapp": "2.17.0"
}
// BOTTOM OF METADATA
});