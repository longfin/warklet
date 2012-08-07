javascript:(function () {
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = '{{script-url}}';
    if (location.protocol != "http") {
        newScript.src = newScript.src.replace(/(.*):\/\//, 
                                              location.protocol+"//");
        console.log("!"+newScript.src);
        newScript.src = newScript.src.replace(/:{1}\d{1,}/, "");
        console.log("@"+newScript.src);
    }
    if (newScript.addEventListener) {
        newScript.addEventListener("error", function(e){
            alert("Fail to load init script...");
        }, false);
    }
    document.getElementsByTagName('body')[0].appendChild(newScript);
})();
