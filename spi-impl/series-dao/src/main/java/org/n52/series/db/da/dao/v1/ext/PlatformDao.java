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
package org.n52.series.db.da.dao.v1.ext;

import static org.hibernate.criterion.Restrictions.eq;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.series.db.da.v1.DbQuery;
import org.n52.series.db.da.DataAccessException;
import org.n52.series.db.da.beans.ext.AbstractSeriesEntity;
import org.n52.series.db.da.beans.ext.PlatformEntity;
import org.n52.series.db.da.dao.v1.AbstractDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PlatformDao extends AbstractDao<PlatformEntity> {

    private static final String COLUMN_PKID = "pkid";

    public PlatformDao(Session session) {
        super(session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformEntity> find(DbQuery query) {
        throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public PlatformEntity getInstance(Long key, DbQuery parameters) throws DataAccessException {
        Criteria criteria = getDefaultCriteria()
                .add(eq("pkid", key));
        return (PlatformEntity) criteria.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PlatformEntity> getAllInstances(DbQuery parameters) throws DataAccessException {
        Criteria criteria = session.createCriteria(PlatformEntity.class, "platform");

        DetachedCriteria filter = parameters.createDetachedFilterCriteria("pkid");
        criteria.add(Subqueries.propertyIn("platform.pkid", filter));

        parameters.addPagingTo(criteria);
        return (List<PlatformEntity>) criteria.list();
    }

    @Override
    public int getCount() throws DataAccessException {
        Criteria criteria = getDefaultCriteria()
                .setProjection(Projections.rowCount());
        return criteria != null ? ((Long) criteria.uniqueResult()).intValue() : 0;
    }

    @Override
    protected Criteria getDefaultCriteria() {
        return session.createCriteria(PlatformEntity.class)
                .add(Restrictions.isNotEmpty("series"));
    }

}