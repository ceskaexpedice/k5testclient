<%
session.invalidate();

String reqAddr = "index.vm";
if (request.getParameter("redirectURL")!=null) {
    reqAddr = request.getParameter("redirectURL");
}
response.sendRedirect(reqAddr);

%>
