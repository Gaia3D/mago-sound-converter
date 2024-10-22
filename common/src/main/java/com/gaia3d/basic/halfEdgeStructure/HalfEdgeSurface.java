package com.gaia3d.basic.halfEdgeStructure;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3d;

import java.util.*;

@Setter
@Getter
@Slf4j
public class HalfEdgeSurface {

    private List<HalfEdge> halfEdges = new ArrayList<>();
    private List<HalfEdgeVertex> vertices = new ArrayList<>();
    private List<HalfEdgeFace> faces = new ArrayList<>();

    public void setTwins() {
        Map<HalfEdgeVertex, List<HalfEdge>> mapVertexOutingHEdges = new HashMap<>();
        Map<HalfEdgeVertex, List<HalfEdge>> mapVertexIncomingHEdges = new HashMap<>();

        for (HalfEdge halfEdge : halfEdges) {
            HalfEdgeVertex startVertex = halfEdge.getStartVertex();
            HalfEdgeVertex endVertex = halfEdge.getEndVertex();

            List<HalfEdge> outingEdges = mapVertexOutingHEdges.computeIfAbsent(startVertex, k -> new ArrayList<>());
            outingEdges.add(halfEdge);

            List<HalfEdge> incomingEdges = mapVertexIncomingHEdges.computeIfAbsent(endVertex, k -> new ArrayList<>());
            incomingEdges.add(halfEdge);
        }

        int vertexCount = vertices.size();
        for (int i = 0; i < vertexCount; i++) {
            HalfEdgeVertex vertex = vertices.get(i);
            List<HalfEdge> outingEdges = mapVertexOutingHEdges.get(vertex);
            List<HalfEdge> incomingEdges = mapVertexIncomingHEdges.get(vertex);

            if (outingEdges == null || incomingEdges == null) {
                continue;
            }

            int outingEdgesCount = outingEdges.size();
            int incomingEdgesCount = incomingEdges.size();
            for (int j = 0; j < outingEdgesCount; j++) {
                HalfEdge outingEdge = outingEdges.get(j);

                if (outingEdge.hasTwin()) {
                    continue;
                }
                for (int k = 0; k < incomingEdgesCount; k++) {
                    HalfEdge incomingEdge = incomingEdges.get(k);

                    if (incomingEdge.hasTwin()) {
                        continue;
                    }
                    if (outingEdge.setTwin(incomingEdge)) {
                        break;
                    }
                }
            }
        }
    }

    public boolean TEST_addRandomPositionToVertices() {
        int vertexCount = vertices.size();
        double offset = 2.0;
        for (int i = 0; i < vertexCount; i++) {
            HalfEdgeVertex vertex = vertices.get(i);
            PositionType positionType = vertex.getPositionType();
            if (positionType != PositionType.INTERIOR) {
                if (vertex.getPosition() != null) {
                    //Vector3d randomOffset = new Vector3d(Math.random() * offset, Math.random() * offset, Math.random() * offset);
                    Vector3d randomOffset = new Vector3d(0.0, 0.0, 40.0);
                    vertex.getPosition().add(randomOffset);
                }
            }

        }

        return true;
    }


