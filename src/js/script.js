(function(window){
//    var accessToken = "{{access-token}}";
    var accessToken = "a24ca6c9c4e9c0b1c6fce9b612c282bac6bd33fc76947f24f550acb73a64e0d43f84b6d97d4a72a6aedb6795a78f1df9acd9e98612b89372b983ae378faa";
//    var postUrl = "{{url}}";
    var postUrl = "http://localhost:8080/post";
    var send = function ($) {
        $.ajax(postUrl, {
            dataType : "jsonp",
            data: $.param({
                access_token: accessToken,
                url: location.href
            }),
            type: "post",
            success: function(data){
                alert(data);
            },
            complete : function(ret){
            }
        });
    }

    // check and load jquery...
    var oldJquery = null;
    if (typeof($) !== "undefined" && typeof($.fn) !== "undefined" && typeof($.fn.jquery) !== "undefined") {
        //backup older jquery object
        oldJquery = $.noConflict();
    } 
    var newScript = document.createElement('script');
    newScript.type = 'text/javascript';
    newScript.src = 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js';

    var self = this;
    if (newScript.addEventListener) {
        newScript.addEventListener("load", function(e){
            var loadedJQuery = $.noConflict();
            console.log($);
            console.log(loadedJQuery);
            if (oldJquery) {
                window.$ = oldJquery.noConflict();
            }
            send.call(self, loadedJQuery);
        }, false);
    } else {
        newScript.onreadystatechange = function() {
            if (this.readyState == "complete") {
                var loadedJQuery = $.noConflict();
                if (oldJquery) {
                    window.$ = oldJquery.noConflict();
                }
                send.call(self, loadedJQuery);
            }
        }
    }
    document.getElementsByTagName('body')[0].appendChild(newScript);
})(window);
