package com.gaia3d.soundDataConverter;

import com.gaia3d.dataStructure.DataType_Plan;
import com.gaia3d.dataStructure.RectangleFace;
import com.gaia3d.dataStructure.Vertex;
import com.gaia3d.utils.StringModifier;
import com.gaia3d.utils.io.LittleEndianDataInputStream;
import com.gaia3d.utils.io.LittleEndianDataOutputStream;
import org.locationtech.proj4j.CoordinateReferenceSystem;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SoundDataConverter
{
    public CoordinateReferenceSystem inputCrs = null;
    public SoundDataConverter()
    {
        System.out.println("SoundDataConverter constructor");
    }

    public void convertDataInFolder(String inputFolderPath, String outputFolderPath)
    {
        System.out.println("SoundDataConverter convert");
        // 1rst, find all files *.RBin in the input folder.***
        ArrayList<String> vecFileExtensions = new ArrayList<>();
        vecFileExtensions.add("RBin");
        ArrayList<String> vecFileNames = new ArrayList<>();
        StringModifier.getFileNamesInFolder(inputFolderPath, vecFileExtensions, vecFileNames);

        int filesCount = vecFileNames.size();
        for (int i = 0; i < filesCount; i++)
        {
            String fileName = vecFileNames.get(i);
            //String rawFileName = StringModifier.getRawFileName(fileName);
            System.out.println("fileName = " + fileName);


            String inputFilePath = inputFolderPath + "/" + fileName;
            //String outputFilePath = outputFolderPath + "/" + rawFileName + ".json";
            try
            {
                convertData(inputFilePath, outputFolderPath);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void convertData(String inputFilePath, String outputFolderPath) throws FileNotFoundException
    {
        System.out.println("SoundDataConverter convert");
        // the input file is binary.***
        // the output file is json.***
        Path inputPath = Paths.get(inputFilePath);
        File input = inputPath.toFile();

        try (LittleEndianDataInputStream stream = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(input))))
        {
//            1. 기본 자료형 정의
//            유형 설명 길이(Byte수)
//            int 정수형 4
//            double 실수형 8
//            string 문자열 UTF-8 적용 (예, 이름) default : 128
//            objNLv * double or double array 소음레벨 8 or 72
//                    * objNLv 은 소음레벨 유형에 따라 소음레벨 배열 개수 차등 지정
//                    - 소음레벨 유형 0 : Overall 인 경우, 소음레벨 1개 (8 Byte)
//                    - 소음레벨 유형 1 : 1/1 옥타브밴드인 경우, 소음레벨 배열 9개로 구성됨. (72 Byte)
//            Overall(1개) + 63 ~ 8k Hz(8개)
//* Index는 integer (0 ~ )
//            2. 기본 파일 저장 형식
//            순서 자료 자료형 변수명
//            1 문자열 Byte 수 (default = 128 ) int num_bytes_string
//            2 저장할 자료 종류 개수 int Ntype
//            Ntype 만큼 이하 반복
//            3 자료 유형 ( → 3. 저장할 자료 유형 참조 ) int Itype
//            4
//            Itype 자료 유형에 따른 File 양식 분기
//→ 상세내역은 4. 종류별 형식 참조
//                    - 2 -
//                    3. 저장할 자료 유형(Itype)
////운영시
//            Type_2_Res_Plan_Day = 21001 //주간 Plan 소음 해석결과
//            Type_2_Res_Plan_Night = 21002 //야간 Plan 소음 해석결과
//            Type_2_Res_Facade_Day = 21003 //주간 Facade 소음 해석결과
//            Type_2_Res_Facade_Night = 21004 //야간 Facade 소음 해석결과
//            //도로/철도 소음
//            Type_2_Res_Stable_Facility_Ext_RD_Day = 22001 //외부 정온시설 주간 도로 소음 해석결과
//            Type_2_Res_Stable_Facility_Ext_RD_Night = 22002 //외부 정온시설 야간 도로 소음 해석결과
//            Type_2_Res_Stable_Facility_Ext_TN_Day = 22003 //외부 정온시설 주간 철도 소음 해석결과
//            Type_2_Res_Stable_Facility_Ext_TN_Night = 22004 //외부 정온시설 야간 철도 소음 해석결과
//            Type_2_Res_Stable_Facility_Int_RD_Day = 22005 //내부 정온시설 주간 도로 소음 해석결과
//            Type_2_Res_Stable_Facility_Int_RD_Night = 22006 //내부 정온시설 야간 도로 소음 해석결과
//            Type_2_Res_Stable_Facility_Int_TN_Day = 22007 //내부 정온시설 주간 철도 소음 해석결과
//            Type_2_Res_Stable_Facility_Int_TN_Night = 22008 //내부 정온시설 야간 철도 소음 해석결과
////철도 진동
//            Type_2_Res_Stable_Facility_Ext_TNV_Day = 23001 //외부 정온시설 주간 철도 진동 해석결과
//            Type_2_Res_Stable_Facility_Ext_TNV_Night = 23002 //외부 정온시설 야간 철도 진동 해석결과
//            Type_2_Res_Stable_Facility_Int_TNV_Day = 23003 //내부 정온시설 주간 철도 진동 해석결과
//            Type_2_Res_Stable_Facility_Int_TNV_Night = 23004 //내부 정온시설 야간 철도 진동 해석결과
////모델
//            Type_2_Source_RD = 29001 //도로
//            Type_2_Source_TN = 29002 //철도
//            Type_2_Object_Building = 29003 //건물
//            Type_2_Object_Topography = 29004 //지형
//            Type_2_Object_Barrier = 29005 //방음벽
//            Type_2_AProperty_Abr = 29006 //흡음률
//            Type_2_AProperty_TL = 29007 //투과손실률
//                    - 3 -
//                    4. 종류별 형식
//            4.1 운영시
//            4.1.1 운영시 Plan 소음 해석결과 (objRPlan)
//                    순서 자료 자료형 변수명
//            1 소음레벨 유형 ( 0 : Overall, 1 : 1/1 옥타브밴드 ) int objNLv_Type
//            2 Node 개수 int num_Node
//            num_Node 만큼 3 ~ 7 반복
//            3 Node 인덱스 int Index
//            4 Node x 좌표 double x
//            5 Node y 좌표 double y
//            6 지면으로부터 Node까지 높이 double z
//            7 소음레벨 배열 [dB(A)]
//            double 배열
//            (1개 or 9개) objNLv
//            8 사각형 개수 int num_Rect
//            num_Rect 만큼 9 ~ 12 반복
//            9 Node 1 인덱스 int node1
//            10 Node 2 인덱스 int node2
//            11 Node 3 인덱스 int node3
//            12 Node 4 인덱스 int node4
//※ 적용 대상
//            Ÿ Type_2_Res_Plan_Day //주간 Plan 소음 해석결과
//            Ÿ Type_2_Res_Plan_Night //야간 Plan 소음 해석결과
//            - 4 -
//                    4.1.2 운영시 Facade 소음 해석결과
//            순서 자료 자료형 변수명
//            1 소음레벨 유형 ( 0 : Overall, 1 : 1/1 옥타브밴드 ) int objNLv_Type
//            1 건물 개수 int num_Building
//            num_Building 만큼 이하 반복
//            2 건물 인덱스 int Index
//            3 objRPlan 사용 ( → 4.1.1 참조)
//※ 적용 대상
//            Ÿ Type_2_Res_Facade_Day //주간 Facade 소음 해석결과
//            Ÿ Type_2_Res_Facade_Night //야간 Facade 소음 해석결과
//            - 5 -

            int num_bytes_string = stream.readInt();
            int Ntype = stream.readInt();
            for (int i = 0; i < Ntype; i++)
            {
                int Itype = stream.readInt();
                switch (Itype)
                {
                    case 21001:
                    case 21002:
                    case 21004:
                    {
                        DataType_Plan resultDataTypePlan = new DataType_Plan();
                        parseCase_4_1_1(stream, resultDataTypePlan);
                        resultDataTypePlan.convertData(inputCrs);
                        break;
                    }
                    case 21003:
                    {
                        parseCase_4_1_2(stream);
                        break;
                    }
                    case 22001:
                    case 22002:
                    case 22003:
                    case 22004:
                    case 22005:
                    case 22006:
                    {
                        parseCase_4_1_3(stream);
                        break;
                    }
                    case 22007:
                    case 22008:
                    case 23001:
                    case 23002:
                    case 23003:
                    case 23004:
                    case 29001:
                    case 29002:
                    case 29003:
                    case 29004:
                    case 29005:
                    case 29006:
                    case 29007:
                        break;
                    default:
                        throw new RuntimeException("Itype = " + Itype);
                }
            }

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

    }

    private void parseCase_4_1_1(LittleEndianDataInputStream stream, DataType_Plan resultDataTypePlan) throws IOException
    {
        resultDataTypePlan.objNLv_Type = stream.readInt();
        resultDataTypePlan.num_Node = stream.readInt();
        for (int j = 0; j < resultDataTypePlan.num_Node; j++)
        {
            Vertex vertex = new Vertex();
            vertex.index = stream.readInt();
            vertex.x = stream.readDouble();
            vertex.y = stream.readDouble();
            vertex.z = stream.readDouble();

            vertex.objNLv = new double[1];
            for(int k=0; k<1; k++)
            {
                vertex.objNLv[k] = stream.readDouble();
            }

            resultDataTypePlan.vertexList.add(vertex);
        }

        resultDataTypePlan.num_Rect = stream.readInt();
        for (int j = 0; j < resultDataTypePlan.num_Rect; j++)
        {
            RectangleFace face = new RectangleFace();
            face.index1 = stream.readInt();
            face.index2 = stream.readInt();
            face.index3 = stream.readInt();
            face.index4 = stream.readInt();

            resultDataTypePlan.faceList.add(face);
        }
    }

    private void parseCase_4_1_2(LittleEndianDataInputStream stream) throws IOException
    {
        int num_building = stream.readInt();
        for (int i = 0; i < num_building; i++) {
            int Index = stream.readInt();
            //parseCase_4_1_1(stream);
            int hola = 0;
        }
    }


    private void parseCase_4_1_3(LittleEndianDataInputStream stream) throws IOException
    {
        int objNLv_Type = stream.readInt();
        int num_building = stream.readInt();
        for (int i = 0; i < num_building; i++)
        {
            int Index = stream.readInt();
            //parseCase21001(stream);


            int num_floor = stream.readInt();
            System.out.println("num_floor = " + num_floor);

            double height = stream.readDouble();

            //System.out.println("height = " + height);
            for (int j = 0; j < num_floor; j++)
            {
                double[] objNLv = new double[1];
                for(int l=0; l<1; l++)
                {
                    objNLv[l] = stream.readDouble();
                    System.out.println("objNLv = " + objNLv[l]);
                    int hola = 0;
                }
            }
        }
//        int objNLv_Type = stream.readInt();
//        int num_Node = stream.readInt();
//        for (int j = 0; j < num_Node; j++)
//        {
//            int Index = stream.readInt();
//            double x = stream.readDouble();
//            double y = stream.readDouble();
//            double z = stream.readDouble();
//            for(int k=0; k<9; k++)
//            {
//                double objNLv = stream.readDouble();
//            }
//        }
//        int num_Rect = stream.readInt();
//        for (int j = 0; j < num_Rect; j++)
//        {
//            int node1 = stream.readInt();
//            int node2 = stream.readInt();
//            int node3 = stream.readInt();
//            int node4 = stream.readInt();
//        }
    }
}
