package com.itsinic.sso.controller;

import com.itsinic.sso.WebConstants;
import com.itsinic.sso.model.App;
import com.itsinic.sso.model.Ticket;
import com.itsinic.sso.model.User;
import com.itsinic.sso.service.UserService;
import com.itsinic.sso.utils.DESUtils;
import com.itsinic.sso.utils.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by HAIOU on 2016/3/16.
 * SSO认证中心
 */
@Controller
public class SSOAuthController {

    Logger logger = LoggerFactory.getLogger(SSOAuthController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/SSOAuth", method = RequestMethod.GET)
    public String SSOAuthGET(HttpServletRequest request, HttpServletResponse response,Model model) throws IOException, ServletException {

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        if ("preLogin".equals(action)) {
            return preLogin(request, response);
        }else {
            logger.error("SSOAuthGET Action can not be empty");
            return "login";
            /*logger.error("指令错误");
            out.print("Action can not be empty");*/
        }
    }

    @RequestMapping(value = "/SSOAuth", method = RequestMethod.POST)
    public String SSOAuthPOST(HttpServletRequest request, HttpServletResponse response,Model model) throws IOException, ServletException {

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        if ("login".equals(action)) {
            return doLogin(request, response,model);
        } else if ("authTicket".equals(action)) {
            return authTicket(request, response);
        } else if ("logout".equals(action)) {
            return doLogout(request, response);
        } else {
            logger.error("指令错误");
            out.print("Action can not be empty");
        }
        out.close();

        return null;

    }

    private String preLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie ticket = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(WebConstants.COOKIENAME)) {
                    ticket = cookie;
                    break;
                }
            }
        }
        if (ticket == null) {
            //request.getRequestDispatcher("login.jsp").forward(request, response);
            return "login";
        } else {
            String encodedTicket = ticket.getValue();
            String decodedTicket = DESUtils.decrypt(encodedTicket, WebConstants.SECRETKEY);
            if (WebConstants.TICKETS.containsKey(decodedTicket)) {
                String setCookieURL = request.getParameter("setCookieURL");
                String gotoURL = request.getParameter("gotoURL");
                if (setCookieURL != null)
                    response.sendRedirect(setCookieURL + "?ticket=" + encodedTicket + "&expiry=" + ticket.getMaxAge()
                            + "&gotoURL=" + gotoURL);
                return null;
            } else {
                //request.getRequestDispatcher("login.jsp").forward(request, response);
                return "login";
            }
        }
    }

    private String authTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder result = new StringBuilder("{");
        PrintWriter out = response.getWriter();
        String encodedTicket = request.getParameter("cookieName");
        if (encodedTicket == null) {
            result.append("\"error\":true,\"errorInfo\":\"Ticket can not be empty!\"");
        } else {
            boolean flag = false;
            String decodedTicket = DESUtils.decrypt(encodedTicket, WebConstants.SECRETKEY);
            if (WebConstants.TICKETS.containsKey(decodedTicket)){
                String appEnName = request.getParameter("appEnName");
                for(App app : WebConstants.TICKETS.get(decodedTicket).getApps()){
                    if(app.getAppEnName().equals(appEnName)){
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    result.append("\"error\":false,\"username\":").append(WebConstants.TICKETS.get(decodedTicket).getUsername());
                }else {
                    result.append("\"error\":true,\"errorInfo\":\"Ticket is not found!\"");
                }
            }
            else
                result.append("\"error\":true,\"errorInfo\":\"Ticket is not found!\"");
        }
        result.append("}");
        out.print(result);
        return null;
    }

    private String doLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder result = new StringBuilder("{");
        PrintWriter out = response.getWriter();
        String encodedTicket = request.getParameter("cookieName");
        if (encodedTicket == null) {
            result.append("\"error\":true,\"errorInfo\":\"Ticket can not be empty!\"");
        } else {
            String decodedTicket = DESUtils.decrypt(encodedTicket, WebConstants.SECRETKEY);
            WebConstants.TICKETS.remove(decodedTicket);
            result.append("\"error\":false");
        }
        result.append("}");
        out.print(result);
        return null;
    }

    private String doLogin(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String password =  request.getParameter("password");
        User user = userService.getUserByName(username);
        if (user == null || !user.getPassword().equals(MD5.getInstrance().getMD5String4(password))) {
            //request.getRequestDispatcher("login.jsp?errorInfo=username or password is wrong!").forward(request,response);
            model.addAttribute("errorInfo","用户名或者密码错误!");
            return "login";
        } else {
            String ticketKey = UUID.randomUUID().toString().replace("-", "");
            String encodedticketKey = DESUtils.encrypt(ticketKey, WebConstants.SECRETKEY);

            Timestamp createTime = new Timestamp(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTime(createTime);
            cal.add(Calendar.MINUTE, WebConstants.TICKETTIMEOUT);
            Timestamp recoverTime = new Timestamp(cal.getTimeInMillis());
            Ticket ticket = new Ticket(username, createTime, recoverTime);

            WebConstants.TICKETS.put(ticketKey, ticket);

            String[] checks = request.getParameterValues("autoAuth");
            int expiry = -1;
            if (checks != null && "1".equals(checks[0]))
                expiry = 7 * 24 * 3600;
            Cookie cookie = new Cookie(WebConstants.COOKIENAME, encodedticketKey);
            cookie.setSecure(WebConstants.SECURE);// 为true时用于https
            cookie.setMaxAge(expiry);
            cookie.setPath("/");
            response.addCookie(cookie);

            String setCookieURL = request.getParameter("setCookieURL");
            String gotoURL = request.getParameter("gotoURL");

            PrintWriter out = response.getWriter();
            /*if(StringUtils.isEmpty(setCookieURL)&&StringUtils.isEmpty(gotoURL)){
                //out.print("SSO Login Success");
                //model.addAttribute("encodedticketKey",encodedticketKey);
                if("admin".equals(username)){
                    App app1 = new App();
                    app1.setAppEnName("app1");
                    app1.setAppName("项目1");
                    app1.setAppUrl("http://web1.itsinic.com:8080/WebSSODemo");
                    ticket.getApps().add(app1);

                    App app2 = new App();
                    app2.setAppEnName("app2");
                    app2.setAppName("项目2");
                    app2.setAppUrl("http://web2.itsinic.com:8080/WebSSODemo2");
                    ticket.getApps().add(app2);
                }else{
                    App app1 = new App();
                    app1.setAppEnName("app1");
                    app1.setAppName("项目1");
                    app1.setAppUrl("http://web1.itsinic.com:8080/WebSSODemo");
                    ticket.getApps().add(app1);
                }
                model.addAttribute("apps", ticket.getApps());
                return "main";
            }else{

                out.print("<script type='text/javascript'>");
                out.print("document.write(\"<form id='url' method='post' action='" + setCookieURL + "'>\");");
                out.print("document.write(\"<input type='hidden' name='username' value='" + username + "' />\");");
                out.print("document.write(\"<input type='hidden' name='gotoURL' value='" + gotoURL + "' />\");");
                out.print("document.write(\"<input type='hidden' name='ticket' value='" + encodedticketKey + "' />\");");
                out.print("document.write(\"<input type='hidden' name='expiry' value='" + expiry + "' />\");");
                out.print("document.write('</form>');");
                out.print("document.getElementById('url').submit();");
                out.print("</script>");

                return "login";
            }*/
            if("admin".equals(username)){
                App app1 = new App();
                app1.setAppEnName("app1");
                app1.setAppName("项目1");
                app1.setAppUrl("http://web1.itsinic.com:8080/WebSSODemo");
                ticket.getApps().add(app1);

                App app2 = new App();
                app2.setAppEnName("app2");
                app2.setAppName("项目2");
                app2.setAppUrl("http://web2.itsinic.com:8080/WebSSODemo2");
                ticket.getApps().add(app2);
            }else{
                App app1 = new App();
                app1.setAppEnName("app1");
                app1.setAppName("项目1");
                app1.setAppUrl("http://web1.itsinic.com:8080/WebSSODemo");
                ticket.getApps().add(app1);
            }
            model.addAttribute("apps", ticket.getApps());
            return "main";
        }

    }
    /*
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(){

        String decodedTicket = DESUtils.decrypt(encodedTicket, WebConstants.SECRETKEY);
        WebConstants.TICKETS.remove(decodedTicket);

        return "login";
    }
    */
    public boolean isPerm(String username,String appUrl){
        boolean flag = false;
        /*if(WebConstants.TICKETS!=null && WebConstants.TICKETS.get(username).getAppUrls().contains(appUrl)){
            flag = true;
        }*/
        return flag;
    }

}
