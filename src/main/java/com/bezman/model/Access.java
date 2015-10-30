package com.bezman.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Terence on 3/23/2015.
 */
@Data
@NoArgsConstructor
public class Access extends BaseModel {
    private int id;

    private User user;

    private String route;
}
