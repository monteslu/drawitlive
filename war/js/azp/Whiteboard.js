//dojo.provide('azp.Whiteboard');

dojo.require('dijit.form.ValidationTextBox');
dojo.require('dijit.form.Button');
dojo.require('dijit.Dialog');
dojo.require('dijit.layout.BorderContainer');
dojo.require('dijit.layout.ContentPane');
dojo.require("dojox.gfx");
dojo.require("dojo.fx");
dojo.require("dojox.gfx.move");
dojo.require("dojo.NodeList-fx");
//dojo.require("dijit.ColorPalette");
dojo.require("dojox.widget.ColorPicker");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.TooltipDialog");
dojo.require("dijit.form.RadioButton");
dojo.require("dijit.form.Select");
dojo.require("dijit.form.Form");
dojo.require("dijit.form.Slider");

var tools = [{name: 'line', showLineColor: true, showLineThickness: true}
			,{name: 'pen', showLineColor: true, showLineThickness: true}
			,{name: 'rect', showLineColor: true, showLineThickness: true}
			,{name: 'ellipse', showLineColor: true, showLineThickness: true}
			,{name: 'filledRect', showFillColor: true, showLineColor: true, showLineThickness: true}
			,{name: 'filledEllipse', showFillColor: true, showLineColor: true, showLineThickness: true}
			,{name: 'text', showLineColor: true, showFontSize: true}
			,{name: 'delete'}
			
			,{name: 'move'}
			,{name: 'moveUp'}
			
			,{name: 'moveDown'}
			];

var whiteboard = {
	width : 700,
	height : 400,
	container : null,
	drawing: null,
	overlayContainer : null,
	overlayDrawing: null,
	lineColor : "#000000",
	fillColor : "#FFFFFF",
	lineStroke : 3,
	fontSize : 12,
	pingInterval : 180000,
	userCheckInterval : 600000,
	tool : 'pen',
	points : [],
	mouseDown : false

};

whiteboard.sendMessage = function(message){
	   dojo.xhrPost({
	        url: '/wbpost',
	        content: {
	    	   wbId: wbId,
	           data: dojo.toJson(message)
	       },
	        load: function(resp){
				console.log("post response",resp);
				if(resp.message){
					messageList.push(resp.message);
				}
				clearChatUI();
	       },
	       error: function(e){
				console.info("post error",e);
				clearChatUI();
	       },
	       handleAs: "json",
	       preventCache: true
	    });
	};

  whiteboard.pingServer = function() {

	    dojo.xhrPost({
	        url: '/wbping',
	        content: {
	    	   wbId: wbId
	    	},
	        load: function(resp){
				if(resp.error)
				{
					console.info("error pinging server",resp.error);
				}
				setTimeout ( "whiteboard.pingServer()", whiteboard.pingInterval);
	       },
	       error: function(e){
				console.info("post error on pinging server",e);
				setTimeout ( "whiteboard.pingServer()", whiteboard.pingInterval);
	       },
	       handleAs: "json",
	       preventCache: true
	    });
	  };
	  
  var sendEmail = function() {
	  
	  console.log('send');
	    dojo.xhrPost({
	        url: '/wbmail',
	        content: {
	    	   wbId: wbId,
	    	   email: dijit.byId('email').getValue()
	    	},
	        load: function(resp){
				if(resp.error)
				{
					console.info("error on email server",resp.error);
				}
				
	       },
	       error: function(e){
				console.info("post error email server",e);
	       },
	       handleAs: "json",
	       preventCache: true
	    });
  };


 var getUserList = function() {

		    dojo.xhrPost({
		        url: '/wbgetUsers',
		        content: {
		    	   wbId: wbId
		    	},
		        load: function(resp){
					if(resp.error)
					{
						console.info("error getting users",resp.error);						
					}
					populateUserList ( resp.userList);
					setTimeout ( "getUserList()", whiteboard.userCheckInterval);
		       },
		       error: function(e){
					console.info("post error on gettingUsers",e);	
					setTimeout ( "getUserList()", whiteboard.userCheckInterval);
		       },
		       handleAs: "json",
		       preventCache: true
		    });
		  };

var	populateUserList = function(userList){
		try{
			var output = '';
			dojo.forEach(userList,function(user){
				output += ('<span id=\"userListItem' + user + '\" style=\"background-color: #FFFFFF;\">' + user + '</span><br>'); 
			});
			dojo.byId("userListDiv").innerHTML = output;
		}catch(e){
			console.info("error filling user list div",e);
		}
	};
	
var animateUserItem = function(user){
		try{
			var userNode = dojo.byId("userListItem" + user);
			if(userNode){
				dojo.animateProperty({
		            node: userNode,
		            duration: 750,
		            properties: {
		                backgroundColor: {
		                    start: "red",
		                    end: "white"
		                },
		                color: {
		                    start: "white",
		                    end: "black"
		                }
		            }
		        }).play();
			}

		}catch(e)
		{
			console.info("couldn\'t animate " + user, e);
		}
		
	};

var clearChatUI = function(){
	try{
	  dijit.byId('chatText').setAttribute('disabled',false);
	  dijit.byId('chatText').setValue('');
	  dijit.byId('chatBtn').setAttribute('disabled',false);
	  dojo.byId('chatWaitMessage').innerHTML = '';
	}catch(e){}
  };


  
