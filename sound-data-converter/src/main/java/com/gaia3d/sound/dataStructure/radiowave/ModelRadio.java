package com.gaia3d.sound.dataStructure.radiowave;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 라디오 신호 간섭
 */
@Builder
@Getter
@Setter
public class ModelRadio {
    /*
        N (int, 4byte)	개수
        index (int, 4byte)	index
        x (float, 4byte)	x좌표
        y (float, 4byte)	y좌표
        RH (float, 4byte)	라디오 안테나 높이
        value (float, 4byte)	청명시 라디오 수신장해 예측결과
        SNR (float, 4byte)	청명시 라디오 전계장도 대장 해비 예측결과
        rank (int, 4byte)	청명시 라디오 수신 품질등급 평가결과
        value_r (float, 4byte)	강우시 라디오 수신장해 예측결과
        SNR_r (float, 4byte)	강우시 라디오 전계강도 대장 해비 예측결과
        rank_r (int, 4byte)	강우시 라디오 수신 품질등급 평가결과
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
    private float valueInRain;
    private float snrInRain;
    private int rankInRain;
    private float nearestDistance;
    private int in;

    public static ModelRadio of(String[] split) {
        return ModelRadio.builder()
                .index(Integer.parseInt(split[0]))
                .x(Float.parseFloat(split[1]))
                .y(Float.parseFloat(split[2]))
                .relativeHeight(Float.parseFloat(split[3]))
                .value(Float.parseFloat(split[4]))
                .snr(Float.parseFloat(split[5]))
                .rank(Integer.parseInt(split[6]))
                .valueInRain(Float.parseFloat(split[7]))
                .snrInRain(Float.parseFloat(split[8]))
                .rankInRain(Integer.parseInt(split[9]))
                .nearestDistance(Float.parseFloat(split[10]))
                .in(Integer.parseInt(split[11]))
                .build();
    }
}
