<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@page trimDirectiveWhitespaces="true"%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <meta name="description" content="Digitized documents access aplication." />
    <meta name="keywords" content="periodical, monograph, library,  book, publication, kramerius, fedora" />
    <meta name="author" content="INCAD, www.incad.cz" />


    <title>Kramerius 5</title>
    
</head>
	<body>
   <div id="logininfo" style="text-align: center;"></div>

	<div id="dialogForm">
	    
	    <form name=login id="loginForm" action="j_security_check" method="post">
	      <table align=center >
	        <tr>
	            <td>
	               <div id="status" style="color:red;"></div>
	            </td>   
            </tr>
            
            
            <tr>
	          <td>
	             <span id="name">Jméno:</span><br>
	             <input type="text" size="30" name="j_username" style="border:1px solid silver;" ><br>
	             <span id="password">Heslo:</span><br>
	             <input type="password" name="j_password" size="30" style="border:1px solid silver;"><br>
                 <div style="padding:3px;"></div>   
	             <input id="button" type="submit"  value="Přihlášení" alt="Login">
	          </td>
	        </tr>
	      </table>
	    </form>
	</div>
	</body>
</html>