var onOpened = function() {
	  
	  
	  dojo.byId('setUserDiv').style.display = 'none';
	  
	  
	  dojo.byId('applicationArea').style.display = '';
	  dijit.byId('applicationArea').resize();
	  initGfx();
	  //whiteboard.sendMessage({chatMessage:'I\'m here!'});
	  dojo.connect(dijit.byId('chatBtn'),'onClick',sendChatMessage);
	  
	  //display any saved messages
	  dojo.forEach(messageList,function(m){
		  if(m.chatMessage){
			  printChatMessage(m);
		  }
	  }); 
	  
	  whiteboard.pingServer();
	  getUserList();
    
  };
  
 var printChatMessage = function(message){
	  
	  chatMessageList.push('<pre class=\"chatMessage\"><span class=\"chatFrom\">' + message.fromUser + '</span>: ' + message.chatMessage + '</pre><br>');
		if(chatMessageList.length > messageMax){
			chatMessageList.shift();
		}
		
		var messageListStr = '';
		for(var i=0; i < chatMessageList.length; i++){
			messageListStr += chatMessageList[i];
		}
		dojo.byId('output').innerHTML= messageListStr;
		dojo.byId('output').scrollTop = dojo.byId('output').scrollHeight;
	  
  };
  
 var onMessage = function(m) {
	console.log("onMessage", m);

	var obj = dojo.fromJson(m.data);
	console.log(obj);
	if(obj.chatMessage){
		printChatMessage(obj);
	}
	if(obj.geometry && obj.geometry.shapeType){
		obj.geometry.fromUser = obj.fromUser;
		if(obj.fromUser != userName){
			drawFromJSON(obj.geometry,whiteboard.drawing);
		}
	}

	if(obj.chatMessage || obj.geometry)
	{
		messageList.push(obj);
	}
	
	if(obj.userList && (obj.userList.length > 0)){
		populateUserList(obj.userList);
	}
	
	if(obj.fromUser){
		//animateUserItem(obj.fromUser);
	}
		
	
  };
  
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
  
 var initGfx = function(){
	  whiteboard.container = dojo.byId("whiteboardContainer");
	  whiteboard.overlayContainer = dojo.byId("whiteboardOverlayContainer");
	  

	  whiteboard.drawing = dojox.gfx.createSurface(whiteboard.container, whiteboard.width, whiteboard.height);
	  whiteboard.overlayDrawing = dojox.gfx.createSurface(whiteboard.overlayContainer, whiteboard.width, whiteboard.height);
	  
	  
	  //for playback
	  whiteboard.movieContainer = dojo.byId("movieWhiteboardContainer");
	  whiteboard.movieDrawing = dojox.gfx.createSurface(whiteboard.movieContainer, whiteboard.width, whiteboard.height);
	  
	  
	  //draw any saved objects
	  dojo.forEach(messageList,function(m){
		  if(m.geometry){
			  m.geometry.fromUser = m.fromUser;
			 drawFromJSON(m.geometry, whiteboard.drawing);
		  }
	  }); 
	  
	  
	  whiteboard.overlayContainer.style.width = whiteboard.width + 'px';
	  whiteboard.overlayContainer.style.height = whiteboard.height + 'px';
	  whiteboard.container.style.width = whiteboard.width + 'px';
	  whiteboard.container.style.height = whiteboard.height + 'px';
	  
	  whiteboard.movieContainer.style.width = whiteboard.width + 'px';
	  whiteboard.movieContainer.style.height = whiteboard.height + 'px';
	  
	  whiteboard.overlayContainer.style.position = 'absolute';
	  whiteboard.overlayContainer.style.zIndex = 1;
	  var c = dojo.coords(whiteboard.container);
	  console.dir(c);
	  dojo.style(whiteboard.overlayContainer,"top", (c.t + 'px'));
	  dojo.style(whiteboard.overlayContainer,"left", (c.l + 'px'));
	  
	  dojo.connect(dojo.body(),'onmouseup',doGfxMouseUp); //mouse release can happen anywhere in the container
	  dojo.connect(whiteboard.overlayContainer,'onmousedown',doGfxMouseDown);
	  //dojo.connect(dojo.body(),'onmousemove',doGfxMouseMove);
	  dojo.connect(whiteboard.overlayContainer,'onmousemove',doGfxMouseMove);
	  console.log("topov",dojo.style(whiteboard.overlayContainer,"top"));
	  
	   if(Modernizr.draganddrop){
		   console.log('supports drag and drop!');
		   var dndc = new DNDFileController('whiteboardOverlayContainer');
	   }

  };
  
var addTimeRand = function(geom){
	  geom.cTime = new Date().getTime();
	  geom.cRand = Math.round(100000000000 * Math.random());
	  geom.fromUser = userName;
	  return geom;
  };

var createRectJSON = function(bounds,filled){
	bounds = normalizeBounds(bounds);
	var geom = {
			xPts: [bounds.x1,bounds.x2],
			yPts: [bounds.y1,bounds.y2]
	};
	geom.shapeType = 'rect';
	geom.filled = filled;
	if(filled){
		geom.fillColor = whiteboard.fillColor;
	}
	geom.lineColor = whiteboard.lineColor;
	geom.lineStroke = whiteboard.lineStroke;

	return addTimeRand(geom);
  };
  
  var createImageJSON = function(bounds,textData){
		bounds = normalizeBounds(bounds);
		var geom = {
				xPts: [bounds.x1,bounds.x2],
				yPts: [bounds.y1,bounds.y2],
				shapeType: 'image'
		};
		geom.dataStr = textData;

		return addTimeRand(geom);
	  };
  
  var createSelectJSON = function(bounds){
		bounds = normalizeBounds(bounds);
		var geom = {
				xPts: [bounds.x1,bounds.x2],
				yPts: [bounds.y1,bounds.y2]
		};
		geom.shapeType = 'select';
		return geom;
  };
  
  var createTextJSON = function(pt,text){
		var geom = {
				xPts: [pt.x],
				yPts: [pt.y]
		};
		geom.shapeType = 'text';
		geom.text = text;
		geom.lineStroke = whiteboard.fontSize;
		geom.lineColor = whiteboard.lineColor;
		
		return addTimeRand(geom);
	  };
  
  
  var createDeleteOverlayJSON = function(bounds){
		bounds = normalizeBounds(bounds);
		var geom = {
				xPts: [bounds.x1,bounds.x2],
				yPts: [bounds.y1,bounds.y2]
		};
		geom.shapeType = 'deleteOverlay';
		return geom;
  };
  
  var createMoveOverlayJSON = function(bounds){
		bounds = normalizeBounds(bounds);
		var geom = {
				xPts: [bounds.x1,bounds.x2],
				yPts: [bounds.y1,bounds.y2]
		};
		geom.shapeType = 'moveOverlay';
		return geom;
};

var createMoveUpOverlayJSON = function(bounds){
	var geom = createMoveOverlayJSON(bounds);
	geom.shapeType = 'moveUpOverlay';
	return geom;
};

var createMoveDownOverlayJSON = function(bounds){
	var geom = createMoveOverlayJSON(bounds);
	geom.shapeType = 'moveDownOverlay';
	return geom;
};

var createMoveJSON = function(shape,ptDelta){
	
	var geom = {
			xPts: [ptDelta.x],
			yPts: [ptDelta.y]
	};
	geom.shapeType = 'move';
	geom.cTime = shape.cTime;
	geom.cRand = shape.cRand;
	geom.fromUser = shape.fromUser;
	return geom;
};


