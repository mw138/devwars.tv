package com.bezman.hibernate.expression;

import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

/**
 * Created by teren on 8/8/2015.
 */
public class DayCriterion extends SQLCriterion {

    public DayCriterion(String propertyName, Integer day) {
        super("day(" + propertyName + ") = ?", new Object[]{day}, new Type[]{IntegerType.INSTANCE});
    }

}
