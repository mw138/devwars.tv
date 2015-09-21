package com.bezman.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShopItem extends BaseModel
{
    private int id;

    private String name, description;

    private Integer price, requiredLevel;
}
