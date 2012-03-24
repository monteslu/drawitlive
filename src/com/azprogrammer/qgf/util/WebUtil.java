package com.azprogrammer.qgf.util;

import javax.servlet.http.HttpServletRequest;



/**
 * @author lmontes
 *
 */
public class WebUtil
{
    
    public static boolean isMobile(HttpServletRequest req){
        String agent = req.getHeader ("user-agent");
        if(agent == null){
            agent = "";
        }
        agent = agent.toUpperCase ();
        
        if((agent.indexOf ("ANDROID") > -1) || (agent.indexOf ("IPHONE") > -1) || (agent.indexOf ("IPAD") > -1)){
            return true;
        }
        else{
            return false;
        }
    }
    
    public static String getMobileTheme(HttpServletRequest req){
        String agent = req.getHeader ("user-agent");
        if(agent == null){
            agent = "";
        }
        agent = agent.toUpperCase ();
        
        if(agent.indexOf ("ANDROID") > -1){
            return "android";
        }
        else{
            return "iphone";
        }
    }
    
    // very simple method - does not do dns lookup or anything like that
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        email = email.trim();
        //"*@*.**" == 6 characters
        if (email.length() < 6) {
            return false;
        }
        if (email.indexOf("@") < 1) {
            return false;
        }

        //make sure there's a dot after the @
        if (email.lastIndexOf(".") < email.indexOf("@")) {
            return false;
        }

        //criteria is met, this just might be a real email
        return true;
    }

}
