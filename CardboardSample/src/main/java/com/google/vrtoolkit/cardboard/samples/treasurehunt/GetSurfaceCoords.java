package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.util.Log;

import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

import java.util.ArrayList;

/**
 * Created by Deepak on 12-03-2016.
 */
public class GetSurfaceCoords {
    public float[] coordSet;
    public float[] coordCol;
    public float[] coordNorm;
    ArrayList<Float> coordSetL = new ArrayList<Float>();
    ArrayList<Float> coordColL = new ArrayList<Float>();
    ArrayList<Float> coordNormL = new ArrayList<Float>();
    Vector3d lastPoint = new Vector3d();

    public void updatePointSet(Vector3d vector3d)
    {
        Log.d("MainActivity","Updated : "+vector3d.x+" : "+vector3d.y+" : "+vector3d.z);
        for(int i=0;i<72;i++)
        {
            coordSetL.add(WorldLayoutData.CUBE_COORDS[i]/40+(float)vector3d.x);
            coordNormL.add(WorldLayoutData.CUBE_NORMALS[i]);
            coordSetL.add(WorldLayoutData.CUBE_COORDS[++i]/40+(float)vector3d.y);
            coordNormL.add(WorldLayoutData.CUBE_NORMALS[i]);
            coordSetL.add(WorldLayoutData.CUBE_COORDS[++i]/40+(float)vector3d.z);
            coordNormL.add(WorldLayoutData.CUBE_NORMALS[i]);
        }
        for(int i=0;i<96;i++)
        {
            coordColL.add(WorldLayoutData.CUBE_COLORS[i]);
        }
        coordSet = new float[coordSetL.size()];
        int i = 0;
        for (Float f : coordSetL) {
            coordSet[i++] = f; // Or whatever default you want.
        }

        coordCol = new float[coordColL.size()];
        i = 0;
        for (Float f : coordColL) {
            coordCol[i++] = f; // Or whatever default you want.
        }

        coordNorm = new float[coordNormL.size()];
        i = 0;
        for (Float f : coordNormL) {
            coordNorm[i++] = f; // Or whatever default you want.
        }
        lastPoint = vector3d;
    }
}