    public void doTrianglesReduction() {
//        if(this.TEST_addRandomPositionToVertices())
//        {
//            return;
//        }

        // 1rst, find possible halfEdges to remove.***
        // Reasons to remove a halfEdge:
        // 1. The halfEdge is very short. (small length).
        // 2. All triangles around the startVertex has a similar normal.
        //----------------------------------------------------------------
        int originalFacesCount = faces.size();
        int originalHalfEdgesCount = halfEdges.size();
        int originalVerticesCount = vertices.size();

        // Make a map ordered by squaredLength.***
        TreeMap<Double, List<HalfEdge>> mapHalfEdgesOrderedBySquaredLength = new TreeMap<>();
        double averageSquaredLength = 0.0;
        for (HalfEdge halfEdge : halfEdges) {
            double squaredLength = halfEdge.getSquaredLength();
            List<HalfEdge> halfEdges = mapHalfEdgesOrderedBySquaredLength.computeIfAbsent(squaredLength, k -> new ArrayList<>());
            halfEdges.add(halfEdge);
            averageSquaredLength += squaredLength;
        }
        averageSquaredLength /= halfEdges.size();
        double averageLength = Math.sqrt(averageSquaredLength);

        double minSquaredLength = averageSquaredLength * 10.0;
        List<List<HalfEdge>> orderedHalfEdgesList = new ArrayList<>(mapHalfEdgesOrderedBySquaredLength.values());
        List<HalfEdge> orderedHalfEdges = new ArrayList<>();

        int orderedHalfEdgesListCount = orderedHalfEdgesList.size();
        for (int i = 0; i < orderedHalfEdgesListCount; i++) {
            List<HalfEdge> halfEdges = orderedHalfEdgesList.get(i);
            orderedHalfEdges.addAll(halfEdges);
        }
        int halfEdgesCount = orderedHalfEdges.size();
        log.info("halfEdgesCount = " + halfEdgesCount);
        int counterAux = 0;
        int hedgesCollapsedCount = 0;

        for (int i = 0; i < halfEdgesCount; i++) {
            HalfEdge halfEdge = orderedHalfEdges.get(i);
            if (halfEdge.getStatus() == ObjectStatus.DELETED) {
                continue;
            }

            if (!halfEdge.hasTwin()) {
                continue;
            }

            if (halfEdge.isDegenerated()) {
                //halfEdge.setStatus(ObjectStatus.DELETED);
                //continue;
            }

            HalfEdgeVertex startVertex = halfEdge.getStartVertex();
            PositionType positionType = startVertex.getPositionType();
            if (positionType != PositionType.INTERIOR) {
                continue;
            }

            boolean testDebug = false;
            if (i == 60610) {
                testDebug = true;
            }
            if (collapseHalfEdge(halfEdge, i, testDebug)) {
                hedgesCollapsedCount++;
                counterAux++;
            }

            if (counterAux >= 2000) {
                counterAux = 0;
                log.info("halfEdges deleted = " + hedgesCollapsedCount);
            }
        }

        // delete objects that status is DELETED.***
        // delete halfEdges that status is DELETED.***
        halfEdgesCount = this.halfEdges.size();
        List<HalfEdge> copyHalfEdges = new ArrayList<>(this.halfEdges);
        this.halfEdges.clear();
        for (int i = 0; i < halfEdgesCount; i++) {
            HalfEdge halfEdge = copyHalfEdges.get(i);
            if (halfEdge.getStatus() != ObjectStatus.DELETED) {
                this.halfEdges.add(halfEdge);
            }
        }

        // delete vertices that status is DELETED.***
        int verticesCount = this.vertices.size();
        List<HalfEdgeVertex> copyVertices = new ArrayList<>(this.vertices);
        this.vertices.clear();
        for (int i = 0; i < verticesCount; i++) {
            HalfEdgeVertex vertex = copyVertices.get(i);
            if (vertex.getStatus() != ObjectStatus.DELETED) {
                this.vertices.add(vertex);
            }
        }

        // delete faces that status is DELETED.***
        int facesCount = this.faces.size();
        List<HalfEdgeFace> copyFaces = new ArrayList<>(this.faces);
        this.faces.clear();
        for (int i = 0; i < facesCount; i++) {
            HalfEdgeFace face = copyFaces.get(i);
            if (face.getStatus() != ObjectStatus.DELETED) {
                this.faces.add(face);
            }
        }

        int finalFacesCount = faces.size();
        int finalHalfEdgesCount = halfEdges.size();
        int finalVerticesCount = vertices.size();

        int facesCountDiff = originalFacesCount - finalFacesCount;
        int halfEdgesCountDiff = originalHalfEdgesCount - finalHalfEdgesCount;
        int verticesCountDiff = originalVerticesCount - finalVerticesCount;
    }

    public Map getVertexAllOutingEdgesMap(Map<HalfEdgeVertex, List<HalfEdge>> vertexEdgesMap) {
        // This function returns a map of all halfEdges that startVertex is the key.***
        if (vertexEdgesMap == null) {
            vertexEdgesMap = new HashMap<>();
        }

        for (HalfEdge halfEdge : halfEdges) {
            if (halfEdge.getStatus() == ObjectStatus.DELETED) {
                continue;
            }
            HalfEdgeVertex startVertex = halfEdge.getStartVertex();
            if (startVertex.getStatus() == ObjectStatus.DELETED) {
                continue;
            }
            List<HalfEdge> edges = vertexEdgesMap.get(startVertex);
            if (edges == null) {
                edges = new ArrayList<>();
                vertexEdgesMap.put(startVertex, edges);
            }
            edges.add(halfEdge);
        }
        return vertexEdgesMap;
    }

