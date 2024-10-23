package com.gaia3d.sound.dataStructure.radiowave;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterferenceType {
    RADIO("라디오 신호 간섭", 12),
    TV("TV 신호 간섭", 9),
    MAGNETIC_FIELD("자기 해석", 10),
    MAGNETIC_FIELD_CONTOUR("자기 해석 컨투어", 5);

    private final String description;
    private final int columnCount;
}