var createMoveUpJSON = function(shape,ptDelta){
	
	var geom = {};
	geom.shapeType = 'moveUp';
	geom.cTime = shape.cTime;
	geom.cRand = shape.cRand;
	geom.fromUser = shape.fromUser;
	return geom;
};

var createMoveDownJSON = function(shape,ptDelta){
	
	var geom = {};
	geom.shapeType = 'moveDown';
	geom.cTime = shape.cTime;
	geom.cRand = shape.cRand;
	geom.fromUser = shape.fromUser;
	return geom;
};
  
var createDeleteJSON = function(shape){
		
		var geom = {};
		geom.shapeType = 'delete';
		geom.cTime = shape.cTime;
		geom.cRand = shape.cRand;
		geom.fromUser = shape.fromUser;
		return geom;
};

 var createEllipseJSON = function(bounds,filled){
	  bounds = normalizeBounds(bounds);
		var geom = {
				xPts: [bounds.x1,bounds.x2],
				yPts: [bounds.y1,bounds.y2]
		};
		geom.shapeType = 'ellipse';
		geom.filled = filled;
		if(filled){
			geom.fillColor = whiteboard.fillColor;
		}
		geom.lineColor = whiteboard.lineColor;
		geom.lineStroke = whiteboard.lineStroke;

		return addTimeRand(geom);
  };

var createLineJSON = function(bounds){
		var geom = {
				xPts: [bounds.x1,bounds.x2],
				yPts: [bounds.y1,bounds.y2]
		};
		geom.shapeType = 'line';
		geom.fillColor = whiteboard.fillColor;
		geom.lineColor = whiteboard.lineColor;
		geom.lineStroke = whiteboard.lineStroke;

		return addTimeRand(geom);
 };

var createPenJSON = function(points){
	  var xPts = [];
	  var yPts = [];
	  dojo.forEach(points, function(point){
		 xPts.push(point.x);
		 yPts.push(point.y);
	  });
		var geom = {shapeType: 'pen',
					fillColor: whiteboard.fillColor,
					lineColor: whiteboard.lineColor,
					lineStroke: whiteboard.lineStroke,
					xPts: xPts,
					yPts: yPts
				};

		return addTimeRand(geom);
};

var  createClearDrawingJSON = function(){
	var geom = {shapeType: 'clear'};
	return geom;
};


var createOffsetBB = function(origBB, pointInBB, newPt){
	//console.log('offset',origBB, pointInBB, newPt);
	
	var xDelta = Math.abs(pointInBB.x - origBB.x1);
	var yDelta = Math.abs(pointInBB.y - origBB.y1);
	
	//console.log('deltas',xDelta,yDelta);
	var bounds = {
		x1: (newPt.x - xDelta),
		y1: (newPt.y - yDelta)
	};
	
	bounds.x2 = bounds.x1 + (origBB.x2 - origBB.x1);
	bounds.y2 = bounds.y1 + (origBB.y2 - origBB.y1);

	return bounds;
};
  
  

