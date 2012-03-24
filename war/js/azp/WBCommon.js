 var openChannel = function() {
    
    var channel = new goog.appengine.Channel(token);
    var handler = {
      'onopen': onOpened,
      'onmessage': onMessage,
      'onerror': function(e) {
			console.log("channel error",e);
        },
      'onclose': function(c) {
        	console.log("channel close",c);
       }
    };
    var socket = channel.open(handler);
   	console.log(socket);
    socket.onopen = onOpened;
    socket.onmessage = onMessage;
  };
  
  
  var doGfxTouchStart = function(evt){
 	//dojo.byId("evtOut").innerHTML = 'start touches:' + evt.changedTouches.length;// + '  targetTouches:' + dojo.toJson(evt.targetTouches);
 	doGfxMouseDown(evt.changedTouches[0]);
 	
  }; 
  
  var doGfxTouchEnd = function(evt){
  
 	//dojo.byId("evtOut").innerHTML = 'end touches:' + dojo.toJson(evt.touches) + '  targetTouches:' + dojo.toJson(evt.targetTouches);
 	doGfxMouseUp(evt.changedTouches[0]);
  }; 
  
  var doGfxTouchMove = function(evt){
	  evt.preventDefault();
 	//dojo.byId("evtOut").innerHTML = 'move touches:';// + dojo.toJson(evt.touches) + '  targetTouches:' + dojo.toJson(evt.targetTouches);
 	doGfxMouseMove(evt.changedTouches[0]);
  }; 