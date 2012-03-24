package com.azprogrammer.qgf.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class WBEmail
{
    public static final int STATUS_SENT = 1;
    public static final int STATUS_ERROR = 0;
    
    @Persistent
    protected String fromUser = null;
    
    @Persistent(nullValue = NullValue.NONE)
    protected long creationTime = 0;
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;
    
    @Persistent
    protected Key wbKey;
    
    @Persistent
    protected String toAddress;
    
    @Persistent
    protected int status;

    public WBEmail(){        
        
    }

    /**
     * @return the fromUser
     */
    public String getFromUser ()
    {
        return fromUser;
    }

    /**
     * @param fromUser the fromUser to set
     */
    public void setFromUser (String fromUser)
    {
        this.fromUser = fromUser;
    }

    /**
     * @return the creationTime
     */
    public long getCreationTime ()
    {
        return creationTime;
    }

    /**
     * @param creationTime the creationTime to set
     */
    public void setCreationTime (long creationTime)
    {
        this.creationTime = creationTime;
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

    /**
     * @return the toAddress
     */
    public String getToAddress ()
    {
        return toAddress;
    }

    /**
     * @param toAddress the toAddress to set
     */
    public void setToAddress (String toAddress)
    {
        this.toAddress = toAddress;
    }

    /**
     * @return the status
     */
    public int getStatus ()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus (int status)
    {
        this.status = status;
    }
    
}
