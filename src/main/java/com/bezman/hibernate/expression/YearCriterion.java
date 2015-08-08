package com.bezman.hibernate.expression;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

/**
 * Created by teren on 8/8/2015.
 */
public class YearCriterion extends SQLCriterion {

    public YearCriterion(String propertyName, Integer year) {
        super("year(" + propertyName + ") = ?", new Object[]{year}, new Type[]{IntegerType.INSTANCE});
    }

}
