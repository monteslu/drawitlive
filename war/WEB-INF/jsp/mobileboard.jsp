<jsp:include page="mobileheader.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
 String hostName=request.getServerName();
%>

<script src='/_ah/channel/jsapi'></script>
<script>
//dojo.require('azp.BBCodeEditor');


var chatMessageList = [];
var geomMessageList = [];
var messageList = [];
var messageMax = 200;
var wbId = '${wbId}';
var token = '${token}';
var userName = '${userName}';
var lastMessage = '';
var userList = [];
var messageListObj = null;
<c:if test="${messageMapJSON != null}">
 messageListObj = ${messageMapJSON};
 if(messageListObj.messages && (messageListObj.messages.length > 0)){
	 messageList = messageListObj.messages;
 }
</c:if>
//dojo.require('azp.Whiteboard');

var error = '${errorMsg}';
</script>
<script src="/js/azp/WBCommon.js"></script>
<script src="/js/azp/MobileWhiteboard.js"></script>


<style>
#header{
	text-align: center;

}

#setUserDiv{
	text-align: center;
}



</style>


	<div id="main" dojoType="dojox.mobile.View" selected="true">
		<h1 dojoType="dojox.mobile.Heading">
			Draw it Live !
		</h1>
		<div id="setUserDiv" style="display: none; padding: 5px;">
			<div id="header"><h1>Collaborative Whiteboard</h1></div>
			
			<br>
			Choose any user name (no spaces or punctuation):<input id="userNameText" type="text"> <span id="userNameBtn" dojoType="dojox.mobile.Button">Start Drawing!</span><br>
			
			<span id="subitUserNameMessage"></span>
			<br><br>	
		
		</div>
		
		<div id="mainMenu" style="display: none;">
			<ul dojoType="dojox.mobile.EdgeToEdgeList">
				<li dojoType="dojox.mobile.ListItem" icon="images/search.png" moveTo="drawing">
					Draw !
				</li>
				<li dojoType="dojox.mobile.ListItem" icon="images/collection.png" moveTo="tools">
					Drawing Tools
				</li>
				<li dojoType="dojox.mobile.ListItem" icon="images/collection.png" moveTo="chat">
					Chat
				</li>
				<li dojoType="dojox.mobile.ListItem" icon="images/picker.png" moveTo="share">
					Share
				</li>
				<li dojoType="dojox.mobile.ListItem" icon="images/me.png" moveTo="about">
					About
				</li>
			</ul>
		</div>
	</div>


	<div id="drawing" dojoType="dojox.mobile.View" style="display: none;">
		<div style="overflow: auto;">
		<div id="whiteboardContainer" style="border: 2px blue solid; background-color: white;"></div>
		<div id="whiteboardOverlayContainer"  style="border: 2px black solid; z-index : 1;"></div>
		</div>
		
	
		
		<div>
		<span id="toolDrawBtn" dojoType="dojox.mobile.Button" style="text-align: left"><span><img id="toolDrawImg" src="/images/pencil.png"></span> Tool</span> 
		<span id="chatDrawBtn" dojoType="dojox.mobile.Button"  style="text-align: right">chat <span>i</span></span>
		</div>
	</div>
	
	<div id="tools" dojoType="dojox.mobile.View" style="display: none;">
		<h1 dojoType="dojox.mobile.Heading" back="Drawing" moveTo="drawing">
			Drawing Tools
		</h1>
		
		<button dojoType="dojox.mobile.Button" id="penToolBtn"><img border="0" src="/images/pencil.png"> Pencil</button>
		   
		   <button dojoType="dojox.mobile.Button" id="lineToolBtn"><img border="0" src="/images/line.png">Line</button>
		   
		   <button dojoType="dojox.mobile.Button" id="rectToolBtn"><img border="0" src="/images/rect.png">Rectangle</button>
		   
		   <button dojoType="dojox.mobile.Button" id="filledRectToolBtn"><img border="0" src="/images/filledRect.png">Filled Rectangle</button>
		   
		   <button dojoType="dojox.mobile.Button" id="ellipseToolBtn"><img border="0" src="/images/ellipse.png">Ellipse</button>
		   
		   <button dojoType="dojox.mobile.Button" id="filledEllipseToolBtn"><img border="0" src="/images/filledEllipse.png">Filled Ellipse</button>
		   
		   
		   <button dojoType="dojox.mobile.Button" id="textToolBtn"><img border="0" src="/images/text.png">Text</button>

		   <button dojoType="dojox.mobile.Button" id="moveToolBtn"><img border="0" src="/images/move.png">Move a shape</button>
		   
		   <button dojoType="dojox.mobile.Button" id="moveUpToolBtn"><img border="0" src="/images/moveUp.png">Pull a shape</button>
		   
		   <button dojoType="dojox.mobile.Button" id="moveDownToolBtn"><img border="0" src="/images/moveDown.png">Push a shape</button>
		   
		   <button dojoType="dojox.mobile.Button" id="deleteToolBtn"><img border="0" src="/images/delete.png">Delete a shape</button>
		
		<ul dojoType="dojox.mobile.EdgeToEdgeList">
			<li dojoType="dojox.mobile.ListItem" moveTo="drawing"  icon="images/m_drawing.png">Drawing</li>
		</ul>
		<ul dojoType="dojox.mobile.EdgeToEdgeList">
			<li dojoType="dojox.mobile.ListItem" moveTo="main"  icon="images/m_drawing.png">home</li>
		</ul>

	</div>
		
	<div id="chat" dojoType="dojox.mobile.View" style="display: none;">
		<h1 dojoType="dojox.mobile.Heading" back="home" moveTo="main">
			Chat
		</h1>
		
		...
		<ul dojoType="dojox.mobile.EdgeToEdgeList">
			<li dojoType="dojox.mobile.ListItem" moveTo="main"  icon="images/home.png">Home</li>
		</ul>
		<ul dojoType="dojox.mobile.EdgeToEdgeList">
			<li dojoType="dojox.mobile.ListItem" moveTo="drawing"  icon="images/home.png">Drawing</li>
		</ul>
	</div>
	
	<div id="share" dojoType="dojox.mobile.View" style="display: none;">
		<h1 dojoType="dojox.mobile.Heading" back="home" moveTo="main">
			Share
		</h1>
		
	</div>

	<div id="about" dojoType="dojox.mobile.View" style="display: none;">
		<h1 dojoType="dojox.mobile.Heading" back="home" moveTo="main">
			About
		</h1>
		
		The drawitlive.com Collaborative Whiteboard was created by Luis Montes<br><br>
		Follow Luis on twitter <a href="http://twitter.com/monteslu">@monteslu</a> for updates.
	</div>


<jsp:include page="mobilefooter.jsp"/>