    private boolean check() {
//        int vertexCount = vertices.size();
//        for (int i = 0; i < vertexCount; i++)
//        {
//            HalfEdgeVertex vertex = vertices.get(i);
//            if(vertex.getStatus() != ObjectStatus.DELETED)
//            {
//                if(vertex.getOutingHalfEdge().getStatus() == ObjectStatus.DELETED)
//                {
//                    int hola = 0;
//                }
//            }
//        }

        int hedgesCount = halfEdges.size();
        for (int i = 0; i < hedgesCount; i++) {
            HalfEdge hedge = halfEdges.get(i);
            if (hedge.getStatus() == ObjectStatus.DELETED) {
                continue;
            }
        }

//        hedgesCount = halfEdges.size();
//        for (int i = 0; i < hedgesCount; i++)
//        {
//            HalfEdge hedge = halfEdges.get(i);
//            ObjectStatus status = hedge.getStatus();
//            List<HalfEdge> hedgesLoop = new ArrayList<>();
//
//            hedgesLoop = hedge.getLoop(hedgesLoop);
//            int hedgeLoopCount = hedgesLoop.size();
//            for (int j = 0; j < hedgeLoopCount; j++)
//            {
//                HalfEdge hedgeLoop = hedgesLoop.get(j);
//                if(hedgeLoop.getStatus() != status)
//                {
//                    int hola = 0;
//                }
//
//                if(hedgeLoop.getStartVertex() == null)
//                {
//                    int hola = 0;
//                }
//
//                if(status != ObjectStatus.DELETED)
//                {
//                    if(hedgeLoop.getPrev() == null)
//                    {
//                        int hola = 0;
//                    }
//                    if(hedgeLoop.getStartVertex().getStatus() == ObjectStatus.DELETED)
//                    {
//                        int hola = 0;
//                    }
//                }
//            }
//
//        }
        return true;
    }

    private HalfEdgeCollapseData getHalfEdgeCollapsingData(HalfEdge halfEdge, HalfEdgeCollapseData resultHalfEdgeCollapseData) {
        if (resultHalfEdgeCollapseData == null) {
            resultHalfEdgeCollapseData = new HalfEdgeCollapseData();
        }

        // HalfEdge A.*********************************************************************
        HalfEdge halfEdgeA = halfEdge;
        resultHalfEdgeCollapseData.setHalfEdgeA(halfEdgeA);
        resultHalfEdgeCollapseData.setStartVertexA(halfEdgeA.getStartVertex());

        List<HalfEdge> halfEdgesLoopA = new ArrayList<>();
        halfEdgesLoopA = halfEdgeA.getLoop(halfEdgesLoopA);
        resultHalfEdgeCollapseData.setHalfEdgesLoopA(halfEdgesLoopA);

        List<HalfEdge> halfEdgesAExterior = new ArrayList<>();
        int hedgesCount = halfEdgesLoopA.size();
        for (int i = 0; i < hedgesCount; i++) {
            HalfEdge hedgeA = halfEdgesLoopA.get(i);
            if (hedgeA == halfEdgeA) {
                continue;
            }
            if (hedgeA.getStatus() == ObjectStatus.DELETED) {
                int hola = 0;
            }
            HalfEdge twin = hedgeA.getTwin();
            if (twin != null && twin.getStatus() != ObjectStatus.DELETED) {
                halfEdgesAExterior.add(twin);
            }
        }
        resultHalfEdgeCollapseData.setHalfEdgesAExterior(halfEdgesAExterior);
        resultHalfEdgeCollapseData.setFaceA(halfEdgeA.getFace());

        // HalfEdge B.*********************************************************************
        HalfEdge halfEdgeB = halfEdgeA.getTwin();
        if (halfEdgeB == null) {
            return resultHalfEdgeCollapseData;
        }

        resultHalfEdgeCollapseData.setHalfEdgeB(halfEdgeB);
        resultHalfEdgeCollapseData.setStartVertexB(halfEdgeB.getStartVertex());

        List<HalfEdge> halfEdgesLoopB = new ArrayList<>();
        halfEdgesLoopB = halfEdgeB.getLoop(halfEdgesLoopB);
        resultHalfEdgeCollapseData.setHalfEdgesLoopB(halfEdgesLoopB);

        List<HalfEdge> halfEdgesBExterior = new ArrayList<>();
        hedgesCount = halfEdgesLoopB.size();
        for (int i = 0; i < hedgesCount; i++) {
            HalfEdge hedgeB = halfEdgesLoopB.get(i);
            if (hedgeB == halfEdgeB) {
                continue;
            }
            if (hedgeB.getStatus() == ObjectStatus.DELETED) {
                int hola = 0;
            }
            HalfEdge twin2 = hedgeB.getTwin();
            if (twin2 != null && twin2.getStatus() != ObjectStatus.DELETED) {
                halfEdgesBExterior.add(twin2);
            }
        }
        resultHalfEdgeCollapseData.setHalfEdgesBExterior(halfEdgesBExterior);
        resultHalfEdgeCollapseData.setFaceB(halfEdgeB.getFace());

        return resultHalfEdgeCollapseData;
    }

