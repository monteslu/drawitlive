<jsp:include page="basicheader.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
 .dataTable{
 	border: 1px solid black;
 	
 }
 .dataTable tr{
 	border: 1px solid black;
 	padding: 2px;
 	
 }
 .dataTable td{
 	border: 1px solid black;
 	padding: 2px;
 	background-color: #EEEEFF;
 	
 }
  .dataTable th{
 	border: 1px solid black;
 	padding: 2px;
 	background-color: #EEEEFF;
 	
 }
 
</style>
<script>
	dojo.require('dijit.form.Button');
	var expand = function(id){
		var btn = dijit.byId('btn'+id);
		//console.log(btn);
		var output = dojo.byId('details' + id);
		
		if(btn.attr('label') == '+'){
			btn.setLabel('-');
			output.innerHTML = 'loading...';
			dojo.xhrGet({
	            url: '/wbchannels/' + id,
	            load: function(resp){
	            	console.log(resp);
					if(resp.status == 'ok'){
						var outputText = '<table>';
						if(resp.channels){
							dojo.forEach(resp.channels,function(channel){
								outputText += ('<tr><td>' + channel.userName + '</td><td>' + channel.sessionId + '</td><td>' + new Date(channel.time) + '</td><td>' + channel.userAgent + '</td></tr>');
							});
						}
						output.innerHTML = outputText + '</table><br><span id=\'messages' + id + '\'></span>';
						messageDetails(id);
					}else{
						output.innerHTML = resp.error;
					}

				},
	            error: function(err){
					output.innerHTML = err;
	            },
	            handleAs: "json",
	            preventCache: true
	         });
		}else{
			btn.setLabel('+');
			output.innerHTML = '';
		}
	};

	var messageDetails = function(id){
		var output = dojo.byId('messages' + id);
		
		if(output){
			output.innerHTML = 'loading...';
			dojo.xhrGet({
	            url: '/wbmessages/' + id,
	            load: function(resp){
	            	console.log('msgresp',resp);
					if(resp.status == 'ok'){
						
						if(resp.messages){
							var outputText = 'messages: ' + resp.messages.length + '<br><table><tr><th>name</th><th>last message</th><th>chat count</th><th>geom count</th></tr>';
							var stats = {};
							
							dojo.forEach(resp.messages,function(mess){
								if(!stats[mess.fromUser]){
									stats[mess.fromUser] = {chats:0,geoms:0,lastMess:0};
								}
								var stat = stats[mess.fromUser];
								if(mess.chatMessage){
									stat.chats++; 
								}
								if(mess.geometry){
									stat.geoms++; 
								}
								if(mess.creationTime > stat.lastMess){
									stat.lastMess = mess.creationTime;
								}
								
							});
							for (stat in stats)
							{
								outputText += ('<tr><td>' + stat + '</td><td>' + new Date(stats[stat].lastMess) + '</td><td>' + stats[stat].chats + '</td><td>' + stats[stat].geoms + '</td></tr>');
							}
							console.dir(stats);

							output.innerHTML = outputText + '</table>';
						}else{
							output.innerHTML = 'no messages';
						}
						
					}else{
						output.innerHTML = resp.error;
					}

				},
	            error: function(err){
					output.innerHTML = err;
	            },
	            handleAs: "json",
	            preventCache: true
	         });
		}

	}
	
</script>
<div style="padding: 10px;">

Hello, ${user.nickname}  , ${user.userId}<br><br>


<table cellspacing="2" border="1" class="dataTable">


<c:forEach var="board" items="${boards}">
<tr>	                    	
						   <td rowspan="2"><button dojoType="dijit.form.Button" onclick="expand('${board.keyString}');" id="btn${board.keyString}">+</button></td> <td><a href="/whiteboard/${board.keyString}">${board.keyString}</a></td><td> ${board.creationDate}</td><td>${board.userAgent}</td><td>${board.referer}</td><td>${board.status}</td>
						    
</tr>
<tr><td colspan="5"><span id="details${board.keyString}"></span></td></tr>
</c:forEach>


</table>




</div>


<jsp:include page="footer.jsp"/>