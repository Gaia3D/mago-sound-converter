package com.gaia3d.sound.dataStructure.radiowave;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * TV 신호 간섭
 */
@Builder
@Getter
@Setter
public class ModelTV {
    /*
        N (int, 4byte)	개수
        index (int, 4byte)	index
        x (float, 4byte)	x좌표
        y (float, 4byte)	y좌표
        RH (float, 4byte)	TV 안테나 높이
        value (float, 4byte)	TV 수신장해 예측결과
        SNR (float, 4byte)	TV 전계강도대장해비 예측결과
        rank (int, 4byte)	TV 수신 품질등급 평가결과
        dist_min (float, 4byte)	최근접 송진선로와의 이격거리
        in (int, 4byte)	지형모델링 영역 내 존재여부 (여 : 1, 부 : 0)
     */

    private int index;
    private float x;
    private float y;
    private float relativeHeight;
    private float value;
    private float snr;
    private int rank;
    private float nearestDistance;
    private int in;

    public static ModelTV of(String[] split) {
        return ModelTV.builder()
                .index(Integer.parseInt(split[0]))
                .x(Float.parseFloat(split[1]))
                .y(Float.parseFloat(split[2]))
                .relativeHeight(Float.parseFloat(split[3]))
                .value(Float.parseFloat(split[4]))
                .snr(Float.parseFloat(split[5]))
                .rank(Integer.parseInt(split[6]))
                .nearestDistance(Float.parseFloat(split[7]))
                .in(Integer.parseInt(split[8]))
                .build();
    }
}
