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
package org.n52.io.response.dataset.record;

import java.util.Map;
import java.util.Map.Entry;

import org.n52.io.response.dataset.Data;

public class RecordData extends Data<RecordValue> {

    private static final long serialVersionUID = 4717558247670336015L;

    private RecordDatasetMetadata metadata;

    /**
     * @param values the timestamp &lt;-&gt; value map.
     * @return a measurement data object.
     */
    public static RecordData newTextObservationData(Map<Long, Map<String, Object>> values) {
        RecordData timeseries = new RecordData();
        for (Entry<Long, Map<String, Object>> data : values.entrySet()) {
            timeseries.addNewValue(data.getKey(), data.getValue());
        }
        return timeseries;
    }

    public static RecordData newTextObservationData(RecordValue... values) {
        RecordData timeseries = new RecordData();
        timeseries.addValues(values);
        return timeseries;
    }

    private void addNewValue(Long timestamp, Map<String, Object> value) {
        addNewValue(new RecordValue(timestamp, value));
    }

    @Override
    public RecordDatasetMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(RecordDatasetMetadata metadata) {
        this.metadata = metadata;
    }

}
