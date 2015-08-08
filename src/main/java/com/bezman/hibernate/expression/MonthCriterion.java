package com.bezman.hibernate.expression;

import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

/**
 * Created by teren on 8/8/2015.
 */
public class MonthCriterion extends SQLCriterion {

    public MonthCriterion(String propertyName, Integer month) {
        super("month(" + propertyName + ") = ?", new Object[]{month}, new Type[]{IntegerType.INSTANCE});
    }

}
