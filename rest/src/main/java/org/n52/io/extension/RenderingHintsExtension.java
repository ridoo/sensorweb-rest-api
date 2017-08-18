/*
 * Copyright (C) 2013-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.io.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import org.n52.io.request.IoParameters;
import org.n52.io.request.StyleProperties;
import org.n52.io.response.dataset.DatasetOutput;
import org.n52.io.response.dataset.TimeseriesMetadataOutput;
import org.n52.io.response.extension.MetadataExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderingHintsExtension extends MetadataExtension<DatasetOutput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenderingHintsExtension.class);

    private static final String CONFIG_FILE = "/config-rendering-hints.json";

    private static final String EXTENSION_NAME = "renderingHints";

    private final RenderingHintsExtensionConfig renderingConfig = readConfig();

    private RenderingHintsExtensionConfig readConfig() {
        try (InputStream config = getClass().getResourceAsStream(CONFIG_FILE);) {
            ObjectMapper om = new ObjectMapper();
            return om.readValue(config, RenderingHintsExtensionConfig.class);
        } catch (IOException e) {
            LOGGER.error("Could not load {}. Using empty config.", CONFIG_FILE, e);
            return new RenderingHintsExtensionConfig();
        }
    }

    @Override
    public String getExtensionName() {
        return EXTENSION_NAME;
    }

    @Override
    public void addExtraMetadataFieldNames(DatasetOutput output) {
        if (hasRenderingHints(output)) {
            output.addExtra(EXTENSION_NAME);
        }
    }

    private boolean hasRenderingHints(DatasetOutput output) {
        return hasSeriesParameters(output) && (hasSeriesConfiguration(output) || hasPhenomenonConfiguration(output));
    }

    private boolean hasSeriesParameters(DatasetOutput output) {
        return output.getSeriesParameters() != null;
    }

    private boolean hasSeriesConfiguration(DatasetOutput output) {
        return renderingConfig.getTimeseriesStyles().containsKey(output.getId());
    }

    private boolean hasPhenomenonConfiguration(DatasetOutput output) {
        String id = output.getDatasetParameters().getPhenomenon().getId();
        return renderingConfig.getPhenomenonStyles().containsKey(id);
    }

    @Override
    public Map<String, Object> getExtras(DatasetOutput output, IoParameters parameters) {
        if (!hasExtrasToReturn(output, parameters)) {
            return Collections.emptyMap();
        }

        if (hasSeriesConfiguration(output)) {
            final StyleProperties style = createStyle(getSeriesStyle(output));
            checkForBackwardCompatiblity(output, style);
            return wrapSingleIntoMap(style);
        } else if (hasPhenomenonConfiguration(output)) {
            final StyleProperties style = createStyle(getPhenomenonStyle(output));
            checkForBackwardCompatiblity(output, style);
            return wrapSingleIntoMap(style);
        }

        LOGGER.error("No rendering style found for {} (id={})", output, output.getId());
        return Collections.emptyMap();
    }

    private boolean hasExtrasToReturn(DatasetOutput output, IoParameters parameters) {
        return super.hasExtrasToReturn(output, parameters)
                && hasRenderingHints(output);
    }

    private RenderingHintsExtensionConfig.ConfiguredStyle getSeriesStyle(DatasetOutput output) {
        return renderingConfig.getTimeseriesStyles().get(output.getId());
    }

    private RenderingHintsExtensionConfig.ConfiguredStyle getPhenomenonStyle(DatasetOutput output) {
        String id = output.getDatasetParameters().getPhenomenon().getId();
        return renderingConfig.getPhenomenonStyles().get(id);
    }

    private StyleProperties createStyle(RenderingHintsExtensionConfig.ConfiguredStyle configuredStyle) {
        return configuredStyle.getStyle();
    }

    private void checkForBackwardCompatiblity(DatasetOutput output, StyleProperties style) {
        if (output instanceof TimeseriesMetadataOutput) {
            ((TimeseriesMetadataOutput) output).setRenderingHints(style);
        }

    }

}