    public boolean collapseHalfEdge(HalfEdge halfEdge, int iteration, boolean testDebug) {
        // When collapse a halfEdge, we delete the face, the twin's face, the twin & the startVertex.***
        // When deleting a face, must delete all halfEdges of the face.***
        // must find all halfEdges that startVertex is the deletingVertex, and set as startVertex the endVertex of the deletingHalfEdge.***
        HalfEdgeCollapseData halfEdgeCollapseData = getHalfEdgeCollapsingData(halfEdge, null);
        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
            return false;
        }
        HalfEdgeVertex deletingVertex = halfEdgeCollapseData.getStartVertexA();
        List<HalfEdge> deletingHalfEdgesLoopA = halfEdgeCollapseData.getHalfEdgesLoopA();

        //check();

        if (iteration == 36522) {
            int hola = 0;
        }

        // twin data.***
        HalfEdge twin = halfEdgeCollapseData.getHalfEdgeB();
        List<HalfEdge> deletingTwinHalfEdgesLoopB = halfEdgeCollapseData.getHalfEdgesLoopB();

        List<HalfEdge> outingEdgesOfDeletingVertex = deletingVertex.getOutingHalfEdges(null);

        // check if outingHedge.endVertex == endVertex.***
        int outingEdgesOfDeletingVertexCount = outingEdgesOfDeletingVertex.size();
        for (int i = 0; i < outingEdgesOfDeletingVertexCount; i++) {
            HalfEdge outingEdge = outingEdgesOfDeletingVertex.get(i);
            if (outingEdge != halfEdge) {
                if (outingEdge.getEndVertex() == halfEdge.getEndVertex()) {
                    int hola = 0;
                    return false;
                }
            }
        }

        if (outingEdgesOfDeletingVertex.size() <= 2) {
            int hola = 0;
        }

        // check code.*****************************************************************************************
//        Map<HalfEdgeVertex, List<HalfEdge>> vertexAllOutingEdgesMap = new HashMap<>();
//        vertexAllOutingEdgesMap = getVertexAllOutingEdgesMap(vertexAllOutingEdgesMap);
//        List<HalfEdge> outingEdges = vertexAllOutingEdgesMap.get(deletingVertex);
//
//        if(outingEdges.size() != outingEdgesOfDeletingVertex.size())
//        {
//            int hola = 0;
//        }
        // End check code.--------------------------------------------------------------------------------------

        HalfEdgeVertex endVertex = halfEdge.getEndVertex();
        HalfEdgeFace deletingFace = halfEdge.getFace();
        HalfEdgeFace deletingTwinFace = twin.getFace();

        if (deletingVertex == endVertex) {
            int hola = 0;
        }

        if (endVertex.getStatus() == ObjectStatus.DELETED) {
            int hola = 0;
        }

