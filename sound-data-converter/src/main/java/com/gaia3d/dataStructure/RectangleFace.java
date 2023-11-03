package com.gaia3d.dataStructure;

public class RectangleFace
{
    public int index1;
    public int index2;
    public int index3;
    public int index4;

    public RectangleFace()
    {
        index1 = 0;
        index2 = 0;
        index3 = 0;
        index4 = 0;
    }

    public RectangleFace(int index1, int index2, int index3, int index4)
    {
        this.index1 = index1;
        this.index2 = index2;
        this.index3 = index3;
        this.index4 = index4;
    }

    public void getTrianglesIndices(int[] indices)
    {
        indices[0] = index1;
        indices[1] = index2;
        indices[2] = index3;
        indices[3] = index1;
        indices[4] = index3;
        indices[5] = index4;
    }
}
