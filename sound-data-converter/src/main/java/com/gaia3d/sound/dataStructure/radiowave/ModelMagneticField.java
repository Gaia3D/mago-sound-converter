package com.gaia3d.sound.dataStructure.radiowave;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 자기 해석
 * 매그내릭~
 */
@Builder
@Getter
@Setter
public class ModelMagneticField {
    /*
        N (int, 4byte)	개수
        index (int, 4byte)	index
        x (float, 4byte)	x좌표
        y (float, 4byte)	y좌표
        RH (float, 4byte)	지상고
        dist2d (float, 4byte)	주거지와의 수평거리
        H_src (float, 4byte)	전선의 지상고
        Value[uT] (float, 4byte)	자계 예측 결과
        ok (int, 4byte)	판정결과(기준 만족 : 1, 기준 불만족 : 0)
        ref (float, 4byte)	자계 평가 기준
        in (int, 4byte)	지형모델링 영역 내 존재여부 (여 : 1, 부 : 0)
    */

    private int index;
    private int x;
    private int y;
    private float relativeHeight;
    private float distanceToDwelling ;
    private float heightOfWire;
    private float value;
    private int result;
    private float ref;
    private int in;

    public static ModelMagneticField of(String[] split) {
        return ModelMagneticField.builder()
                .index(Integer.parseInt(split[0]))
                .x(Integer.parseInt(split[1]))
                .y(Integer.parseInt(split[2]))
                .relativeHeight(Float.parseFloat(split[3]))
                .distanceToDwelling(Float.parseFloat(split[4]))
                .heightOfWire(Float.parseFloat(split[5]))
                .value(Float.parseFloat(split[6]))
                .result(Integer.parseInt(split[7]))
                .ref(Float.parseFloat(split[8]))
                .in(Integer.parseInt(split[9]))
                .build();
    }
}
