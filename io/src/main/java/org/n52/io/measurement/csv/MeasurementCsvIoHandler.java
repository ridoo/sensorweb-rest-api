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
package org.n52.io.measurement.csv;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTime;
import org.n52.io.CsvIoHandler;
import org.n52.io.IoParseException;
import org.n52.io.IoProcessChain;
import org.n52.io.IoStyleContext;
import org.n52.io.request.RequestSimpleParameterSet;
import org.n52.io.response.dataset.DataCollection;
import org.n52.io.response.dataset.DatasetOutput;
import org.n52.io.response.dataset.count.CountData;
import org.n52.io.response.dataset.count.CountValue;
import org.n52.io.response.dataset.measurement.MeasurementData;
import org.n52.io.response.dataset.measurement.MeasurementValue;
import org.n52.io.response.dataset.text.TextData;
import org.n52.io.response.dataset.text.TextValue;

// TODO extract non measurement specifics to csvhandler

public class MeasurementCsvIoHandler extends CsvIoHandler<MeasurementData> {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    // needed by some clients to detect UTF-8 encoding (e.g. excel)
    private static final String UTF8_BYTE_ORDER_MARK = "\uFEFF";

    private final IoStyleContext context;

    private NumberFormat numberformat = DecimalFormat.getInstance();

    private boolean useByteOrderMark = true;

    private boolean zipOutput = false;

    private String tokenSeparator = ";";

    public MeasurementCsvIoHandler(RequestSimpleParameterSet simpleRequest,
            IoProcessChain<MeasurementData> processChain,
            IoStyleContext context) {
        super(simpleRequest, processChain);
        this.numberformat = DecimalFormat.getInstance(i18n.getLocale());
        this.context = context;
    }

    @Override
    protected String[] getHeader() {
        return new String[] {"station", "phenomenon", "uom", "date", "value"};
    }

    public void setTokenSeparator(String tokenSeparator) {
        this.tokenSeparator = tokenSeparator == null
                ? this.tokenSeparator
                : tokenSeparator;
    }

    public void setIncludeByteOrderMark(boolean byteOrderMark) {
        this.useByteOrderMark = byteOrderMark;
    }

    public void setZipOutput(boolean zipOutput) {
        this.zipOutput = zipOutput;
    }

    @Override
    public void encodeAndWriteTo(DataCollection<MeasurementData> data, OutputStream stream) throws IoParseException {
        try {
            if (zipOutput) {
                writeAsZipStream(data, stream);
            } else {
                writeAsPlainCsv(data, stream);
            }
        } catch (IOException e) {
            throw new IoParseException("Could not write CSV to output stream.", e);
        }
    }

    private void writeAsPlainCsv(DataCollection<MeasurementData> data, OutputStream stream) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(stream);
        writeHeader(bos);
        writeData(data, bos);
        bos.flush();
    }

    private void writeAsZipStream(DataCollection<MeasurementData> data, OutputStream stream) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(stream);
        zipStream.putNextEntry(new ZipEntry("csv-zip-content.csv"));
        writeHeader(zipStream);
        writeData(data, zipStream);
        zipStream.flush();
    }

    private void writeHeader(OutputStream stream) throws IOException {
        String csvLine = csvEncode(getHeader());
        if (useByteOrderMark) {
            csvLine = UTF8_BYTE_ORDER_MARK + csvLine;
        }
        writeCsvLine(csvLine, stream);
    }

    private void writeData(DataCollection<MeasurementData> data, OutputStream stream) throws IOException {
        for (DatasetOutput metadata : context.getSeriesMetadatas()) {
            MeasurementData series = data.getSeries(metadata.getId());
            writeData(metadata, (MeasurementData) series, stream);
        }
    }

    private void writeData(DatasetOutput metadata, MeasurementData series, OutputStream stream) throws IOException {
        String station = metadata.getSeriesParameters().getPlatform().getLabel();
        String phenomenon = metadata.getSeriesParameters().getPhenomenon().getLabel();
        String uom = metadata.getUom();
        for (MeasurementValue timeseriesValue : series.getValues()) {
            String[] values = new String[getHeader().length];
            values[0] = station;
            values[1] = phenomenon;
            values[2] = uom;

            long timestamp = timeseriesValue.getTimestamp();
            values[3] = new DateTime(timestamp).toString();
            values[4] = numberformat.format(timeseriesValue.getValue());
            writeCsvLine(csvEncode(values), stream);
        }
    }

    private void writeData(DatasetOutput metadata, TextData series, OutputStream stream) throws IOException {
        String station = metadata.getSeriesParameters().getPlatform().getLabel();
        String phenomenon = metadata.getSeriesParameters().getPhenomenon().getLabel();
        String uom = metadata.getUom();
        for (TextValue value : series.getValues()) {
            String[] values = new String[getHeader().length];
            values[0] = station;
            values[1] = phenomenon;
            values[2] = uom;

            long timestamp = value.getTimestamp();
            values[3] = new DateTime(timestamp).toString();
            values[4] = value.getValue();
            writeCsvLine(csvEncode(values), stream);
        }
    }

    private void writeData(DatasetOutput metadata, CountData series, OutputStream stream) throws IOException {
        String station = metadata.getSeriesParameters().getPlatform().getLabel();
        String phenomenon = metadata.getSeriesParameters().getPhenomenon().getLabel();
        String uom = metadata.getUom();
        for (CountValue value : series.getValues()) {
            String[] values = new String[getHeader().length];
            values[0] = station;
            values[1] = phenomenon;
            values[2] = uom;

            long timestamp = value.getTimestamp();
            values[3] = new DateTime(timestamp).toString();
            values[4] = Integer.toString(value.getValue());
            writeCsvLine(csvEncode(values), stream);
        }
    }

    private void writeCsvLine(String line, OutputStream stream) throws IOException {
        stream.write(line.getBytes(UTF8));
    }

    private String csvEncode(String[] values) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value);
            sb.append(tokenSeparator);
        }
        sb.deleteCharAt(sb.lastIndexOf(tokenSeparator));
        return sb.append("\n").toString();
    }

}