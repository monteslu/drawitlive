package com.azprogrammer.qgf.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;



/**
 * @author lmontes
 *
 */

@PersistenceCapable
public class WBChannel  implements Serializable
{

    
    
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;
    
    @Persistent
    protected String sessionId;
    
    @Persistent
    protected long time;
    
    @Persistent    
    protected String userName;
    
    @Persistent
    protected String userAgent;
    
    @Persistent
    protected Key wbKey;
    
    public WBChannel()
    {
        
        
    }

    /**
     * @return the key
     */
    public Key getKey ()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey (Key key)
    {
        this.key = key;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId ()
    {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId (String sessionId)
    {
        this.sessionId = sessionId;
    }

    /**
     * @return the time
     */
    public long getTime ()
    {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime (long time)
    {
        this.time = time;
    }

    /**
     * @return the userName
     */
    public String getUserName ()
    {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName (String userName)
    {
        this.userName = userName;
    }

    /**
     * @return the userAgent
     */
    public String getUserAgent ()
    {
        return userAgent;
    }

    /**
     * @param userAgent the userAgent to set
     */
    public void setUserAgent (String userAgent)
    {
        this.userAgent = userAgent;
    }

    /**
     * @return the wbKey
     */
    public Key getWbKey ()
    {
        return wbKey;
    }

    /**
     * @param wbKey the wbKey to set
     */
    public void setWbKey (Key wbKey)
    {
        this.wbKey = wbKey;
    }


    
}
