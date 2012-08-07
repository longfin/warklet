javascript:(function () {
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = 'http://localhost:8080/users/5013b03813902fd1bfdedf2f/script';
    if (newScript.addEventListener) {
        newScript.addEventListener("error", function(e){
            alert("Fail to load init script...");
        }, false);
    }
    document.getElementsByTagName('body')[0].appendChild(newScript);
})();
