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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.I18nFeatureEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FeatureDao extends AbstractDao<FeatureEntity> {

    private static final String SERIES_FILTER_PROPERTY = "feature";

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureDao.class);

    public FeatureDao(Session session) {
        super(session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FeatureEntity> find(DbQuery query) {
        LOGGER.debug("find instance: {}", query);
        Criteria criteria = translate(I18nFeatureEntity.class, getDefaultCriteria(), query)
                .add(Restrictions.ilike("name", "%" + query.getSearchTerm() + "%"));
        return addFilters(criteria, query).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<FeatureEntity> getAllInstances(DbQuery query) throws DataAccessException {
        LOGGER.debug("get all instances: {}", query);
        Criteria criteria = translate(I18nFeatureEntity.class, getDefaultCriteria(), query);
        return (List<FeatureEntity>) addFilters(criteria, query).list();
    }

    @Override
    protected String getSeriesProperty() {
        return SERIES_FILTER_PROPERTY;
    }

    @Override
    protected Class<FeatureEntity> getEntityClass() {
        return FeatureEntity.class;
    }

}
