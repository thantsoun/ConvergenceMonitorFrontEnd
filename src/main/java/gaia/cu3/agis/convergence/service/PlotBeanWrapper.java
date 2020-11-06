/*
 * Astrometric Global Iterative Solution (AGIS)
 * Copyright (C) 2006-2011 Gaia Data Processing and Analysis Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package gaia.cu3.agis.convergence.service;

import gaia.cu1.tools.exception.GaiaException;
import gaia.cu3.agis.algo.gis.convergence.source.MagnitudeBinHistoImpl;
import gaia.cu3.agis.convergence.domain.BinHistogramInfo;
import gaia.cu3.agis.plotting.PlotCategory;
import gaia.cu3.agis.web.PlotBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
@Service
public class PlotBeanWrapper {

    private final Logger log = LoggerFactory.getLogger(PlotBeanWrapper.class);
    
    private final PlotBean plotBean;

    public PlotBeanWrapper() {
        log.info("Initializing {}", PlotBeanWrapper.class);
        this.plotBean = new PlotBean();
    }

    public Properties getPastRunPropsObject() throws GaiaException {
        return plotBean.getPastRunPropsObject();
    }

    public int getIterationCount() throws Exception {
        return plotBean.getIterationCount();
    }

    public void setRetrievalMode(boolean retrievalMode) {
        plotBean.setRetrievalMode(retrievalMode);
    }

    public PlotCategory[] getConvergencePlotCategories() throws GaiaException {
        return plotBean.getConvergencePlotCategories();
    }
    
    public int getNparamSolved() throws GaiaException {
        return plotBean.getNparamSolved();
    }

    public void setRun(long currRunId) throws Exception {
        plotBean.setRun(currRunId);
    }
    
    public List<BinHistogramInfo> getMagnitudeBinnedHisto(short iter, int parentOrdinal, boolean isUpdate) throws Exception {
        List<MagnitudeBinHistoImpl> binHistogramList = plotBean.getMagnitudeBinnedHisto(iter, parentOrdinal, isUpdate);
        return binHistogramList.stream()
                .map(histogram -> BinHistogramInfo.getBinHistogramInfo(histogram, isUpdate))
                .collect(Collectors.toList());
    }

    public byte[] plot(PlotCategory plotCategory, short iteration, int width, boolean cached, short export) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024*1024);

        plotBean.setWithtitle(true);
        plotBean.setSet((short)plotCategory.ordinal());
        plotBean.setCached(cached);
        plotBean.setWidth(width);
        plotBean.setIter(iteration);
        plotBean.setExport(export);
        
        if(!plotCategory.getCode().contains("correlation")) {
            plotBean.plot(outputStream, iteration, (short) plotCategory.ordinal());
        } else {
            plotBean.plotCorrelation(outputStream, iteration, (short) plotCategory.ordinal());
        }
        
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotMap(PlotCategory plotCategory, short iteration, int width, boolean cached, short export, double min, double max) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024*1024);

        boolean colorFilter1 = min != 0d;
        boolean colorFilter2 = max != 0d;

        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        plotBean.setColorFilter(colorFilter1 && colorFilter2);
        plotBean.setMinRangeMap(min);
        plotBean.setMaxRangeMap(max);
        plotBean.setCat(plotCategory.getParent().getCode());
        plotBean.setExport(export);

        try {
            plotBean.plotHpixMap(outputStream, iteration, (short) plotCategory.ordinal());
        } catch (Exception e) {
            log.error("Error getting the Hpix Plot Map", e);
            plotBean.streamErrorImage(outputStream, e.getMessage());
        }

        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] magBinnedHistogram(PlotCategory plotCategory, short iteration, int width, boolean cached, short bin, boolean isUpdate) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024*1024);

        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        plotBean.setCat(plotCategory.getParent().getCode());

        plotBean.plotMagBinned(outputStream, iteration, (short) plotCategory.ordinal(), bin, isUpdate);
        
        outputStream.flush();
        return outputStream.toByteArray();
    }
}
