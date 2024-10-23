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
public class ModelMagneticFieldContour {
    /*
        Nx (int, 4byte)	x방향 개수
        Ny (int, 4byte)	y방향 개수
        index (int, 4byte)	index
        x (float, 4byte)	x좌표
        y (float, 4byte)	y좌표
        RH (float, 4byte)	지상고
        uT	자계 예측 결과
    */

    private int index;
    private int x;
    private int y;
    private float relativeHeight;
    private float value;

    public static ModelMagneticFieldContour of(String[] split) {
        return ModelMagneticFieldContour.builder()
                .index(Integer.parseInt(split[0]))
                .x(Integer.parseInt(split[1]))
                .y(Integer.parseInt(split[2]))
                .relativeHeight(Float.parseFloat(split[3]))
                .value(Float.parseFloat(split[4]))
                .build();
    }
}
