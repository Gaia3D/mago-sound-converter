package com.gaia3d.sound.dataStructure.radiowave;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class Model {
    private int length;
    List<?> values;
}
