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
import gaia.cu1.tools.satellite.calibration.astro.AstroCalDataServer;
import gaia.cu1.tools.satellite.definitions.FprsDirection;
import gaia.cu1.tools.util.props.PropertyLoader;
import gaia.cu3.agis.algo.gis.convergence.source.MagnitudeBinHistoImpl;
import gaia.cu3.agis.convergence.domain.BinHistogramInfo;
import gaia.cu3.agis.convergence.domain.CalibrationEffectInfo;
import gaia.cu3.agis.convergence.domain.NrEffectsUsed;
import gaia.cu3.agis.convergence.domain.PlotSummaryInfo;
import gaia.cu3.agis.plotting.PlotCategory;
import gaia.cu3.agis.web.PlotBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
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

    public byte[] plotCalibration(PlotCategory plotCategory, int effectId, int functionId, short iteration, int width, boolean cached, short export, boolean isUpdate) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024*1024);

        plotBean.setIsCalUpdate(isUpdate);
        plotBean.setIdEffect(effectId);
        plotBean.setIdFunction(functionId);
        plotBean.setWithtitle(true);
        plotBean.setCat(plotCategory.getCode());
        plotBean.setIter(iteration);
        plotBean.setSet((short)plotCategory.ordinal());
        plotBean.setCached(cached);
        plotBean.setWidth(width);
        plotBean.setExport(export);

        plotBean.plot(outputStream, iteration, (short) plotCategory.ordinal());
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

    public List<String> getGlobalGroupNames() throws Exception {
        return Arrays.asList(plotBean.getGlobalGroupNames());
    }

    public NrEffectsUsed getUsedEffectsTotal() throws ClassNotFoundException, RemoteException, GaiaException {
        return new NrEffectsUsed(plotBean.getUsedEffectsTotal(FprsDirection.AC), plotBean.getUsedEffectsTotal(FprsDirection.AL));
    }

    public byte[] plotMultiGlobals(short globalGroupIndex, short set, short iteration, int width) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024*1024);

        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        
        plotBean.plotMultiGlobals(outputStream, globalGroupIndex, iteration, set);

        outputStream.flush();
        return outputStream.toByteArray();
        
    }
    
    public byte[] imagesToZip() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.imagesToZip(outputStream);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public List<CalibrationEffectInfo> getCalibrationEffects(FprsDirection fprsDirection) throws GaiaException {
        AstroCalDataServer calServer = plotBean.getAstroCalServer();
        return Arrays.stream(calServer.getAstroCalibration(fprsDirection).getCalibrationEffects())
                .map(CalibrationEffectInfo::new)
                .collect(Collectors.toList());
    }

    public void getAstroCalServer() throws GaiaException {
        plotBean.getAstroCalServer();
    }
    
    public PlotSummaryInfo getPlotSummaryInfo() throws Exception {
        int nrSummaryAstro = plotBean.getNoSummaryPlots();
        int nrSummaryAtt = plotBean.getNoAttSummaryPlots();
        int nrSummaryCal = plotBean.getNoCalSummaryPlots();
        int nrSummaryRot = PropertyLoader.getPropertyAsBoolean("gaia.cu3.agis.mgr.rotateEveryRun") ? plotBean.getNoRotSummaryPlots() : 0;
        int nrSummaryCg = plotBean.getNoCGSummaryPlots();
        int nrSummaryCorr = plotBean.getNoCorrSummaryPlots();
        int nrSummaryAux = plotBean.getNoAuxSummaryPlots();
        int nrSummaryRes = plotBean.getNoResSummaryPlots();
        return new PlotSummaryInfo(nrSummaryAstro, nrSummaryAtt, nrSummaryCal, nrSummaryRot, nrSummaryCg, nrSummaryCorr, nrSummaryAux, nrSummaryRes);
    }

    public byte[] plotAllInOneConvSummaryPng(int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotAllInOneConvSummary();
    }

    public byte[] getAllInOneConvSummaryCsv() throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotAllInOneConvSummary();
    }

    private byte[] plotAllInOneConvSummary() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotAllInOneConvSummary(outputStream);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotSummaryPng(int summary, int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotSummary(summary);
    }

    public byte[] getSummaryCsv(int summary) throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotSummary(summary);
    }

    private byte[] plotSummary(int summary) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotSummary(outputStream, summary);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotRotSummaryPng(int rot, int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotRotSummary(rot);
    }

    public byte[] getRotSummaryCsv(int rot) throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotRotSummary(rot);
    }

    private byte[] plotRotSummary(int rot) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotFrameRotationSummary(outputStream, rot);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotCGSummaryPng(int cg, int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotCGSummary(cg);
    }

    public byte[] getCGSummaryCsv(int cg) throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotCGSummary(cg);
    }

    private byte[] plotCGSummary(int cg) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotCGSummary(outputStream, cg);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotCorrSummaryPng(int corr, int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotCorrSummary(corr);
    }

    public byte[] getCorrSummaryCsv(int corr) throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotCorrSummary(corr);
    }

    private byte[] plotCorrSummary(int corr) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotCorrelationSummary(outputStream, corr);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotAuxSummaryPng(int aux, int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotAuxSummary(aux);
    }

    public byte[] getAuxSummaryCsv(int aux) throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotAuxSummary(aux);
    }

    private byte[] plotAuxSummary(int aux) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotAuxSummary(outputStream, aux);
        outputStream.flush();
        return outputStream.toByteArray();
    }

    public byte[] plotResSummaryPng(int width, boolean cached) throws Exception {
        plotBean.setExport(0);
        plotBean.setWithtitle(true);
        plotBean.setWidth(width);
        plotBean.setCached(cached);
        return plotResSummary();
    }

    public byte[] getResSummaryCsv() throws Exception {
        plotBean.setExport(1);
        plotBean.setCached(false);
        return plotResSummary();
    }

    private byte[] plotResSummary() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096*4096);
        plotBean.plotResSummary(outputStream);
        outputStream.flush();
        return outputStream.toByteArray();
    }
}
