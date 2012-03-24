(function() {
  var app_id = "https://chrome.google.com/webstore/detail/kioencljenicfebobkjlgckndibecajp";
  var app_name = "";
  var message = "You can also try our app in the Chrome Web Store"; 
  var install_message = ""; 
  var install_image = "data:image/png;base64,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";
  var cancel_message = "";
  var cancel_image = "http://cws-badge.appspot.com/images/cancel.png";
  var css_url = "data:text/css;base64,Ll93ZWJzdG9yZV9iYWRnZSB7CiAgd2lkdGg6IDEyOHB4OwogIGhlaWdodDogMTkwcHg7CiAgdG9wOiAwcHg7CiAgcmlnaHQ6IDBweDsKICBwb3NpdGlvbjogYWJzb2x1dGU7CiAgbWFyZ2luOiAwIDUwcHggMCA1MHB4OwogIHBhZGRpbmc6IDVweDsKICBib3gtc2hhZG93OiAjMDAwIDAgMCA1cHg7CiAgYmFja2dyb3VuZC1jb2xvcjogI2VhZTg4YzsKICBib3JkZXI6IHNvbGlkIDFweCAjYzljNzc4OwogIGJvcmRlci1ib3R0b20tbGVmdC1yYWRpdXM6IDVweDsKICBib3JkZXItYm90dG9tLXJpZ2h0LXJhZGl1czogNXB4OwogIGRpc3BsYXk6IGlubGluZTsKICBmb250LWZhbWlseTogQXJpYWw7CiAgb3BhY2l0eTogMTsKICAtd2Via2l0LXRyYW5zaXRpb246IGFsbCAwLjVzIGVhc2UtaW4tb3V0Owp9CgouX3dlYnN0b3JlX2JhZGdlOmhvdmVyIHsKICBiYWNrZ3JvdW5kLWNvbG9yOiAjZjJmMDkwOwp9CgouX3dlYnN0b3JlX2JhZGdlIC5fd2Vic3RvcmVfbWVzc2FnZSB7CiAgd2lkdGg6IDEwMCU7CiAgdGV4dC1zaGFkb3c6ICMxMTEgMHB4IDBweCAycHg7CiAgdGV4dC1hbGlnbjogY2VudGVyOwogIGZvbnQtd2VpZ2h0OiA3MDA7CiAgZGlzcGxheTogYmxvY2s7Cn0KCi5fd2Vic3RvcmVfYmFkZ2UgLl93ZWJzdG9yZV9pbnN0YWxsIHsKICB3aWR0aDogMTI4cHg7CiAgaGVpZ2h0OiAxMjhweDsKICBtYXJnaW4tdG9wOiA1cHg7CiAgZGlzcGxheTogYmxvY2s7Cn0KCi5fd2Vic3RvcmVfYmFkZ2UgLl93ZWJzdG9yZV9jYW5jZWwgewogIHRvcDogMDsKICByaWdodDogMnB4OwogIHBvc2l0aW9uOiBhYnNvbHV0ZTsKICBvcGFjaXR5OiAwOwogIC13ZWJraXQtdHJhbnNpdGlvbjogYWxsIDAuMnMgZWFzZS1pbi1vdXQ7Cn0KCi5fd2Vic3RvcmVfYmFkZ2U6aG92ZXIgLl93ZWJzdG9yZV9jYW5jZWwgewogIG9wYWNpdHk6IDE7Cn0=";
  var cookie_name = "_webstore__rTyf";
  var isInstalled = !!(window.chrome && window.chrome.app && window.chrome.app.isInstalled && !(window.location.host == "badgemator.appspot.com"));
  var isCancelled = !!(document.cookie.indexOf(cookie_name + "=true") >= 0);
  
  if(window.navigator.userAgent.indexOf("Chrome/") >= 0 && (!isInstalled && !isCancelled)) {
    var onloaded = function() {
      var container = document.createElement("span");
      container.className = "_webstore_badge";
      
      document.styleSheets[0].addRule("._webstore_badge", "opacity:0", 0);
      
      if(message) {
        var message_element = document.createElement("span");
        message_element.className = "_webstore_message";
        message_element.innerText = message;
        container.appendChild(message_element);
      }
      
      var link = document.createElement("a");
      link.href = url;
      link.target = "_blank";
      link.className = "_webstore_install";
      container.appendChild(link);
       
      if(install_message) {
        link.innerText = install_message;
        link.alt = "Installs " + app_name; 
      }
      else if(install_image) {
        var img = new Image();
        img.src = install_image;
        link.appendChild(img);
        link.alt = "Installs " + app_name; 
      }
      
      var close = document.createElement("a");
      close.className = "_webstore_cancel";
      close.href = "#";
      
      close.addEventListener("click", function(e) {
        // Set a cookie, that we will respect if the user cancels.
        container.style.display = "none";
        document.cookie = cookie_name + "=true";
        e.preventDefault();
        return false;
      });
      if(cancel_message) {
        close.innerText = cancel_message;
      }
      else if(cancel_image) {
        var img = new Image();
        img.src = cancel_image;
        
        img.alt = "Cancel Prompt"; 
        close.appendChild(img);
      }
      container.appendChild(close);
      
      if(css_url) {
        var css_link = document.createElement("link");
        css_link.rel = "stylesheet";
        css_link.title = "webstore_badge";
        css_link.href = css_url;
        document.head.appendChild(css_link);
      }
      document.body.appendChild(container);
    };
    
    if(document.readyState == "complete") {
      onloaded();
    }
    else {
      document.addEventListener("DOMContentLoaded", onloaded);
    }
  }
})();