<%@page import="java.util.Enumeration"%>

<%@page import="com.google.apphosting.utils.config.*"%><html>
<body>

<%
	try{
		String attrName = request.getParameter("attributeName");
		String attrValue = request.getParameter("attributeValue");
		if((attrName != null) && (!"".equals(attrName.trim()))){
			session.setAttribute(attrName,attrValue);
		}
		
	}catch(Exception attrE){
		//do nothing
	}

%>

<h1>Session Variables</h1>
<form method="get" action="info.jsp">
<table border="1">

  <tr><th>variable</th><th>Object.toString()</th><th>class</th>
  </tr>
<%
    	 for (Enumeration e = session.getAttributeNames() ; e.hasMoreElements(); ) {
         	String attributeName = e.nextElement().toString();
           	out.print("<tr>");
			out.println("<td>" + attributeName + "</td>");
			out.println("<td>" + session.getAttribute(attributeName) + "</td>");
			out.println("<td>" + session.getAttribute(attributeName).getClass() + "</td>");
			out.println("</tr>");
     	}

%>
<tr><td><input type="text" name="attributeName" size="12"></td><td><input type="text" name="attributeValue" size="12"></td><td align="left"><input type="submit" value="add"></td>
</table>
</form>
<BR><BR>
<h2>Request Headers</h2>

<table border="1">
  <tr><th>header</th><th>value</th>
  </tr>
<%

	 for (Enumeration e = request.getHeaderNames() ; e.hasMoreElements() ;) {
         String headerName = e.nextElement().toString();
		 
          	 out.print("<tr>");
          	 out.println("<td>" + headerName + "</td>");
		 out.println("<td>" + request.getHeader(headerName) + "</td>");
		 out.println("</tr>");

     }
 		 
%>
</table>

<BR><BR>
<h2>Servlet Context</h2>

<table border="1">
  <tr><th>attribute</th><th>value</th>
  </tr>
<%

	 for (Enumeration e = application.getAttributeNames() ; e.hasMoreElements() ;) {
         String headerName = e.nextElement().toString();
		 
          	 out.print("<tr>");
          	 out.println("<td>" + headerName + "</td>");
		 out.println("<td>" + application.getAttribute(headerName) + "</td>");
		 out.println("</tr>");

     }
 		 
%>
</table>


<BR><BR>
<h2>System properties</h2>

<table border="1">
  <tr><th>attribute</th><th>value</th>
  </tr>
<%

	 for (Enumeration e = System.getProperties().propertyNames() ; e.hasMoreElements() ;) {
         String propName = e.nextElement().toString();
		 
          	 out.print("<tr>");
          	 out.println("<td>" + propName + "</td>");
		 out.println("<td>" + System.getProperty(propName) + "</td>");
		 out.println("</tr>");

     }
 		 
%>
</table>
<%= System.getProperty("com.google.appengine.application.id") %>
<BR><BR>
<h2>Request Attributes</h2>

<table border="1">
  <tr><th>attribute</th><th>value</th>
  </tr>
<%

	 for (Enumeration e = request.getAttributeNames() ; e.hasMoreElements() ;) {
         String headerName = e.nextElement().toString();
		 
          	 out.print("<tr>");
          	 out.println("<td>" + headerName + "</td>");
		 out.println("<td>" + request.getAttribute(headerName) + "</td>");
		 out.println("</tr>");

     }
 		 
%>
</table>


<BR><BR>
<h2>Request Parameters</h2>

<table border="1">
  <tr><th>parameter</th><th>value</th>
  </tr>
<%

	 for (Enumeration e = request.getParameterNames() ; e.hasMoreElements() ;) {
         String headerName = e.nextElement().toString();
		 
          	 out.print("<tr>");
          	 out.println("<td>" + headerName + "</td>");
		 out.println("<td>" + request.getParameter(headerName) + "</td>");
		 out.println("</tr>");

     }
 		 
%>
</table>


<BR><BR>
<h2>ServletConfig Init Parms</h2>

<table border="1">
  <tr><th>parameter</th><th>value</th>
  </tr>
<%

	 for (Enumeration e = config.getInitParameterNames(); e.hasMoreElements() ;) {
         String headerName = e.nextElement().toString();
		 
          	 out.print("<tr>");
          	 out.println("<td>" + headerName + "</td>");
		 out.println("<td>" + config.getInitParameter(headerName) + "</td>");
		 out.println("</tr>");

     }
 		 
%>
</table>

<BR><BR>
<h2>Other Stuff</h2>

<table border="1">
  <tr><th>name</th><th>value</th>
  </tr>
<tr><td>session.getId()</td><td><%= session.getId() %></td></tr>
<tr><td>request.getRequestedSessionId()</td><td><%= request.getRequestedSessionId()%></td></tr>
<tr><td>request.getRemoteHost()</td><td><%= request.getRemoteHost() %></td></tr>

</table>

</body>
</html>