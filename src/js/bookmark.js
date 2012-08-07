javascript:(function () {
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = '{{script-url}}';
    if (newScript.addEventListener) {
        newScript.addEventListener("error", function(e){
            alert("Fail to load init script...");
        }, false);
    }
    document.getElementsByTagName('body')[0].appendChild(newScript);
})();
