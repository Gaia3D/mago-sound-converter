package com.gaia3d.basic.structure;

import com.gaia3d.basic.structure.interfaces.SurfaceStructure;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents a face of a Gaia object.
 * It contains the indices and the face normal.
 * The face normal is calculated by the indices and the vertices.
 *
 * @author znkim
 * @see <a href="https://en.wikipedia.org/wiki/Face_normal">Face normal</a>
 * @since 1.0.0
 */
@Slf4j
@Getter
@Setter
public class GaiaSurface extends SurfaceStructure {
    /*private List<GaiaFace> faces = new ArrayList<>();*/

    public void calculateNormal(List<GaiaVertex> vertices) {
        for (GaiaFace face : faces) {
            face.calculateFaceNormal(vertices);
        }
    }

    public int[] getIndices() {
        int index = 0;
        int indicesCount = getIndicesCount();
        int[] resultIndices = new int[indicesCount];
        for (GaiaFace face : faces) {
            for (int indices : face.getIndices()) {
                resultIndices[index++] = indices;
            }
        }
        return resultIndices;
    }

    public int getIndicesCount() {
        int count = 0;
        for (GaiaFace face : faces) {
            count += face.getIndices().length;
        }
        return count;
    }

    public void clear() {
        faces.forEach(GaiaFace::clear);
        faces.clear();
    }

    public GaiaSurface clone() {
        GaiaSurface clonedSurface = new GaiaSurface();
        for (GaiaFace face : faces) {
            clonedSurface.getFaces().add(face.clone());
        }
        return clonedSurface;
    }

    private boolean getFacesWeldedWithFaces(List<GaiaFace> masterFaces, List<GaiaFace> resultFaces, Map<GaiaFace, GaiaFace> mapVisitedFaces) {
        boolean newFaceAdded = false;
        Map<Integer, Integer> mapIndices = new HashMap<>();

        // make a map of indices.***
        for (GaiaFace face : masterFaces) {
            int[] indices = face.getIndices();
            for (int index : indices) {
                mapIndices.put(index, index);
            }
        }

        int i = 0;
        int facesCount = faces.size();
        boolean finished = false;

        while (!finished && i < facesCount) {
            boolean newFaceAddedOneLoop = false;
            for (GaiaFace currFace : faces) {
                if (!mapVisitedFaces.containsKey(currFace)) {
                    int[] currFaceIndices = currFace.getIndices();
                    // if some indices of the currFace exists in the mapIndices, then add the face to the resultFaces.***
                    for (int index : currFaceIndices) {
                        if (mapIndices.containsKey(index)) {
                            resultFaces.add(currFace);
                            mapVisitedFaces.put(currFace, currFace);
                            newFaceAdded = true;
                            newFaceAddedOneLoop = true;

                            // add the indices of the face to the mapIndices.***
                            for (int index2 : currFaceIndices) {
                                mapIndices.put(index2, index2);
                            }
                            break;
                        }
                    }
                }
            }

            if (!newFaceAddedOneLoop) {
                finished = true;
            }

            i++;
        }

        return newFaceAdded;
    }

    public void getWeldedFaces(List<List<GaiaFace>> resultWeldedFaces) {
        List<GaiaFace> weldedFaces = new ArrayList<>();
        Map<GaiaFace, GaiaFace> mapVisitedFaces = new HashMap<>();
        for (GaiaFace masterFace : faces) {
            if (mapVisitedFaces.containsKey(masterFace)) {
                continue;
            }
            mapVisitedFaces.put(masterFace, masterFace);

            List<GaiaFace> masterFaces = new ArrayList<>();
            masterFaces.add(masterFace);

            weldedFaces.clear();
            if (this.getFacesWeldedWithFaces(masterFaces, weldedFaces, mapVisitedFaces)) {
                masterFaces.addAll(weldedFaces);
                for (GaiaFace weldedFace : weldedFaces) {
                    mapVisitedFaces.put(weldedFace, weldedFace);
                }
            }

            resultWeldedFaces.add(masterFaces);
        }
    }
}
