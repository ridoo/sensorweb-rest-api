/*
 * Copyright (C) 2013-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package org.n52.series.db.dao;

import static org.hibernate.criterion.DetachedCriteria.forClass;
import static org.hibernate.criterion.Projections.projectionList;
import static org.hibernate.criterion.Projections.property;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.io.request.IoParameters;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO: JavaDoc
 *
 * @author <a href="mailto:h.bredel@52north.org">Henning Bredel</a>
 * @param <T> the data entity type
 */
@Transactional
@SuppressWarnings("rawtypes") // infer entitType runtime
public class DataDao<T extends DataEntity> extends AbstractDao<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDao.class);

    private static final String COLUMN_SERIES_PKID = "seriesPkid";

    private static final String COLUMN_DELETED = "deleted";

    private static final String COLUMN_TIMESTAMP = "timestamp";

    private final Class<T> entityType;

    public DataDao(Session session, Class<T> clazz) {
        super(session);
        this.entityType = clazz;
    }

    public DataDao(Session session) {
        super(session);
        this.entityType = (Class<T>) DataEntity.class;
    }

    @Override
    public List<T> find(DbQuery query) {
        LOGGER.debug("find instances: {}", query);
        return Collections.emptyList();
    }

    @Override
    public T getInstance(Long key, DbQuery parameters) throws DataAccessException {
        LOGGER.debug("get instance '{}': {}", key, parameters);
        return entityType.cast(session.get(entityType, key));
    }

    /**
     * <p>
     * Retrieves all available observation instances.</p>
     *
     * @param parameters query parameters.
     * @return all instances matching the given query parameters.
     * @throws DataAccessException if accessing database fails.
     */
    @Override
    @SuppressWarnings("unchecked") // cast from hibernate
    public List<T> getAllInstances(DbQuery parameters) throws DataAccessException {
        LOGGER.debug("get all instances: {}", parameters);
        Criteria criteria = getDefaultCriteria();
        parameters.addTimespanTo(criteria);
        return (List<T>) criteria.list();
    }

    /**
     * Retrieves all available observations belonging to a particular series.
     *
     * @param series the entity to get all observations for.
     * @return all observation entities belonging to the series.
     * @throws org.n52.series.db.DataAccessException if accessing database
     * fails.
     */
    public List<T> getAllInstancesFor(DatasetEntity series) throws DataAccessException {
        LOGGER.debug("get all instances for series '{}'", series.getPkid());
        return getAllInstancesFor(series, DbQuery.createFrom(IoParameters.createDefaults()));
    }

    /**
     * Retrieves all available observation instances belonging to a particular
     * series.
     *
     * @param series the series the observations belongs to.
     * @param parameters some query parameters to restrict result.
     * @return all observation entities belonging to the given series which
     * match the given query.
     * @throws DataAccessException if accessing database fails.
     */
    @SuppressWarnings("unchecked") // cast from hibernate
    public List<T> getAllInstancesFor(DatasetEntity series, DbQuery parameters) throws DataAccessException {
        LOGGER.debug("get all instances for series '{}': {}", series.getPkid(), parameters);
        Criteria criteria = getDefaultCriteria()
                .add(eq(COLUMN_SERIES_PKID, series.getPkid()));
        parameters.addTimespanTo(criteria);
        return (List<T>) criteria.list();
    }

    @Override
    protected String getSeriesProperty() {
        return ""; // there's no series property for observation
    }

    @Override
    protected Criteria getDefaultCriteria() {
        return session.createCriteria(entityType)
                .addOrder(Order.asc(COLUMN_TIMESTAMP))
                .add(eq(COLUMN_DELETED, Boolean.FALSE));
    }

    @Override
    protected Class<T> getEntityClass() {
        return entityType;
    }

    @SuppressWarnings("unchecked")
    public T getDataValueAt(Date timestamp, DatasetEntity series) {
        LOGGER.debug("get instances @{} for '{}'", timestamp, series.getPkid());
        Criteria criteria = getDefaultCriteria()
                .add(Restrictions.eq(COLUMN_SERIES_PKID, series.getPkid()))
                .add(Restrictions.eq(COLUMN_TIMESTAMP, timestamp));

        DetachedCriteria filter = forClass(DatasetEntity.class)
                .setProjection(projectionList().add(property("pkid")));
        criteria.add(Subqueries.propertyIn(COLUMN_SERIES_PKID, filter));
        return (T) criteria.uniqueResult();
    }

}