var drawFromJSON = function(geom,drawing)
  {
	if(geom && geom.shapeType){
		var shape;
		var stroke = {color: geom.lineColor, width: geom.lineStroke};
		if(geom.shapeType == 'rect'){
			shape = drawing.createRect({x: geom.xPts[0], y: geom.yPts[0], width: (geom.xPts[1] - geom.xPts[0]), height: (geom.yPts[1] - geom.yPts[0]) });
		}
		else if(geom.shapeType == 'image'){
			//var img = new Image();
			//img.src = geom.text;
			var imgData = geom.dataStr;
			//console.log('drawImage',imgData);
			if(imgData){
				shape =  drawing.createImage({src:imgData,x: geom.xPts[0], y: geom.yPts[0], width: (geom.xPts[1] - geom.xPts[0]), height: (geom.yPts[1] - geom.yPts[0]) });
			}
			
		}
		else if(geom.shapeType == 'line'){
			shape = drawing.createLine({x1: geom.xPts[0], y1: geom.yPts[0], x2: geom.xPts[1], y2: geom.yPts[1]});
			stroke.cap = 'round';
		}
		else if(geom.shapeType == 'text'){ 
			
			shape = drawing.createText({ x:geom.xPts[0], y:geom.yPts[0] + geom.lineStroke, text:geom.text});
			shape.setFont({ size:(geom.lineStroke + "pt"), weight:"normal", family:"Arial" });
			shape.setFill(geom.lineColor);
			var width = shape.getTextWidth(geom.text);
			shape.wbbb = {
				x1: geom.xPts[0],
				y1: geom.yPts[0],
				x2: (geom.xPts[0] + width),
				y2: geom.yPts[0] + geom.lineStroke
			};
			
			
		}
		else if(geom.shapeType == 'ellipse'){
	
			shape = drawing.createEllipse({cx: ((geom.xPts[1] - geom.xPts[0])/2) + geom.xPts[0],
				 cy: ((geom.yPts[1] - geom.yPts[0])/2) + geom.yPts[0],
				 rx: (geom.xPts[1] - geom.xPts[0])/2,
				 ry: (geom.yPts[1] - geom.yPts[0])/2 });
		}
		else if(geom.shapeType == 'pen'){
			if(geom.xPts){
				if(geom.xPts.length > 1){
					//console.log("num pen points drawing:",geom.xPts.length);
					shape = drawing.createGroup();
					
					for(var i = 0; i < (geom.xPts.length - 1); i++){
						
						var lineShape = drawing.createLine({x1: geom.xPts[i], y1: geom.yPts[i], x2: geom.xPts[i + 1], y2: geom.yPts[i + 1]});
						stroke.cap = 'round';
						lineShape.setStroke(stroke);

						shape.add(lineShape);
					}
				}			
			}
		}else if(geom.shapeType == 'clear'){
			drawing.clear();
		}else if(geom.shapeType == 'delete'){
			removeShape(geom,drawing);
		}else if(geom.shapeType == 'move'){
			moveShape(geom,drawing);
		}else if(geom.shapeType == 'moveUp'){
			moveShapeUp(geom,drawing);
		}else if(geom.shapeType == 'moveDown'){
			moveShapeDown(geom,drawing);
		}
		else if(geom.shapeType == 'select'){
			
			shape = drawing.createRect({x: geom.xPts[0] - 3, y: geom.yPts[0] - 3, width: (geom.xPts[1] - geom.xPts[0] + 6), height: (geom.yPts[1] - geom.yPts[0] + 6) });
			shape.setStroke({color: new dojo.Color([0,0,255,0.75]), width: 2});
			shape.setFill(new dojo.Color([0,0,255,0.25]));
			return shape;
		}else if(geom.shapeType == 'deleteOverlay'){
			
			shape = drawing.createRect({x: geom.xPts[0] - 3, y: geom.yPts[0] - 3, width: (geom.xPts[1] - geom.xPts[0] + 6), height: (geom.yPts[1] - geom.yPts[0] + 6) });
			shape.setStroke({color: new dojo.Color([255,0,0,0.75]), width: 2});
			shape.setFill(new dojo.Color([255,0,0,0.25]));
			
			var line = drawing.createLine({x1: geom.xPts[0] - 3, y1: geom.yPts[0] - 3, x2: geom.xPts[1] + 3, y2: geom.yPts[1] + 3});
			line.setStroke({color: "#FF0000", width: 2});
			
			line = drawing.createLine({x1: geom.xPts[1] + 3, y1: geom.yPts[0] - 3, x2: geom.xPts[0] - 3, y2: geom.yPts[1] + 3});
			line.setStroke({color: "#FF0000", width: 2});
			
			return shape;
		}else if(geom.shapeType == 'moveOverlay'){
			
			shape = drawing.createRect({x: geom.xPts[0] - 3, y: geom.yPts[0] - 3, width: (geom.xPts[1] - geom.xPts[0] + 6), height: (geom.yPts[1] - geom.yPts[0] + 6) });
			shape.setStroke({color: new dojo.Color([0,0,255,0.75]), width: 2});
			shape.setFill(new dojo.Color([0,0,255,0.25]));
			
			return shape;
		}else if(geom.shapeType == 'moveUpOverlay'){
			
			shape = drawing.createRect({x: geom.xPts[0] - 3, y: geom.yPts[0] - 3, width: (geom.xPts[1] - geom.xPts[0] + 6), height: (geom.yPts[1] - geom.yPts[0] + 6) });
			//shape.setStroke({color: new dojo.Color([0,0,255,0.75]), width: 2});
			shape.setFill(new dojo.Color([0,0,255,0.15]));
			
			var line = drawing.createLine({x1: geom.xPts[0] - 5, y1: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2), x2: geom.xPts[1] + 3, y2: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2)});
			line.setStroke({color: "#0000FF", width: 2});
			
			line = drawing.createLine({x1: geom.xPts[0] - 5, y1: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2), x2: geom.xPts[0] + ((geom.xPts[1] - geom.xPts[0]) / 2), y2: geom.yPts[0] -5});
			line.setStroke({color: "#0000FF", width: 2});
			
			line = drawing.createLine({x1: geom.xPts[0] + ((geom.xPts[1] - geom.xPts[0]) / 2), y1: geom.yPts[0] -5, x2: geom.xPts[1] + 5, y2: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2)});
			line.setStroke({color: "#0000FF", width: 2});
			
			
			return shape;
		}else if(geom.shapeType == 'moveDownOverlay'){
			
			shape = drawing.createRect({x: geom.xPts[0] - 3, y: geom.yPts[0] - 3, width: (geom.xPts[1] - geom.xPts[0] + 6), height: (geom.yPts[1] - geom.yPts[0] + 6) });
			//shape.setStroke({color: new dojo.Color([0,0,255,0.75]), width: 2});
			shape.setFill(new dojo.Color([0,0,255,0.15]));
			
			var line = drawing.createLine({x1: geom.xPts[0] - 5, y1: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2), x2: geom.xPts[1] + 3, y2: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2)});
			line.setStroke({color: "#0000FF", width: 2});
			
			line = drawing.createLine({x1: geom.xPts[0] - 5, y1: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2), x2: geom.xPts[0] + ((geom.xPts[1] - geom.xPts[0]) / 2), y2: geom.yPts[1] + 5});
			line.setStroke({color: "#0000FF", width: 2});
			
			line = drawing.createLine({x1: geom.xPts[0] + ((geom.xPts[1] - geom.xPts[0]) / 2), y1: geom.yPts[1] + 5, x2: geom.xPts[1] + 5, y2: geom.yPts[0] + ((geom.yPts[1] - geom.yPts[0]) / 2)});
			line.setStroke({color: "#0000FF", width: 2});
			
			return shape;
		}
	
		if(shape){
			
			shape.cRand = geom.cRand;
			shape.cTime = geom.cTime;
			if(!shape.wbbb){
				shape.wbbb = getBoundingBox(geom);
			}
			shape.fromUser = geom.fromUser;
			
			if(geom.filled && shape.setFill){
				shape.setFill(geom.fillColor);
			}
			if(shape.setStroke && (geom.shapeType != 'text')){
				shape.setStroke(stroke);
			}
		}
	
		return shape;
	}
	
 };
 
 var getBoundingBox = function(geom){
	 
	 if(geom.xPts && geom.yPts){
		 var xs = geom.xPts;
		 var ys = geom.yPts;
		 var bb = {x1: 0, x2: -1, y1: 0, y2: -1 };
		 
		 if(xs.length > 1){
			bb.x1 = xs[0];
			bb.x2 = xs[1];
			dojo.forEach(xs, function(x){
				if(x < bb.x1){
					bb.x1 = x;
				}
				else if(x > bb.x2){
					bb.x2 = x;
				}
			});
		 }
		 
		 if(ys.length > 1){
				bb.y1 = ys[0];
				bb.y2 = ys[1];
				dojo.forEach(ys, function(y){
					if(y < bb.y1){
						bb.y1 = y;
					}
					else if(y > bb.y2){
						bb.y2 = y;
					}
				});
		 }
		 
		 return bb;
	 }
	 else{
		return null;
	 }
	 
 };

 var removeShape = function(geom, drawing){
	  var shape = getShapeFromGeom(geom,drawing);
	  if(shape){
		  drawing.remove(shape);   
	  }
	  
  };

 
var moveShape = function(geom, drawing){
	  var shape = getShapeFromGeom(geom,drawing);
	  if(shape){
		  shape.applyTransform({dx: geom.xPts[0], dy: geom.yPts[0]});
		  if(shape.wbbb){
			shape.wbbb.x1 += geom.xPts[0];
			shape.wbbb.x2 += geom.xPts[0];
			shape.wbbb.y1 += geom.yPts[0];
			shape.wbbb.y2 += geom.yPts[0];
		  }
	  }
	  
};

