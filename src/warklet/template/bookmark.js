javascript:(function () {
  var newScript = document.createElement('script');
  newScript.type = 'text/javascript';
  newScript.src = '{{script-url}}';
  document.getElementsByTagName('body')[0].appendChild(newScript);
})();