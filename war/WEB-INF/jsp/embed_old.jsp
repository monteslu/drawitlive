<jsp:include page="basicheader.jsp"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
 String hostName=request.getServerName();
%>

<script src='/_ah/channel/jsapi'></script>
<script>
dojo.require('azp.BBCodeEditor');


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
<script src="/js/azp/Whiteboard.js"></script>

<!--<script src="/js/azp/BBCodeEditor.js"></script>-->

<style>
#header{
	font-size: 3em;
	text-align: center;

}

#setUserDiv{
	text-align: center;
}

._webstore_badge {
  width: 128px;
  height: 190px;
  top: 0px;
  right: 0px;
  position: absolute;
  margin: 0 50px 0 50px;
  padding: 5px;
  box-shadow: #000 0 0 5px;
  background-color: #eae88c;
  border: solid 1px #c9c778;
  border-bottom-left-radius: 5px;
  border-bottom-right-radius: 5px;
  display: inline;
  font-family: Arial;
  opacity: 1;
  -webkit-transition: all 0.5s ease-in-out;
}

</style>


<div id="setUserDiv" style="display: none; padding: 5px;">
	<div id="header"><h1>Collaborative Whiteboard</h1></div>
	
	<br>
	Choose any user name (no spaces or punctuation):<input id="userNameText" dojoType="dijit.form.ValidationTextBox"> <span id="userNameBtn" dojoType="dijit.form.Button">Start Drawing!</span><br>
	
	<span id="subitUserNameMessage"></span>
	<br><br>
<!--	<script src="/js/badge.js"></script>-->

</div>
<div id="imgDialog" dojoType="dijit.Dialog" title="Right-click on image to save it">
    <img id="exportedImg" name="exportedImg">
</div>


<div id="textDialog" dojoType="dijit.Dialog" title="Type in some text."  style="display: none">
    <input type="text" dojoType="dijit.form.ValidationTextBox" id="wbText" name="wbText"><br>
	<button dojoType="dijit.form.Button" id="okTextBtn">OK</button>&nbsp;&nbsp;&nbsp;<button dojoType="dijit.form.Button" id="cancelTextBtn">Cancel</button>
</div>

<div id="movieDialog" dojoType="dijit.Dialog" title="Move slider to see drawing steps."  style="display: none">
    <div id="movieWhiteboardContainer" style="border: 2px blue solid; background-color: white;"></div><br>
    <div id="movieSlider" dojoType="dijit.form.HorizontalSlider" value="0"
minimum="0" maximum="1" discreteValues="2" intermediateChanges="true"
showButtons="false" style="width:500px;"></div><br> <button dojoType="dijit.form.Button" id="exportMovieImgBtn"><img src="/images/export-icon.png"></button>
	    	<div dojoType="dijit.Tooltip" connectId="exportMovieImgBtn">Export this snapshot of the drawing surface.</div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    	user: <span id="movieUser">user</span><br>
</div>