        //*********************************************************************************
        // 1- Delete the 2 faces, the 2 halfEdges, the 2 halfEdgesLoop, the startVertex.***
        //*********************************************************************************
        // delete the 2 faces.***
        deletingFace.setStatus(ObjectStatus.DELETED);
        deletingTwinFace.setStatus(ObjectStatus.DELETED);

        // Delete the 2 halfEdgesLoop.***
        List<HalfEdge> keepFutureTwineablesHalfEdges = new ArrayList<>(); // keep here the halfEdges that can be twined in the future.***
        List<HalfEdgeVertex> vertexThatMustChangeOutingHalfEdge = new ArrayList<>();
        //this.check();

        // Side A.**************************************************************************
        int counterAux = 0;
        for (HalfEdge deletingHalfEdgeA : deletingHalfEdgesLoopA) {
            deletingHalfEdgeA.setStatus(ObjectStatus.DELETED);
            HalfEdgeVertex startVertex = deletingHalfEdgeA.getStartVertex();
            if (startVertex != null)// && startVertex.getOutingHalfEdge() == deletingHalfEdgeA)
            {
                vertexThatMustChangeOutingHalfEdge.add(startVertex);
                startVertex.note = "mustChange-outingHalfEdge_DIRECT" + counterAux + "_ITER: " + iteration;
                startVertex.setOutingHalfEdge(null);
            }

            deletingHalfEdgeA.note = "deleted-in-collapseHalfEdge_DIRECT" + counterAux + "_ITER: " + iteration;
            deletingHalfEdgeA.breakRelations();
            counterAux++;
        }

        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
        }

        if (endVertex.getStatus() == ObjectStatus.DELETED) {
            int hola = 0;
        }

        // Side B.***************************************************************************
        counterAux = 0;
        for (HalfEdge deletingTwinHalfEdgeB : deletingTwinHalfEdgesLoopB) {
            deletingTwinHalfEdgeB.setStatus(ObjectStatus.DELETED);
            HalfEdgeVertex startVertex = deletingTwinHalfEdgeB.getStartVertex();
            if (startVertex != null)// && startVertex.getOutingHalfEdge() == deletingTwinHalfEdgeB)
            {
                vertexThatMustChangeOutingHalfEdge.add(startVertex);
                startVertex.note = "mustChange-outingHalfEdge_TWIN" + counterAux + "_ITER: " + iteration;
                startVertex.setOutingHalfEdge(null);
            }

            deletingTwinHalfEdgeB.note = "deleted-in-collapseHalfEdge_TWIN" + counterAux + "_ITER: " + iteration;
            deletingTwinHalfEdgeB.breakRelations();
        }

        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
        }


        // delete the startVertex.***
        deletingVertex.setStatus(ObjectStatus.DELETED);
        deletingVertex.deleteObjects();
        deletingVertex.note = "deleted-in-collapseHalfEdge" + "_ITER: " + iteration;

        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
        }

        //**************************************************************************************
        // 2- Set the endVertex to halfEdges that lost the startVertex.***
        //**************************************************************************************

