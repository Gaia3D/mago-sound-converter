package com.gaia3d.sound.dataStructure;

public class Vertex
{
    public double x;
    public double y;
    public double z;

    public int index;

    public double[] objNLv; // 소음레벨 배열 [dB(A)]

    public Vertex()
    {
        x = 0;
        y = 0;
        z = 0;
    }

    public Vertex(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
