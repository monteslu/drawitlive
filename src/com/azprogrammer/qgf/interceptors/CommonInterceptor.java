package com.azprogrammer.qgf.interceptors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.azprogrammer.qgf.auth.Provider;
import com.azprogrammer.qgf.auth.ProviderLogin;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;



public class CommonInterceptor extends HandlerInterceptorAdapter
{

    protected List <Provider> m_providers;
    
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle (HttpServletRequest request,
                              HttpServletResponse response, Object handler)
                                                                           throws Exception
    {
        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser(); // req.getUserPrincipal()?!?
        
        
        Set<String> attributes = new HashSet<String>();
        
        if (user != null) {
            request.setAttribute ("user", user);
            request.setAttribute ("logoutURL", userService.createLogoutURL (request.getRequestURI ()));
        } else {
            List<ProviderLogin> logins = new ArrayList <ProviderLogin> ();
            
            for (Provider provider : getProviders ())
            {
                ProviderLogin login = new ProviderLogin (provider);
                login.setLoginURL (userService.createLoginURL(request.getRequestURI(), null, provider.getOpenIdURL (), attributes));
                logins.add (login);
            }
            
            request.setAttribute ("logins", logins);
        }
        
        return true;
    }


    /**
     * @return the providers
     */
    public List <Provider> getProviders ()
    {
        return m_providers;
    }


    /**
     * @param providers the providers to set
     */
    public void setProviders (List <Provider> providers)
    {
        m_providers = providers;
    }
}
