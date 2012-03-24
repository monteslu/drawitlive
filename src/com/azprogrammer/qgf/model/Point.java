package com.azprogrammer.qgf.model;

import java.io.Serializable;



/**
 * @author lmontes
 *
 */
public class Point implements Serializable
{
    protected double x;
    protected double y;
    
    public Point(){
        
        
    }
    
    /**
     * @return the x
     */
    public double getX ()
    {
        return x;
    }
    /**
     * @param x the x to set
     */
    public void setX (double x)
    {
        this.x = x;
    }
    /**
     * @return the y
     */
    public double getY ()
    {
        return y;
    }
    /**
     * @param y the y to set
     */
    public void setY (double y)
    {
        this.y = y;
    }
}
