package com.devidend01.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //geter,setter,toString,equal 등의 기능을 자동적으로 추가해준다 단 막 붙이는건 지양해라
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private String ticker;
    private String name;
}
