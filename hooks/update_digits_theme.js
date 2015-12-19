#!/usr/bin/env node
 
//
// This hook copies the digits custom theme for android to the correct platform folder and therefore reflects the changes at the plugins folder into the platform.
 
var filestocopy = [{
    "plugins/twitter-digits-plugin/digitsCustomTheme.xml": 
    "platforms/android/res/values/digitsCustomTheme.xml"
},
{
    "digitsCustomTheme.xml": 
    "platforms/android/res/values/digitsCustomTheme.xml"
}, ];
 
var fs = require('fs');
var path = require('path');
 
// no need to configure below
var rootdir = process.argv[2];
 
filestocopy.forEach(function(obj) {
    Object.keys(obj).forEach(function(key) {
        var val = obj[key];
        var srcfile = path.join(rootdir, key);
        var destfile = path.join(rootdir, val);
        //console.log("copying "+srcfile+" to "+destfile);
        var destdir = path.dirname(destfile);
        if (fs.existsSync(srcfile) && fs.existsSync(destdir)) {
            fs.createReadStream(srcfile).pipe(
               fs.createWriteStream(destfile));
        }
    });
});