//        Map<HalfEdgeVertex, List<HalfEdge>> vertexAllOutingEdgesMap = new HashMap<>();
//        vertexAllOutingEdgesMap = getVertexAllOutingEdgesMap(vertexAllOutingEdgesMap);
//        List<HalfEdge> outingEdges = vertexAllOutingEdgesMap.get(deletingVertex);


        if (outingEdgesOfDeletingVertex != null) {
            for (HalfEdge outingEdge : outingEdgesOfDeletingVertex) {
                if (outingEdge.getStatus() == ObjectStatus.DELETED) {
                    continue;
                }
                if (endVertex.getStatus() == ObjectStatus.DELETED) {
                    int hola = 0;
                }

                if (outingEdge.isDegenerated()) {
                    int hola = 0;
                }
                HalfEdgeVertex currEndVertex = outingEdge.getEndVertex();
                if (currEndVertex == endVertex) {
                    int hola = 0;
                }
                outingEdge.setStartVertex(endVertex);
                if (outingEdge.isDegenerated()) {
                    int hola = 0;
                }
                outingEdge.note = "Reasigned StartVertex-in-collapseHalfEdge" + "_ITER: " + iteration;
                endVertex.setOutingHalfEdge(outingEdge);
            }
        }

        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
        }

        List<HalfEdge> halfEdgesAExterior = halfEdgeCollapseData.getHalfEdgesAExterior();
        List<HalfEdge> halfEdgesBExterior = halfEdgeCollapseData.getHalfEdgesBExterior();

        int halfEdgesCount = halfEdgesAExterior.size();
        for (int i = 0; i < halfEdgesCount; i++) {
            HalfEdge halfEdgeAExterior = halfEdgesAExterior.get(i);
            HalfEdgeVertex startVertex = halfEdgeAExterior.getStartVertex();
            if (startVertex == null) {
                int hola = 0;
            }
            startVertex.setOutingHalfEdge(halfEdgeAExterior);
        }

        halfEdgesCount = halfEdgesBExterior.size();
        for (int i = 0; i < halfEdgesCount; i++) {
            HalfEdge halfEdgeBExterior = halfEdgesBExterior.get(i);
            HalfEdgeVertex startVertex = halfEdgeBExterior.getStartVertex();
            if (startVertex == null) {
                int hola = 0;
            }
            startVertex.setOutingHalfEdge(halfEdgeBExterior);
        }

        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
        }

        //**************************************************************************************
        // 3- Set twins between the halfEdges stored in keepFutureTwineablesHalfEdges.***
        //**************************************************************************************
        setTwinsBetweenHalfEdges(halfEdgeCollapseData.getHalfEdgesAExterior());
        setTwinsBetweenHalfEdges(halfEdgeCollapseData.getHalfEdgesBExterior());

        if (!halfEdgeCollapseData.check()) {
            int hola = 0;
        }

        //check();

        return true;
    }

    public void collapseHalfEdge_original(HalfEdge halfEdge, boolean testDebug) {
        // When collapse a halfEdge, we delete the face, the twin's face, the twin & the startVertex.***
        // When deleting a face, must delete all halfEdges of the face.***
        // must find all halfEdges that startVertex is the deletingVertex, and set as startVertex the endVertex of the deletingHalfEdge.***
        HalfEdge twin = halfEdge.getTwin();
        HalfEdgeVertex deletingVertex = halfEdge.getStartVertex();

        List<HalfEdge> deletingHalfEdgesLoop = new ArrayList<>();
        deletingHalfEdgesLoop = halfEdge.getLoop(deletingHalfEdgesLoop);

        List<HalfEdge> deletingTwinHalfEdgesLoop = new ArrayList<>();
        deletingTwinHalfEdgesLoop = twin.getLoop(deletingTwinHalfEdgesLoop);

        List<HalfEdge> outingEdgesOfDeletingVertex = deletingVertex.getOutingHalfEdges(null);

        // check code.*****************************************************************************************
//        Map<HalfEdgeVertex, List<HalfEdge>> vertexAllOutingEdgesMap = new HashMap<>();
//        vertexAllOutingEdgesMap = getVertexAllOutingEdgesMap(vertexAllOutingEdgesMap);
//        List<HalfEdge> outingEdges = vertexAllOutingEdgesMap.get(deletingVertex);
//
//        if(outingEdges.size() != outingEdgesOfDeletingVertex.size())
//        {
//            int hola = 0;
//        }
        // End check code.--------------------------------------------------------------------------------------

        HalfEdgeVertex endVertex = halfEdge.getEndVertex();
        HalfEdgeFace deletingFace = halfEdge.getFace();
        HalfEdgeFace deletingTwinFace = twin.getFace();

        //*********************************************************************************
        // 1- Delete the 2 faces, the 2 halfEdges, the 2 halfEdgesLoop, the startVertex.***
        //*********************************************************************************
        // delete the 2 faces.***
        deletingFace.setStatus(ObjectStatus.DELETED);
        deletingTwinFace.setStatus(ObjectStatus.DELETED);

        // Delete the 2 halfEdgesLoop.***
        List<HalfEdge> keepFutureTwineablesHalfEdges = new ArrayList<>(); // keep here the halfEdges that can be twined in the future.***
        List<HalfEdgeVertex> vertexThatMustChangeOutingHalfEdge = new ArrayList<>();
        //this.check();

        // Side A.**************************************************************************
        int counterAux = 0;
        for (HalfEdge deletingHalfEdge : deletingHalfEdgesLoop) {
            deletingHalfEdge.setStatus(ObjectStatus.DELETED);
            HalfEdgeVertex startVertex = deletingHalfEdge.getStartVertex();
            if (startVertex != null && startVertex.getOutingHalfEdge() == deletingHalfEdge) {
                vertexThatMustChangeOutingHalfEdge.add(startVertex);
                startVertex.note = "mustChange-outingHalfEdge_DIRECT";
                //this.check();
                startVertex.changeOutingHalfEdge();
                //this.check();
            }

            deletingHalfEdge.note = "deleted-in-collapseHalfEdge_DIRECT";
            HalfEdge deletingTwin = deletingHalfEdge.getTwin();
            if (deletingTwin != null) {
                keepFutureTwineablesHalfEdges.add(deletingHalfEdge.getTwin());
            }
            //this.check();
            deletingHalfEdge.untwin();
            //this.check();
            //deletingHalfEdge.setStartVertex(null);
            //deletingHalfEdge.setNext(null);
            //this.check();

            counterAux++;
        }

        // Side B.***************************************************************************
        for (HalfEdge deletingTwinHalfEdge : deletingTwinHalfEdgesLoop) {
            deletingTwinHalfEdge.setStatus(ObjectStatus.DELETED);
            HalfEdgeVertex startVertex = deletingTwinHalfEdge.getStartVertex();
            if (startVertex != null && startVertex.getOutingHalfEdge() == deletingTwinHalfEdge) {
                vertexThatMustChangeOutingHalfEdge.add(startVertex);
                startVertex.note = "mustChange-outingHalfEdge_TWIN";
                startVertex.changeOutingHalfEdge();
                //this.check();
            }

            deletingTwinHalfEdge.note = "deleted-in-collapseHalfEdge_TWIN";
            HalfEdge deletingTwinHalfEdgeTwin = deletingTwinHalfEdge.getTwin();
            if (deletingTwinHalfEdgeTwin != null) {
                keepFutureTwineablesHalfEdges.add(deletingTwinHalfEdge.getTwin());
            }
            deletingTwinHalfEdge.untwin();
            //deletingTwinHalfEdge.setStartVertex(null);
            //deletingTwinHalfEdge.setNext(null);
        }

        // delete the 2 halfEdges.***
        halfEdge.untwin();
        halfEdge.setStatus(ObjectStatus.DELETED);
        twin.untwin();
        twin.setStatus(ObjectStatus.DELETED);

        // delete the startVertex.***
        deletingVertex.setStatus(ObjectStatus.DELETED);
        //deletingVertex.deleteObjects();
        deletingVertex.note = "deleted-in-collapseHalfEdge";

        //**************************************************************************************
        // 2- Set the endVertex to halfEdges that lost the startVertex.***
        //**************************************************************************************

        if (outingEdgesOfDeletingVertex != null) {
            for (HalfEdge outingEdge : outingEdgesOfDeletingVertex) {
                if (outingEdge.getStatus() == ObjectStatus.DELETED) {
                    continue;
                }
                outingEdge.setStartVertex(endVertex);
                outingEdge.note = "Reasigned StartVertex-in-collapseHalfEdge";
                endVertex.setOutingHalfEdge(outingEdge);
            }
        }


        //**************************************************************************************
        // 3- Set twins between the halfEdges stored in keepFutureTwineablesHalfEdges.***
        //**************************************************************************************
        setTwinsBetweenHalfEdges(keepFutureTwineablesHalfEdges);
    }

    public void setTwinsBetweenHalfEdges(List<HalfEdge> halfEdges) {
        // This function sets the twins between the halfEdges
        int halfEdgesCount = halfEdges.size();
        for (int i = 0; i < halfEdgesCount; i++) {
            HalfEdge halfEdge = halfEdges.get(i);
            if (halfEdge.getStatus() == ObjectStatus.DELETED || halfEdge.hasTwin()) {
                continue;
            }

            for (int j = i + 1; j < halfEdgesCount; j++) {
                HalfEdge halfEdge2 = halfEdges.get(j);
                if (halfEdge2.getStatus() == ObjectStatus.DELETED || halfEdge2.hasTwin()) {
                    continue;
                }

                if (halfEdge.setTwin(halfEdge2)) {
                    break;
                }
            }
        }
    }
}
