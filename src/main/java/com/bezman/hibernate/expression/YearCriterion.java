package com.bezman.hibernate.expression;

import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

/**
 * Class to simplify the querying of date : Year
 */
public class YearCriterion extends SQLCriterion {

    public YearCriterion(String propertyName, Integer year) {
        super("year(" + propertyName + ") = ?", new Object[]{year}, new Type[]{IntegerType.INSTANCE});
    }

}