var moveShapeUp = function(geom, drawing){
	  var shape = getShapeFromGeom(geom,drawing);
	  if(shape){
		  shape.moveToFront();
	  }	  
};

var moveShapeDown = function(geom, drawing){
	  var shape = getShapeFromGeom(geom,drawing);
	  if(shape){
		  shape.moveToBack();
	  }	  
};
  
  
  var getShapeFromGeom = function(geom, drawing){
	  
	  var retVal = null;
	  dojo.every(drawing.children, function(shape){
		    if((shape.cRand == geom.cRand) && (shape.cTime == geom.cTime)){
		    	retVal = shape; 
		        return false;
		    }
		    return true; // keep going until we find one that isn't
		});
	  
	  return retVal;	  
  };
  
 var pointInDrawing = function(pt){
	  if((pt.x > -2) && (pt.x < (whiteboard.width + 2)) && (pt.y > -2) && (pt.y < (whiteboard.height + 2))){
		  return true;
	  }else{
		 return false;
	  }
	  
  };
  
var getGfxMouse = function(evt){
	  var coordsM = dojo.coords(whiteboard.container);
	  return {x: Math.round(evt.clientX - coordsM.x), y: Math.round(evt.clientY - coordsM.y)};
};
  
var  doGfxMouseDown = function(evt)
  {
	var pt = getGfxMouse(evt);
	//console.dir(pt);
	if(pointInDrawing(pt)){
		whiteboard.mouseDownPt = pt;
		whiteboard.points = [pt];
		whiteboard.mouseDown = true;
		
		whiteboard.selectedShape = getHoveredShape(whiteboard.drawing,pt);
	}
	
 };

var doGfxMouseMove = function(evt)
  {
	var pt = getGfxMouse(evt);
	
	if(whiteboard.mouseDown){
		if((whiteboard.tool == 'pen') && pointInDrawing(pt) ){
			if((whiteboard.points[whiteboard.points.length - 1].x != pt.x) || (whiteboard.points[whiteboard.points.length - 1].y != pt.y)){
				whiteboard.points.push(pt);
				
				if(whiteboard.points.length > 1){
				//make sure its not the same point as last time
				
					var geom = createLineJSON(
							{x1: whiteboard.points[whiteboard.points.length - 2].x,
							y1: whiteboard.points[whiteboard.points.length - 2].y,
							x2: whiteboard.points[whiteboard.points.length - 1].x,
							y2: whiteboard.points[whiteboard.points.length - 1].y }
					);
					drawFromJSON(geom,whiteboard.overlayDrawing);
				}
				
			}
		}else{
			var bounds = {x1:whiteboard.mouseDownPt.x, y1:whiteboard.mouseDownPt.y, x2: pt.x, y2: pt.y};
			if(whiteboard.tool != 'pen'){
				whiteboard.overlayDrawing.clear();
			}
			
			var geom;
			
			if(whiteboard.tool == 'rect'){
				geom  = createRectJSON(bounds,false);
				drawFromJSON(geom,whiteboard.overlayDrawing);
			}else if(whiteboard.tool == 'filledRect'){
				geom  = createRectJSON(bounds,true);
				drawFromJSON(geom,whiteboard.overlayDrawing);
			}else if(whiteboard.tool == 'ellipse'){
				geom  = createEllipseJSON(bounds,false);
				drawFromJSON(geom,whiteboard.overlayDrawing);
			}else if(whiteboard.tool == 'filledEllipse'){
				geom  = createEllipseJSON(bounds,true);
				drawFromJSON(geom,whiteboard.overlayDrawing);
			}else if(whiteboard.tool == 'line'){
				geom  = createLineJSON(bounds);
				drawFromJSON(geom,whiteboard.overlayDrawing);
			}else if(whiteboard.tool == 'move'){
				if(whiteboard.selectedShape && whiteboard.mouseDownPt)
				{
					geom = createMoveOverlayJSON(whiteboard.selectedShape.wbbb);
					drawFromJSON(geom,whiteboard.overlayDrawing);
					var offBB = createOffsetBB(whiteboard.selectedShape.wbbb,whiteboard.mouseDownPt,pt);
					//console.dir(offBB);
					var geom2 = createMoveOverlayJSON(offBB);
					
					drawFromJSON(geom2,whiteboard.overlayDrawing);
				}				
			} 
			
		}
	}
	else{
		if(whiteboard.tool == 'move'){
			whiteboard.overlayDrawing.clear();
			var shape = getHoveredShape(whiteboard.drawing,pt);
			if(shape){
				geom = createMoveOverlayJSON(shape.wbbb);
				drawFromJSON(geom,whiteboard.overlayDrawing);
			}
		}
		
	}
	
	
	//mouse up or down doesn't matter for the select and delete tools
	if(whiteboard.tool == 'delete'){
		whiteboard.overlayDrawing.clear();
		var shape = getHoveredShape(whiteboard.drawing,pt);
		if(shape){
			geom = createDeleteOverlayJSON(shape.wbbb);
			drawFromJSON(geom,whiteboard.overlayDrawing);
		}
	}else if(whiteboard.tool == 'moveUp'){
		whiteboard.overlayDrawing.clear();
		var shape = getHoveredShape(whiteboard.drawing,pt);
		if(shape){
			geom = createMoveUpOverlayJSON(shape.wbbb);
			drawFromJSON(geom,whiteboard.overlayDrawing);
		}
	}else if(whiteboard.tool == 'moveDown'){
		whiteboard.overlayDrawing.clear();
		var shape = getHoveredShape(whiteboard.drawing,pt);
		if(shape){
			geom = createMoveDownOverlayJSON(shape.wbbb);
			drawFromJSON(geom,whiteboard.overlayDrawing);
		}
	}

  };
  
  
 var doGfxMouseUp = function(evt)
  {
	var pt = getGfxMouse(evt);
	whiteboard.mouseDown = false;
	//console.dir(pt);
	
	//always clear the overlay
	whiteboard.overlayDrawing.clear();
	
	if(whiteboard.mouseDownPt){
		//make sure mouse was released inside of drawing area
		if(pointInDrawing(pt)){
			
			//console.dir(whiteboard.mouseDownPt);
			
			var bounds = {x1:whiteboard.mouseDownPt.x, y1:whiteboard.mouseDownPt.y, x2: pt.x, y2: pt.y};
			//whiteboard.mouseDownPt = null;
	
			var geom = null;
			if(whiteboard.tool == 'rect'){
				geom  = createRectJSON(bounds,false);
				drawFromJSON(geom,whiteboard.drawing);
			}else if(whiteboard.tool == 'filledRect'){
				geom  = createRectJSON(bounds,true);
				drawFromJSON(geom,whiteboard.drawing);
			}else if(whiteboard.tool == 'ellipse'){
				geom  = createEllipseJSON(bounds,false);
				drawFromJSON(geom,whiteboard.drawing);
			}else if(whiteboard.tool == 'filledEllipse'){
				geom  = createEllipseJSON(bounds,true);
				drawFromJSON(geom,whiteboard.drawing);
			}else if(whiteboard.tool == 'line'){
				geom  = createLineJSON(bounds);
				drawFromJSON(geom,whiteboard.drawing);
			}else if(whiteboard.tool == 'pen'){
				geom = createPenJSON(whiteboard.points);
				drawFromJSON(geom,whiteboard.drawing);
				console.log("num pen points sending:",geom.xPts.length);
			}else if(whiteboard.tool == 'delete'){
				var shape = getHoveredShape(whiteboard.drawing,pt);
				if(shape){
					geom = createDeleteJSON(shape);
					drawFromJSON(geom,whiteboard.drawing);
				}
			}else if(whiteboard.tool == 'move'){
				//console.log(whiteboard.selectedShape,whiteboard.mouseDownPt,bounds);
				if(whiteboard.selectedShape && whiteboard.mouseDownPt)
				{
					var ptDelta = {x: (pt.x - whiteboard.mouseDownPt.x),y: (pt.y - whiteboard.mouseDownPt.y)};
					
					geom = createMoveJSON(whiteboard.selectedShape, ptDelta);
					
					drawFromJSON(geom,whiteboard.drawing);
					//console.dir(geom);
				}
				
			}else if(whiteboard.tool == 'moveUp'){
				var shape = getHoveredShape(whiteboard.drawing,pt);
				if(shape){
					geom = createMoveUpJSON(shape);
					drawFromJSON(geom,whiteboard.drawing);
				}
			}else if(whiteboard.tool == 'moveDown'){
				var shape = getHoveredShape(whiteboard.drawing,pt);
				if(shape){
					geom = createMoveDownJSON(shape);
					drawFromJSON(geom,whiteboard.drawing);
				}
			}else if(whiteboard.tool == 'text'){
				whiteboard.textPoint = pt;
				dijit.byId('textDialog').show();
				dijit.byId('wbText').focus();
				
			}
			
	
			//whiteboard.points = [];
			
			if(geom){			
				whiteboard.sendMessage({geometry:geom});
			}
			
		}else{
			whiteboard.mouseDownPt = null;
			console.log("mouse released outside of drawing area");
		}
	}
	
	//clear everything
	whiteboard.mouseDownPt = null;
	whiteboard.selectedShape = null;
	whiteboard.points = [];
	
  };

  //first point should be upper left of rect
