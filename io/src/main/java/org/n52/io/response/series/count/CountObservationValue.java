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
package org.n52.io.response.series.count;

import java.io.Serializable;

import org.n52.io.response.series.SeriesData;
import org.n52.io.response.series.SeriesDataMetadata;

public class CountObservationValue extends SeriesData implements Comparable<CountObservationValue>, Serializable {

    private static final long serialVersionUID = 635165564503748527L;

    private Long timestamp;

    private Integer value;

    public CountObservationValue() {
        // for serialization
    }

    public CountObservationValue(long timestamp, Integer value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getValue() {
        return value == null
                ? Integer.MIN_VALUE
                : value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TextValue [ ");
        sb.append("timestamp: ").append(timestamp).append(", ");
        sb.append("value: ").append(value);
        return sb.append(" ]").toString();
    }

    @Override
    public int compareTo(CountObservationValue o) {
        return getTimestamp().compareTo(o.getTimestamp());
    }

    @Override
    public boolean hasReferenceValues() {
        return false;
    }

    @Override
    public SeriesDataMetadata getMetadata() {
        return null;
    }

}