<!doctype html>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ --> 
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6" style="height: 100%; width: 100%"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7" style="height: 100%; width: 100%"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8" style="height: 100%; width: 100%"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9" style="height: 100%; width: 100%"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"  style="height: 100%; width: 100%"> <!--<![endif]-->
<head>
  <meta charset="utf-8">

  <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame 
       Remove this if you use the .htaccess -->
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

  <title></title>
  <meta name="description" content="">
  <meta name="author" content="monteslu">

  <!--  Mobile viewport optimized: j.mp/bplateviewport -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0">


  <!-- CSS : implied media="all" -->
  <link rel="stylesheet" href="css/style.css?v=2">

  <!-- Uncomment if you are specifically targeting less enabled mobile browsers
  <link rel="stylesheet" media="handheld" href="css/handheld.css?v=2">  -->
 
  <!-- All JavaScript at the bottom, except for Modernizr which enables HTML5 elements & feature detects -->
  <script src="js/libs/modernizr-1.6.min.js"></script>
  <script TYPE="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dojo/dojo.xd.js" djConfig="parseOnLoad: true, baseUrl: '/js/', modulePaths:{azp:'azp'}, gfxRenderer:'canvas,svg,vml,silverlight'"></script>


<style type="text/css">
        @import "http://ajax.googleapis.com/ajax/libs/dojo/1.6/dijit/themes/claro/claro.css";
              
</style>
<c:if test="${logins != null}">
<script>
	dojo.require("dijit.Dialog");
	dojo.require("dijit.form.Button");
	dojo.addOnLoad(function(){
		var loginLink = dojo.byId('loginlink'); 
		dojo.connect(loginLink, "onclick", function(e){
			// stop the native click 
			e.preventDefault();
			dijit.popup.open({
				popup: dijit.byId('loginDialog'), around: loginLink
			});
		});
		<c:forEach var="login" items="${logins}" varStatus="status">
			dojo.connect(dijit.byId('login${login.provider.shortName}Btn'),'onClick',function(){
				document.location.href = '${login.loginURL}';
			});
		</c:forEach>
		
	});
</script>
</c:if>
</head>

<body class="claro" style="height: 100%; width: 100%">

  <div id="container">
    <header>
		<div style="width: 100%; text-align: right;">
<c:if test="${logoutURL != null}">
	<span>Welcome, ${user} </span>
	<span><a href="${logoutURL}">logout</a></span>
</c:if><c:if test="${logins != null}">
	<a id="loginlink" href="#">login/signup</a>
	<div id="loginDialog" style="display: none" dojoType="dijit.TooltipDialog">
		Login with your account from:<br><br>
		<c:forEach var="login" items="${logins}" varStatus="status">
			<span dojoType="dijit.form.Button" id="login${login.provider.shortName}Btn" ><img src="/images/logos/${login.provider.shortName}.png" alt="${login.provider.providerName}" title="${login.provider.providerName}"></span>
		</c:forEach>
	</div>
</c:if>
</div>



    </header>
    
    <div id="main">
