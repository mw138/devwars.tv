package com.bezman.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Access extends BaseModel {
    private int id;

    private User user;

    private String route;
}
