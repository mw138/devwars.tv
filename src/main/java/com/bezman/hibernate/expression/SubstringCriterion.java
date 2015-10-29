package com.bezman.hibernate.expression;

import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

/**
 * Class to simplify the querying of date : Month
 */
public class SubstringCriterion extends SQLCriterion {

    public SubstringCriterion(String propertyName, Integer start, Integer length, String eq) {
        super("SUBSTRING(" + propertyName + ", " + start + ", " + length + ") = ?", new Object[]{eq}, new Type[]{StringType.INSTANCE});
    }

}