var  normalizeBounds = function(bounds){
		if(bounds.x2 < bounds.x1){
			var tempX1 = bounds.x1;
			bounds.x1 = bounds.x2;
			bounds.x2 = tempX1;
		}
		if(bounds.y2 < bounds.y1){
			var tempY1 = bounds.y1;
			bounds.y1 = bounds.y2;
			bounds.y2 = tempY1;
		}
		return bounds;
  };
  
  
var getHoveredShape = function(drawing, pt){
	
	try{
		var children = drawing.children;
		if(children){
			for(var i = children.length; i > 0; i--){
				var child = children[i - 1];
				if(ptInBox(pt,child.wbbb)){
					return child;
				}	
			}
		}
	}catch(e){
		console.log('error finding shape',e);
	}
	
	return null;
	
};

var ptInBox = function(pt, box){
	if(pt && box){
		if((pt.x >= box.x1) && (pt.x <= box.x2) && (pt.y >= box.y1) && (pt.y <= box.y2)){
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
	
};

var selectTool = function(toolName)
  {
	
	  hide("lineColorDisplay");
	  hide("fillColorDisplay");
	  hide("lineStrokeSelect");
	  hide("fontSizeSelect");
	  
	  var tool = null;
	  dojo.forEach(tools,function(aTool){
		  	if(aTool.name == toolName){
		  		tool = aTool;
		  	}
			//dojo.style(dijit.byId(aTool.name + 'ToolBtn').domNode,'border','0px');
		  	//dojo.addClass(dojo.style(dijit.byId(aTool.name + 'ToolBtn').domNode, "selected");
		  	dojo.removeClass(dijit.byId(aTool.name + 'ToolBtn').domNode, "selected");
	  });
	  
	  //dojo.style(dijit.byId(tool.name + 'ToolBtn').domNode,'border','2px solid black');
		  	dojo.addClass(dijit.byId(tool.name + 'ToolBtn').domNode, "selected");
	  whiteboard.tool = tool.name;
	  
	  if(tool.showLineColor){
		  show("lineColorDisplay");
	  }
	  if(tool.showFillColor){
		  show("fillColorDisplay");
	  }
	  if(tool.showLineThickness){
		  show("lineStrokeSelect");
	  }
	  if(tool.showFontSize){
		  show("fontSizeSelect");
	  }
	  
  };

 var hide = function(id){
	try{
		dijit.byId(id).domNode.style.display = 'none';
	}catch(e)
	{
	}
 };

 var show = function(id){
	 try{
		dijit.byId(id).domNode.style.display = '';
	}catch(e)
	{
	}
 };
 
 var chooseColor = function(type) {
	  	var cp = dijit.byId(type + 'ColorPaletteWidget');
	  	//console.log(cp);
	    dojo.style(dojo.byId(type + 'Swatch'),{backgroundColor: cp.value});
		whiteboard[type + 'Color'] = cp.value;
	    dijit.popup.close(dijit.byId(type + "ColorPaletteDialog"));
	};
	
var cancelChooseColor = function(type) {
	dijit.popup.close(dijit.byId(type + "ColorPaletteDialog"));
};

var  sendChatMessage = function(){
	var cwm = dojo.byId('chatWaitMessage');
	var ct = dijit.byId('chatText');
	var cb = dijit.byId('chatBtn');
	var msg = dojo.trim('' + ct.getValue());
	if(msg == '')
	{
		cwm.innerHTML = 'Cat got your tongue?';	
  	}else if(msg == lastMessage){
  		cwm.innerHTML = 'That\'s what you said last time.';
  	}else{
  		ct.setAttribute('disabled',true);
		cb.setAttribute('disabled',true);
		lastMessage = msg;
		dojo.byId('chatWaitMessage').innerHTML = 'sending...';
		whiteboard.sendMessage({chatMessage:msg});
  	}

  };

 var exportImage = function(){
	try{
		
		dojo.byId("exportedImg").src = dojo.query('canvas',dojo.byId('applicationArea'))[0].toDataURL();
		dijit.byId("imgDialog").show();
		
	}catch(e){
		console.info("canvas not supported",e);
	}
  };
  
 var exportMovieImage = function(){
		try{
			
			dojo.byId("exportedImg").src = dojo.query('canvas',dojo.byId('movieDialog'))[0].toDataURL();
			dijit.byId("imgDialog").show();
			
		}catch(e){
			console.info("canvas not supported",e);
		}
};
  
var showMovie = function(){
		try{
			
			dijit.byId("movieDialog").show();
			
			if(messageList){
				whiteboard.geomMessageList = [];
				dojo.forEach(messageList,function(m){
					if(m.geometry){
						whiteboard.geomMessageList.push(m);
					}
				});
			}
			var mSlider = dijit.byId('movieSlider'); 
			mSlider.setAttribute('maximum',whiteboard.geomMessageList.length);
			mSlider.setAttribute('discreteValues',whiteboard.geomMessageList.length);
			
			mSlider.setValue(0);
			
		}catch(e){
			console.info("canvas not supported",e);
		}
  };
  
 var incrementMovie = function(){
	  var indexEnd = Math.round(dijit.byId('movieSlider').getValue());
	  whiteboard.movieDrawing.clear();
	  for(var i =0; i < indexEnd; i++){
		  drawFromJSON(whiteboard.geomMessageList[i].geometry, whiteboard.movieDrawing);
	  }
	  if(indexEnd > 0){
		  dojo.byId('movieUser').innerHTML = whiteboard.geomMessageList[indexEnd - 1].fromUser;
	  }
	  
  };
  
  var doCancelAddText = function(){
		dijit.byId('wbText').setValue('');
		dijit.byId('textDialog').hide();
		whiteboard.overlayDrawing.clear();
		whiteboard.textPoint = null;
	};

	var doAddText = function(){
		var text = dijit.byId('wbText').getValue();
		if((text != '') && (whiteboard.textPoint)){
			dijit.byId('textDialog').hide();
			var geom = createTextJSON(whiteboard.textPoint,text);
			drawFromJSON(geom,whiteboard.drawing);
			whiteboard.textPoint = null;
			whiteboard.sendMessage({geometry:geom});
		}
		whiteboard.overlayDrawing.clear();
	};

	var doIncrementalText = function(){
		whiteboard.overlayDrawing.clear();
		var text = dijit.byId('wbText').getValue();
		if((text != '') && (whiteboard.textPoint)){
			var geom = createTextJSON(whiteboard.textPoint,text);
			drawFromJSON(geom,whiteboard.overlayDrawing);
		}
		
	};

  
var submitUserName = function(){
		var unm = dojo.byId('subitUserNameMessage');
		var unt = dijit.byId('userNameText');
		var unb = dijit.byId('userNameBtn');
		if(!unt.isValid()){
			unm.innerHTML = 'Invalid user name';
		}else{
			unb.setAttribute('disabled',true);
			unt.setAttribute('disabled',true);
			unm.innerHTML = 'sending...';
			
			dojo.xhrPost({
		        url: '/wbSetName',
		        content: {
		    	   wbId: wbId,
		           userName: unt.getValue()
		       },
		        load: function(resp){
					console.log("post response",resp);
					if(resp.error){
						unm.innerHTML = '<b>Error: ' + resp.error + '</b><br>Please try again.';
						unb.setAttribute('disabled',false);
						unt.setAttribute('disabled',false);
					}else{
						token = resp.token;
						userName = resp.userName;
						unm.innerHTML = 'connecting to channel...';
						openChannel();
					}
		       },
		       error: function(e){
					console.info("post error",e);
					unm.innerHTML = '<b>Error: ' + e + '</b><br>Please try again.';
					unb.setAttribute('disabled',false);
					unt.setAttribute('disabled',false);
		       },
		       handleAs: "json",
		       preventCache: true
		    });

		}
 };

 
 var loadFunction = function(){
	
	if(token){
	    	openChannel();
	}
	else{
		if(userName){
			dijit.byId("userNameText").setValue(userName);
			submitUserName();
		}else{
			dojo.connect(dijit.byId('userNameBtn'),'onClick',submitUserName);
			dojo.byId('setUserDiv').style.display = '';
			dojo.connect(dijit.byId("userNameText"), 'onKeyDown', function(evt) {
		          if(evt.keyCode == dojo.keys.ENTER) { 
		        	  submitUserName();  
				  }
		     });
		}
		
		   
	}
	
	dojo.connect(dijit.byId('lineColorPaletteOkBtn'),'onClick',function(){
		chooseColor('line');
	});	
	dojo.connect(dijit.byId('lineColorPaletteCancelBtn'),'onClick',function(){
		cancelChooseColor('line');
	});

	dojo.connect(dijit.byId('fillColorPaletteOkBtn'),'onClick',function(){
		chooseColor('fill');
	});	
	dojo.connect(dijit.byId('fillColorPaletteCancelBtn'),'onClick',function(){
		cancelChooseColor('fill');
	});
	
	if(Modernizr.canvas)
	{
		dojo.connect(dijit.byId('exportImgBtn'),'onClick',exportImage);
		dojo.connect(dijit.byId('exportMovieImgBtn'),'onClick',exportMovieImage);
	}else{
		dojo.style(dijit.byId('exportImgBtn').domNode, {'visibility': 'hidden', 'display': 'none'});
		dojo.style(dijit.byId('exportMovieImgBtn').domNode, {'visibility': 'hidden', 'display': 'none'});
	}
	
	dojo.connect(dijit.byId('showMovieBtn'),'onClick',showMovie);
	dojo.connect(dijit.byId('movieSlider'),'onChange',incrementMovie);
	


	dojo.connect(dijit.byId('lineStrokeSelect'),'onChange',function(){
		whiteboard.lineStroke = Math.floor(1.0 * dijit.byId('lineStrokeSelect').getValue());
	});
	
	dojo.connect(dijit.byId('fontSizeSelect'),'onChange',function(){
		whiteboard.fontSize = Math.floor(1.0 * dijit.byId('fontSizeSelect').getValue());
	});
	
	dojo.connect(dijit.byId('clearDrawingNoBtn'),'onClick',function(){
		dijit.popup.close(dijit.byId("clearDrawingDialog"));
	});
	
	dojo.connect(dijit.byId('clearDrawingYesBtn'),'onClick',function(){
		dijit.popup.close(dijit.byId("clearDrawingDialog"));
		var geom = createClearDrawingJSON();
		whiteboard.sendMessage({geometry: geom });
		drawFromJSON(geom,whiteboard.drawing);
		
	});
	
	dojo.connect(dijit.byId('sendMailButton'),'onClick',function(){
		
		sendEmail();
		
	});
	
	
	
	
	selectTool('pen');
	
	dojo.forEach(tools,function(tool){
		dojo.connect(dijit.byId(tool.name + 'ToolBtn'),'onClick',function(){
			selectTool(tool.name);			
		});
	});
	
	
	  dojo.connect(dijit.byId("wbText"), 'onKeyDown', function(evt) {
          if(evt.keyCode == dojo.keys.ENTER) { 
          	doAddText();  
			}
     });
	   
	   dojo.connect(dijit.byId("okTextBtn"), 'onClick', function(evt) {
          doAddText();
     });
	   
	   dojo.connect(dijit.byId("cancelTextBtn"), 'onClick', function(evt) {
          doCancelAddText();
     });
	   
	   dojo.connect(dijit.byId("wbText"), 'onKeyUp', function(evt) {
          doIncrementalText();
     });
	   
	   dojo.connect(dijit.byId("wbText"), 'onChange', function(evt) {
          doIncrementalText();
     });
	   
	   dojo.connect(dijit.byId("textDialog"), 'onClose', function(evt) {
			whiteboard.overlayDrawing.clear();
			dijit.byId("wbText").setValue('');
     });
	   
	   dojo.connect(dijit.byId("textDialog"), 'onHide', function(evt) {
			whiteboard.overlayDrawing.clear();
			dijit.byId("wbText").setValue('');
     });
	   
	   try{
		   var reader = new FileReader();
		   reader.onload = function(e) {
		     document.querySelector('img').src = e.target.result;
		   };
	
		   function onDrop(e) {
		     reader.readAsDataURL(e.dataTransfer.files[0]);
		   };
	   
	   }catch(imgE){
		   
		   
	   }
	   

	
	
};



function DNDFileController(id) {
	  var el_ = document.getElementById(id);
	  var thumbnails_ = document.getElementById('thumbnails');



	  this.dragenter = function(e) {
	    e.stopPropagation();
	    e.preventDefault();
	    el_.classList.add('rounded');
	  };

	  this.dragover = function(e) {
	    e.stopPropagation();
	    e.preventDefault();
	  };

	  this.dragleave = function(e) {
	    e.stopPropagation();
	    e.preventDefault();
	    el_.classList.remove('rounded');
	  };

	  this.drop = function(e) {
		  
		try{
		  
			//console.log('dropevent',e);
			
			var pt = getGfxMouse(e);
			
		    e.stopPropagation();
		    e.preventDefault();
	
		    el_.classList.remove('rounded');
	
		    var files = e.dataTransfer.files;
	
		    for (var i = 0, file; file = files[i]; i++) {
		      var imageType = /image.*/;
		      if (!file.type.match(imageType)) {
		        continue;
		      }
	
			      // FileReader
		      var reader = new FileReader();
		      
		      reader.onerror = function(evt) {
		         alert('Error code: ' + evt.target.error.code);
		      };
		      reader.onload = (function(aFile) {
		        return function(evt) {
		          if (evt.target.readyState == FileReader.DONE) {
		         	  
		        	  console.log('rawImg',evt.target.result.length);
		        	  var img = new Image();
		        	  img.src = evt.target.result;
		        	  var imgData = img.src;
		        	  var newH, newW;
		        	  
		        	  img.onload = function(){
		        		  console.log(img.height, img.width); 
		        		  var maxDim = 75;
		        		  //console.log(whiteboard);
		        		  if(img.height > maxDim || img.width > maxDim){
		        			  //need to scale
		        			  
		        			  
		        			  if(img.width > img.height){
		        				  newW = maxDim;
		        				  newH = Math.round((maxDim * img.height) / img.width);
		        			  }else{
		        				  newH = maxDim;
		        				  newW = Math.round((maxDim * img.width) / img.height);
		        				  
		        			  }
		        			  
		        		  }else{
		        			  newH = img.height;
		        			  newW = img.width;
		        		  }
		        		  
		        		  
		        		  var tempCanvas = document.createElement("canvas");
	        			  tempCanvas.height = newH;
	        			  tempCanvas.width = newW;
	        			  var tempContext = tempCanvas.getContext("2d");
	        			  tempContext.drawImage(img,0,0,newW,newH);
	        			  
	        			  var bounds = {x1:pt.x, y1:pt.y, x2: pt.x + newW, y2: pt.y + newH};
	        			  var imgJSON = createImageJSON(bounds,tempCanvas.toDataURL());
	        			  
	        			  
	        			  //console.log(imgJSON);
	        			  
	        			  drawFromJSON(imgJSON,whiteboard.drawing);
	        			  
	        			  whiteboard.sendMessage({geometry:imgJSON});
	        			  
	        			          			  
		        		  
		        	  };
		        	  
		          }
		        };
		      })(file);
	
		      reader.readAsDataURL(file);
		    }
	
		    return false;
	    
	    
		}catch(dropE){
			console.log('DnD error',dropE);
		}
	  };

	  
	  el_.addEventListener("dragenter", this.dragenter, false);
	  el_.addEventListener("dragover", this.dragover, false);
	  el_.addEventListener("dragleave", this.dragleave, false);
	  el_.addEventListener("drop", this.drop, false);
	};



 
 
 dojo.addOnLoad(loadFunction);
 