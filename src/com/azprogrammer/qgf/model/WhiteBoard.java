package com.azprogrammer.qgf.model;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;



/**
 * @author lmontes
 *
 */

@PersistenceCapable
public class WhiteBoard
{
    public static final String STATUS_ACTIVE = "A";
    public static final String STATUS_INACTIVE = "I";
    
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;
    
    @Persistent
    protected String createdBySessionId;
    
    @Persistent 
    protected String hangoutURL;
    
    @Persistent    
    protected String referer;
    
    @Persistent    
    protected String userAgent;
    
    @Persistent    
    protected String status = STATUS_ACTIVE;
    
    @Persistent Date creationDate;

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
     * @return the createdBySessionId
     */
    public String getCreatedBySessionId ()
    {
        return createdBySessionId;
    }

    /**
     * @param createdBySessionId the createdBySessionId to set
     */
    public void setCreatedBySessionId (String createdBySessionId)
    {
        this.createdBySessionId = createdBySessionId;
    }

    /**
     * @return the referer
     */
    public String getReferer ()
    {
        return referer;
    }

    /**
     * @param referer the referer to set
     */
    public void setReferer (String referer)
    {
        this.referer = referer;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate ()
    {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate (Date creationDate)
    {
        this.creationDate = creationDate;
    }
    
    public String getKeyString(){
        if(key == null){
            return null;
        }else{
            
            return KeyFactory.keyToString (key);
        }
        
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
     * @return the status
     */
    public String getStatus ()
    {
        return status;
    }



    /**
     * @param status the status to set
     */
    public void setStatus (String status)
    {
        this.status = status;
    }



    /**
     * @return the hangoutURL
     */
    public String getHangoutURL ()
    {
        return hangoutURL;
    }



    /**
     * @param hangoutURL the hangoutURL to set
     */
    public void setHangoutURL (String hangoutURL)
    {
        this.hangoutURL = hangoutURL;
    }
    

}
