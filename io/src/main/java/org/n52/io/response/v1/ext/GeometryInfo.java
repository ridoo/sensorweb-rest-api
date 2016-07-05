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
package org.n52.io.response.v1.ext;

import java.util.HashMap;
import java.util.Map;

import org.n52.io.geojson.FeatureOutputSerializer;
import org.n52.io.geojson.GeoJSONFeature;
import org.n52.io.geojson.GeoJSONObject;
import org.n52.io.response.AbstractOutput;
import org.n52.io.response.ServiceOutput;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO: JavaDoc
 *
 * @author <a href="mailto:h.bredel@52north.org">Henning Bredel</a>
 */
@JsonSerialize(using = FeatureOutputSerializer.class, as = GeoJSONObject.class)
public class GeometryInfo extends AbstractOutput implements CondensedGeometryInfo, GeoJSONFeature {

    private Geometry geometry;

    private PlatformItemOutput platform;

    private final GeometryType geometyCategory;

    public GeometryInfo(GeometryType category) {
        this.geometyCategory = category;
    }

    @Override
    public void setId(String id) {
        super.setId(getUrlIdPrefix() + "/" + id);
    }

    @Override
    @JsonIgnore
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    @JsonIgnore
    public String getDomainId() {
        return super.getDomainId();
    }

    @JsonIgnore
    public ServiceOutput getService() {
        return super.getService();
    }

    @Override
    public PlatformItemOutput getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformItemOutput platform) {
        this.platform = platform;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }


    @JsonIgnore
    @Override
    public String getHrefBase() {
        String base = super.getHrefBase();
        String suffix = getUrlIdPrefix();
        return base != null && base.endsWith(suffix)
                ? base.substring(0, base.lastIndexOf(suffix) - 1)
                : base;
    }

    private String getUrlIdPrefix() {
        return getType().getGeometryType();
    }

    @JsonIgnore
    public GeometryType getType() {
        return geometyCategory;
    }

    public String getGeometyCategory() {
        return geometyCategory.getGeometryType();
    }

    public Map<String, Object> getProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("geometryType", getGeometyCategory());
        properties.put("platform", platform);
        properties.put("href", getHref());
        properties.put("id", getId());
        return properties;
    }

    @Override
    public boolean isSetGeometry() {
        return getGeometry() != null && !getGeometry().isEmpty();
    }

}