<div id="applicationArea" style="width: 100%; height: 100%; display: none; position: absolute; top: 0; bottom: 0;" dojoType="dijit.layout.BorderContainer" liveSplitters="true">
<div region="center">
	draw: 
	
	<div>
	<div id="whiteboardContainer" style="border: 2px blue solid; background-color: white;"></div>
	<div id="whiteboardOverlayContainer"  style="border: 2px black solid; z-index : 1;"></div>
	</div>
			<form onsubmit="return false" dojoType="dijit.form.Form" id="toolForm">

		   <button dojoType="dijit.form.Button" id="penToolBtn"><img border="0" src="/images/pencil.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="penToolBtn" position="above" showDelay="0">Pencil<br>(freehand drawing)</div>
		   
		   <button dojoType="dijit.form.Button" id="lineToolBtn"><img border="0" src="/images/line.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="lineToolBtn" position="above" showDelay="0">Straight Line</div>
		   
		   <button dojoType="dijit.form.Button" id="rectToolBtn"><img border="0" src="/images/rect.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="rectToolBtn" position="above" showDelay="0">Rectangle</div>
		   
		   <button dojoType="dijit.form.Button" id="filledRectToolBtn"><img border="0" src="/images/filledRect.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="filledRectToolBtn" position="above" showDelay="0">Filled Rectangle</div>
		   
		   <button dojoType="dijit.form.Button" id="ellipseToolBtn"><img border="0" src="/images/ellipse.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="ellipseToolBtn" position="above" showDelay="0">Ellipse</div>
		   
		   <button dojoType="dijit.form.Button" id="filledEllipseToolBtn"><img border="0" src="/images/filledEllipse.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="filledEllipseToolBtn" position="above" showDelay="0">Filled Ellipse</div>
		   
		   
		   <button dojoType="dijit.form.Button" id="textToolBtn"><img border="0" src="/images/text.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="textToolBtn" position="above" showDelay="0">Draw Text</div>

		   <button dojoType="dijit.form.Button" id="moveToolBtn"><img border="0" src="/images/move.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="moveToolBtn" position="above" showDelay="0">Move a shape</div>
		   
		   <button dojoType="dijit.form.Button" id="moveUpToolBtn"><img border="0" src="/images/moveUp.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="moveUpToolBtn" position="above" showDelay="0">Pull a shape forward</div>
		   
		   <button dojoType="dijit.form.Button" id="moveDownToolBtn"><img border="0" src="/images/moveDown.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="moveDownToolBtn" position="above" showDelay="0">Push a shape back</div>
		   
		   <button dojoType="dijit.form.Button" id="deleteToolBtn"><img border="0" src="/images/delete.png"></button>
		   <div dojoType="dijit.Tooltip" connectId="deleteToolBtn" position="above" showDelay="0">Delete a shape</div>
		   
		   <br>
		   
		   <div id="lineColorDisplay" style="background-color: FFFFFF; border-color: black; border-width: 1px;" dojoType="dijit.form.DropDownButton">
			   <span id="colorText">color <span id="lineSwatch" style="height: 10px; width: 10px; border: 1px solid black; background-color: black;">&nbsp;&nbsp;</span></span>
			   <div dojoType="dijit.TooltipDialog" id="lineColorPaletteDialog" title="Color Palette">
					<div dojoType="dojox.widget.ColorPicker" id="lineColorPaletteWidget"></div>
					<button dojoType="dijit.form.Button" id="lineColorPaletteOkBtn">OK</button> <button dojoType="dijit.form.Button" id="lineColorPaletteCancelBtn">Cancel</button>
			   </div>
		   </div>
		   <div id="fillColorDisplay" style="background-color: FFFFFF; border-color: black; border-width: 1px;" dojoType="dijit.form.DropDownButton">
			   <span id="colorText">fill <span id="fillSwatch" style="height: 10px; width: 10px; border: 1px solid black; background-color: white;">&nbsp;&nbsp;</span></span>
			   <div dojoType="dijit.TooltipDialog" id="fillColorPaletteDialog" title="Color Palette">
					<div dojoType="dojox.widget.ColorPicker" id="fillColorPaletteWidget"></div>
					<button dojoType="dijit.form.Button" id="fillColorPaletteOkBtn">OK</button> <button dojoType="dijit.form.Button" id="fillColorPaletteCancelBtn">Cancel</button>					
			   </div>
		   </div>
		   <select name="select" dojoType="dijit.form.Select" id="lineStrokeSelect">
    			<option value="1">
    	    	Line Thicknes:  1
	    		</option>
	    		<option value="2">
    	    	Line Thicknes:  2
	    		</option>
	    		<option value="3" selected="selected">
    	    	Line Thicknes:  3
	    		</option>
	    		<option value="4">
    	    	Line Thicknes:  4
	    		</option>
	    		<option value="5">
    	    	Line Thicknes:  5
	    		</option>
	    		<option value="6">
    	    	Line Thicknes:  6
	    		</option>
	    		<option value="7">
    	    	Line Thicknes:  7
	    		</option>
	    		<option value="8">
    	    	Line Thicknes:  8
	    		</option>
	    		<option value="9">
    	    	Line Thicknes:  9
	    		</option>
	    		<option value="10">
    	    	Line Thicknes: 10
	    		</option>
	    		<option value="15">
    	    	Line Thicknes: 15
	    		</option>
	    		<option value="20">
    	    	Line Thicknes: 20
	    		</option>
	    		<option value="30">
    	    	Line Thicknes: 30
	    		</option>
	    		<option value="50">
    	    	Line Thicknes: 50
	    		</option>
	    		<option value="75">
    	    	Line Thicknes: 75
	    		</option>
	    		<option value="100">
    	    	Line Thicknes:100
	    		</option>
	    	</select>
	    	
	    	
	    	<select name="select" dojoType="dijit.form.Select" id="fontSizeSelect">
    			<option value="5">
    	    	Font:  5pt
	    		</option>
	    		<option value="6">
    	    	Font:  6pt
	    		</option>
	    		<option value="7">
    	    	Font:  7pt
	    		</option>
	    		<option value="8">
    	    	Font:  8pt
	    		</option>
	    		<option value="9">
    	    	Font:  9pt
	    		</option>
	    		<option value="10">
    	    	Font: 10pt
	    		</option>
	    		<option value="12" selected="selected">
    	    	Font: 12pt
	    		</option>
	    		<option value="14">
    	    	Font: 14pt
	    		</option>
	    		<option value="16">
    	    	Font: 16pt
	    		</option>
	    		<option value="20">
    	    	Font: 20pt
	    		</option>
	    		<option value="24">
    	    	Font: 24pt
	    		</option>
	    		<option value="32">
    	    	Font: 32pt
	    		</option>
				<option value="48">
    	    	Font: 48pt
	    		</option>
				<option value="64">
    	    	Font: 64pt
	    		</option>
	    	</select>
	    	
	    	<button dojoType="dijit.form.Button" id="exportImgBtn"><img src="/images/export-icon.png"></button>
	    	<div dojoType="dijit.Tooltip" connectId="exportImgBtn" position="below" showDelay="0">Export the drawing surface.</div>
	    	
	    	<button dojoType="dijit.form.Button" id="showMovieBtn"><img src="/images/movie-icon.png"></button>
	    	<div dojoType="dijit.Tooltip" connectId="showMovieBtn" position="below" showDelay="0">View all steps that made this drawing.</div>
	    	
	    	
	    	<div id="clearDrawingDisplay" dojoType="dijit.form.DropDownButton">
			   <span id="colorText">clear</span>
			   <div dojoType="dijit.TooltipDialog" id="clearDrawingDialog" title="Clear Drawing">
			   		Clear Drawing?<br>
					<button id="clearDrawingYesBtn" dojoType="dijit.form.Button">Yes</button> &nbsp;&nbsp;&nbsp; <button id="clearDrawingNoBtn" dojoType="dijit.form.Button">No</button>					
			   </div>
		   </div>
		</form>
	<BR><BR>
	Share this link with your friends to have them join:<br>
<a href="http://<%= hostName %>/whiteboard/${wbId}">http://<%= hostName %>/whiteboard/${wbId}</a><br>
or send them an email:<input dojoType="dijit.form.ValidationTextBox" style="width:20em;" id="email"
						                  	  			 regExp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$"
						                  	  			 maxlength="128" placeHolder="enter an email address"/><button id="sendMailButton" dojoType="dijit.form.Button">send</button><br><br>
Or start a new whiteboard: <a href="http://<%= hostName %>/whiteboard">http://<%= hostName %>/whiteboard</a><br>
<a href="http://twitter.com/monteslu">@monteslu</a>
</div>



</div>


<jsp:include page="footer.jsp"/>