package com.azprogrammer.qgf.auth;



/**
 * @author lmontes
 *
 */
public class Provider
{
    protected String m_providerName;
    protected String m_openIdURL;
    protected String m_shortName;
    /**
     * @return the providerName
     */
    public String getProviderName ()
    {
        return m_providerName;
    }
    /**
     * @param providerName the providerName to set
     */
    public void setProviderName (String providerName)
    {
        m_providerName = providerName;
    }
    /**
     * @return the openIdURL
     */
    public String getOpenIdURL ()
    {
        return m_openIdURL;
    }
    /**
     * @param openIdURL the openIdURL to set
     */
    public void setOpenIdURL (String openIdURL)
    {
        m_openIdURL = openIdURL;
    }

    /**
     * @return the shortName
     */
    public String getShortName ()
    {
        return m_shortName;
    }
    /**
     * @param shortName the shortName to set
     */
    public void setShortName (String shortName)
    {
        m_shortName = shortName;
    }

}
