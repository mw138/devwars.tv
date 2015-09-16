package com.bezman.hibernate.expression;

import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

/**
 * Class to simplify the querying of date : Month
 */
public class MonthCriterion extends SQLCriterion {

    public MonthCriterion(String propertyName, Integer month) {
        super("month(" + propertyName + ") = ?", new Object[]{month}, new Type[]{IntegerType.INSTANCE});
    }

}
