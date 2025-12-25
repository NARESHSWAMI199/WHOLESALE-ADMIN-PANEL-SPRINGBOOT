package com.sales.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPermissionsDto {
    Integer userId;
    List<Integer> groupList;
}
