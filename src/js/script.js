(function(window){
    var postUrl = "{{url}}";
    var send = function ($) {
        var comment = prompt("Comment about...", document.title);
        $.ajax(postUrl, {
            dataType : "jsonp",
            data: $.param({
                comment: comment,
                url: location.href
            }),
            success: function(data){
				alert("Posted successfully.");
            },
            error: function(ret){
				alert("Oops, something is wrong.");
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
