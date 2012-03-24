package com.azprogrammer.qgf.model;

import java.util.Date;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;



/**
 * @author lmontes
 *
 */
@PersistenceCapable
public class WBMessage
{
    @Persistent
    protected String chatMessage = null;
    
    @Persistent
    protected WBGeometry geometry = null;
    
    @Persistent
    protected String fromUser = null;
    
    
    
    @Persistent(nullValue = NullValue.NONE)
    protected long creationTime = 0;
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;
    
    @Persistent
    protected Key wbKey;
    
    
    protected Set <String> userList;
    
    public WBMessage(){
        
    }

    /**
     * @return the chatMessage
     */
    public String getChatMessage ()
    {
        return chatMessage;
    }

    /**
     * @param chatMessage the chatMessage to set
     */
    public void setChatMessage (String chatMessage)
    {
        this.chatMessage = chatMessage;
    }

    /**
     * @return the geometry
     */
    public WBGeometry getGeometry ()
    {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     */
    public void setGeometry (WBGeometry geometry)
    {
        this.geometry = geometry;
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
     * @return the userList
     */
    public Set <String> getUserList ()
    {
        return userList;
    }

    /**
     * @param userList the userList to set
     */
    public void setUserList (Set <String> userList)
    {
        this.userList = userList;
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
    
    
}
