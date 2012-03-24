package com.azprogrammer.qgf.auth;



/**
 * @author lmontes
 *
 */
public class ProviderLogin
{
    protected Provider m_provider;
    protected String m_loginURL;
    
    /**
     * 
     * @param provider
     */
    public ProviderLogin(Provider provider){
        setProvider (provider);        
    }

    /**
     * @return the provider
     */
    public Provider getProvider ()
    {
        return m_provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider (Provider provider)
    {
        m_provider = provider;
    }

    /**
     * @return the loginURL
     */
    public String getLoginURL ()
    {
        return m_loginURL;
    }

    /**
     * @param loginURL the loginURL to set
     */
    public void setLoginURL (String loginURL)
    {
        m_loginURL = loginURL;
    }
    
}
