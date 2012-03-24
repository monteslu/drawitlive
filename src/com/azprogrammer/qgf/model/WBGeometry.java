package com.azprogrammer.qgf.model;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;


/**
 * @author lmontes
 *
 */

@PersistenceCapable
public class WBGeometry  implements Serializable
{
  
    @NotPersistent
    protected String dataStr; //non-persisted temp for marshalling to json
    
    @NotPersistent
    protected String fromUser; //only here to keep JSON-lib from complaining, will not persist.
    
    @Persistent
    protected String shapeType;
    
    @Persistent
    protected String lineColor;
    
    @Persistent
    protected String fillColor;
    
    @Persistent
    protected double lineStroke;
    
    @Persistent
    protected boolean filled = false;
    
    @Persistent
    protected int[] xPts;
    
    @Persistent
    protected int[] yPts;
    
    @Persistent
    protected long cTime;
    
    @Persistent
    protected long cRand;
    
    
    
    @Persistent
    protected String text;
    
    @Persistent
    protected Text data;
    
    

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;
    
    
    //some things like images may have an id associated
    protected int shapeId;
    
    public WBGeometry(){
        
    }

    /**
     * @return the shapeType
     */
    public String getShapeType ()
    {
        return shapeType;
    }

    /**
     * @param shapeType the shapeType to set
     */
    public void setShapeType (String shapeType)
    {
        this.shapeType = shapeType;
    }

    

    /**
     * @return the lineColor
     */
    public String getLineColor ()
    {
        return lineColor;
    }

    /**
     * @param lineColor the lineColor to set
     */
    public void setLineColor (String lineColor)
    {
        this.lineColor = lineColor;
    }

    /**
     * @return the fillColor
     */
    public String getFillColor ()
    {
        return fillColor;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor (String fillColor)
    {
        this.fillColor = fillColor;
    }

    /**
     * @return the shapeId
     */
    public int getShapeId ()
    {
        return shapeId;
    }

    /**
     * @param shapeId the shapeId to set
     */
    public void setShapeId (int shapeId)
    {
        this.shapeId = shapeId;
    }

    /**
     * @return the lineStroke
     */
    public double getLineStroke ()
    {
        return lineStroke;
    }

    /**
     * @param lineStroke the lineStroke to set
     */
    public void setLineStroke (double lineStroke)
    {
        this.lineStroke = lineStroke;
    }


    /**
     * @return the filled
     */
    public boolean isFilled ()
    {
        return filled;
    }

    /**
     * @param filled the filled to set
     */
    public void setFilled (boolean filled)
    {
        this.filled = filled;
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
     * @return the xPts
     */
    public int[] getxPts ()
    {
        return xPts;
    }

    /**
     * @param xPts the xPts to set
     */
    public void setxPts (int[] xPts)
    {
        this.xPts = xPts;
    }

    /**
     * @return the yPts
     */
    public int[] getyPts ()
    {
        return yPts;
    }

    /**
     * @param yPts the yPts to set
     */
    public void setyPts (int[] yPts)
    {
        this.yPts = yPts;
    }

    /**
     * @return the cTime
     */
    public long getcTime ()
    {
        return cTime;
    }

    /**
     * @param cTime the cTime to set
     */
    public void setcTime (long cTime)
    {
        this.cTime = cTime;
    }

    /**
     * @return the cRand
     */
    public long getcRand ()
    {
        return cRand;
    }

    /**
     * @param cRand the cRand to set
     */
    public void setcRand (long cRand)
    {
        this.cRand = cRand;
    }

    /**
     * @return the text
     */
    public String getText ()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText (String text)
    {
        this.text = text;
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
     * @return the data
     */
    public Text getData ()
    {
        return data;
    }
    
   

    /**
     * @param data the data to set
     */
    public void setData (Text data)
    {
        this.data = data;
    }

    /**
     * @return the dataStr
     */
    public String getDataStr ()
    {
        return dataStr;
    }

    /**
     * @param dataStr the dataStr to set
     */
    public void setDataStr (String dataStr)
    {
        this.dataStr = dataStr;
    }


}
