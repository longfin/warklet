javascript:(function () {
  var newScript = document.createElement('script');
  newScript.type = 'text/javascript';
  newScript.src = 'http://warklet-dev.elasticbeanstalk.com/user/{{user-id}}/script';
  document.getElementsByTagName('body')[0].appendChild(newScript);
})();