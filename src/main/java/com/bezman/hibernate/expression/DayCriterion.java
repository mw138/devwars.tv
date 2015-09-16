package com.bezman.hibernate.expression;

import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

/**
 * Class to simplify the querying of date : Day
 */
public class DayCriterion extends SQLCriterion {

    public DayCriterion(String propertyName, Integer day) {
        super("day(" + propertyName + ") = ?", new Object[]{day}, new Type[]{IntegerType.INSTANCE});
    }

}
