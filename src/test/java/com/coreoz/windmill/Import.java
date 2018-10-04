package com.coreoz.windmill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Import {
    private String a;
    private String b;
    private String c;
    private Integer integerNumber;
    private Double doubleNumber;
}
