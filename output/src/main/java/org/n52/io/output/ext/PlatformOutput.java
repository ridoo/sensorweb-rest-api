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
package org.n52.io.output.ext;

import org.n52.io.input.PlatformType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;
import java.util.Collection;
import java.util.List;

import org.n52.io.output.AbstractOutput;
import org.n52.io.output.OutputCollection;
import org.n52.io.output.FeatureOutput;
import org.n52.io.output.PhenomenonOutput;
import org.n52.io.output.ProcedureOutput;

/**
 * TODO: JavaDoc
 *
 * @author <a href="mailto:h.bredel@52north.org">Henning Bredel</a>
 * @since 2.0.0
 */
public class PlatformOutput extends AbstractOutput {

    private final PlatformType platformType;

    private Collection<DatasetOutput> series;

    private Geometry geometry;

    private PhenomenonOutputCollection phenomena;

    private ProcedureOutputCollection procedures;

    private FeatureOutputCollection features;

    public PlatformOutput(PlatformType platformType) {
        this.platformType = platformType;
    }

    @Override
    public String getHrefBase() {
        String base = super.getHrefBase();
        String suffix = getType().getPlatformType();
        return base != null && base.endsWith(suffix)
                ? base.substring(0, base.lastIndexOf(suffix) - 1)
                : base;
    }

    public String getPlatformType() {
        return getType().getPlatformType();
    }

    @JsonIgnore
    public PlatformType getType() {
        return platformType != null
                ? platformType
                // stay backward compatible
                : PlatformType.STATIONARY_INSITU;
    }

    @Override
    public void setId(String id) {
        super.setId(getType().createId(id));
    }

    public Collection<DatasetOutput> getSeries() {
        return series;
    }

    public void setSeries(List<DatasetOutput> series) {
        this.series = series;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Collection<PhenomenonOutput> getPhenomena() {
        return getNullSafeItems(phenomena);
    }

    public void setPhenomena(PhenomenonOutputCollection phenomena) {
        this.phenomena = phenomena;
    }

    public Collection<ProcedureOutput> getProcedures() {
        return getNullSafeItems(procedures);
    }

    public void setProcedures(ProcedureOutputCollection procedures) {
        this.procedures = procedures;
    }

    public Collection<FeatureOutput> getFeatures() {
        return getNullSafeItems(features);
    }

    public void setFeatures(FeatureOutputCollection features) {
        this.features = features;
    }

    private <T> Collection<T> getNullSafeItems(OutputCollection<T> collection) {
        return collection != null
                ? collection.getItems()
                : null;
    }

}