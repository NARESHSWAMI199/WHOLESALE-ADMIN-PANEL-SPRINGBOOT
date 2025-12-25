package com.sales.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GraphDto {
    Integer year;
    List<Integer> months;
}
