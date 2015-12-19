var exec = require('cordova/exec');

var TwitterDigits = {
	login: function (successCallback, errorCallback) {
		exec(successCallback, errorCallback, 'TwitterDigits', 'login', []);
	}
};

module.exports = TwitterDigits;