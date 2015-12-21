# twitter-digits-plugin

Cordova plugin to use Twitter Digits single sign on with a phone number.
The plugin bases on the following docs: https://docs.fabric.io/android/digits/digits.html

### Install

##### Get Fabric API & Digits keys for your app

Information about how to get these keys can be found here [https://fabric.io](https://fabric.io) and here [https://github.com/twitter/digits-android](https://github.com/twitter/digits-android).

##### Add the plugin to your Cordova or Ionic app
`cordova plugin add https://github.com/nifino/twitter-digits-plugin.git --variable FABRIC_KEY=<Fabric API Key>`

or

`ionic plugin add https://github.com/nifino/twitter-digits-plugin.git --variable FABRIC_KEY=<Fabric API Key>`

##### Adapt the project's config.xml file
Open the `config.xml` file located in your project's root directory and add the following two lines before the closing ```</widget>``` tag:
````
<preference name="TwitterConsumerKey" value="<Twitter Consumer Key>" />
<preference name="TwitterConsumerSecret" value="<Twitter Consumer Secret>" />
````

##### Customize Digits Activity themes

A template file named 'digitsCustomTheme.xml' is provided by the plugin that can be used a starting point for customizing the themes.

If you want to use a custom theme for the Digits android activities you can specify this by inserting the following line in the config.xml (as described above):
````
<preference name="CustomTheme" value="CustomDigitsTheme" />
````

That your changes in the theme file get automatically updated in the platform folder please copy the hook provided in '/plugins/twitter-digits-plugin/hooks/digitsCustomTheme.js'
to the global hooks folder of your project '/hooks/after_prepare/'. Add a unique three digit prefix to the JS file so that it is named like the existing hooks, e.g. '020_digitsCustomTheme.js'.

Additional information is provided here [https://docs.fabric.io/android/digits/theming.html](https://docs.fabric.io/android/digits/theming.html).

### Usage

This plugin adds an object to the window named TwitterDigits.

##### Login

Login using the `.login` method:

````
TwitterDigits.login(function(result) {
    console.log('Successful login!');
    console.log(result);
  }, function(error) {
    console.log('Login caused an error');
    console.log(error);
  }
);
````

The login reponse object is defined as:
````
{
  userId: '<Twitter User Id>',
  secret: '<Twitter OAuth Secret>',
  token: '<Twitter Oauth Token>',
  phoneNumber: '<Phone Number>'
}
````
