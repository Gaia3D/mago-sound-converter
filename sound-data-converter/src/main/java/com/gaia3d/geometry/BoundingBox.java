package com.gaia3d.geometry;

import org.joml.Vector3d;

public class BoundingBox
{
    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public BoundingBox()
    {
        minX = 0;
        minY = 0;
        minZ = 0;
        maxX = 0;
        maxY = 0;
        maxZ = 0;
    }

    public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void set(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void copyFrom(BoundingBox boundingBox)
    {
        minX = boundingBox.minX;
        minY = boundingBox.minY;
        minZ = boundingBox.minZ;
        maxX = boundingBox.maxX;
        maxY = boundingBox.maxY;
        maxZ = boundingBox.maxZ;
    }

    public void deleteObjects()
    {
    }

    public void initBox(double x, double y, double z)
    {
        minX = x;
        minY = y;
        minZ = z;
        maxX = x;
        maxY = y;
        maxZ = z;
    }

    public void getCenterPosition(Vector3d resultCenterPosition)
    {
        resultCenterPosition.x = (minX + maxX) / 2.0;
        resultCenterPosition.y = (minY + maxY) / 2.0;
        resultCenterPosition.z = (minZ + maxZ) / 2.0;
    }

    public void addBox(BoundingBox boundingBox)
    {
        if(boundingBox.minX < minX)
        {
            minX = boundingBox.minX;
        }

        if(boundingBox.minY < minY)
        {
            minY = boundingBox.minY;
        }

        if(boundingBox.minZ < minZ)
        {
            minZ = boundingBox.minZ;
        }

        if(boundingBox.maxX > maxX)
        {
            maxX = boundingBox.maxX;
        }

        if(boundingBox.maxY > maxY)
        {
            maxY = boundingBox.maxY;
        }

        if(boundingBox.maxZ > maxZ)
        {
            maxZ = boundingBox.maxZ;
        }
    }

    public void addPoint(double x, double y, double z)
    {
        if(x < minX)
        {
            minX = x;
        }

        if(y < minY)
        {
            minY = y;
        }

        if(z < minZ)
        {
            minZ = z;
        }

        if(x > maxX)
        {
            maxX = x;
        }

        if(y > maxY)
        {
            maxY = y;
        }

        if(z > maxZ)
        {
            maxZ = z;
        }
    }

    public double getMinX()
    {
        return minX;
    }

    public double getMinY()
    {
        return minY;
    }

    public double getMinZ()
    {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

}
