function ready(callback) {
    // in case the document is already rendered
    if (document.readyState!='loading') callback();
    // modern browsers
    else if (document.addEventListener) document.addEventListener('DOMContentLoaded', callback);
    // IE <= 8
    else document.attachEvent('onreadystatechange', function() {
            if (document.readyState=='complete') callback();
        });
}

ready(function(){
    // Get the DOM nodes
    let forget = document.querySelector('#nav');
    let form = document.querySelector('#loginform');
    let sso = document.querySelector('#wpsindriaOpenIdRedirect');

    // Move stuff
    form.append(forget);

    if (sso !== null) {
        form.append(sso);
    }


});
