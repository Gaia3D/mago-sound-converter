package com.gaia3d.sound.converter.kml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedData {
    @JacksonXmlProperty(localName = "Data")
    private Data data;
}
