<%@ page isErrorPage="true" %>  
<%@ page import="org.apache.commons.lang.exception.ExceptionUtils" %>
<h1>Some error happens. </h1>
<p>This may be caused by session time out or invalid operation.</p>
<p>You can click this button to login again:<button><a href="/toolbox/Logout">Login again</a></button> </p>



<p>
The following exception stack trace may be helpful to debug:
</p>
<p>
Exception is: <%= exception %>  
</p>
<p>
  <%= ExceptionUtils.getStackTrace(exception) %>  
</p>
