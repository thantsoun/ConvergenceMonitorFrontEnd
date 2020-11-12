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
package gaia.cu3.agis.convergence.web.rest;

import gaia.cu1.tools.satellite.definitions.FprsDirection;
import gaia.cu3.agis.convergence.domain.*;
import gaia.cu3.agis.convergence.service.IterationBeanWrapper;
import gaia.cu3.agis.convergence.service.PlotBeanWrapper;
import gaia.cu3.agis.convergence.web.rest.util.DetailedHeaderUtil;
import gaia.cu3.agis.plotting.PlotCategory;
import gaia.cu3.agis.util.AgisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
@RestController
@RequestMapping("/api")
public class PlotResource {

    private final Logger log = LoggerFactory.getLogger(PlotResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlotBeanWrapper plotBeanWrapper;
    private final IterationBeanWrapper iterationBeanWrapper;

    public PlotResource(PlotBeanWrapper plotBeanWrapper, IterationBeanWrapper iterationBeanWrapper) {
        log.info("Initializing {}", PlotResource.class);
        this.plotBeanWrapper = plotBeanWrapper;
        this.iterationBeanWrapper = iterationBeanWrapper;
    }

    @GetMapping("/currentPlotsUtil")
    public synchronized ResponseEntity<CurrentPlotsUtil> getCurrentPlotsUtil() {
        try {
            setUpPlotBean();
            CurrentPlotsUtil currentPlotsUtil = plotBeanWrapper.getCurrentPlotsUtil(plotBeanWrapper.getConvergencePlotCategories());
            return ResponseEntity.ok(currentPlotsUtil);
        } catch (Exception ex) {
            log.error("Error getting the Current Plots Util Object", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Current Plots Util Object", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotSummaryInfo")
    public synchronized ResponseEntity<PlotSummaryInfo> getPlotSummaryInfo() {
        try {
            setUpPlotBean();
            return ResponseEntity.ok(plotBeanWrapper.getPlotSummaryInfo());
        } catch (Exception ex) {
            log.error("Error getting the Current Plots Util Object", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Current Plots Util Object", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/binHistogramInfo")
    public synchronized ResponseEntity<List<BinHistogramInfo>> getBinHistogramInfo(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration) {
        try {
            setUpPlotBean();
            PlotCategory plotCategory = PlotCategory.valueOf(plotCategoryString);
            int parentOrdinal = plotCategory.getParent().ordinal();
            boolean isUpdate = !plotCategory.getCode().endsWith(".true");
            List<BinHistogramInfo> binHistogramInfos = plotBeanWrapper.getMagnitudeBinnedHisto(iteration, parentOrdinal, isUpdate);
            return ResponseEntity.ok(binHistogramInfos);
        } catch (Exception ex) {
            log.error("Error getting the BinHistogram Information for iteration {} and Plot Category {}", iteration, plotCategoryString, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the BinHistogram Information for iteration " + iteration +
                            " and Plot Category " + plotCategoryString, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getGlobalGroupNames")
    public synchronized ResponseEntity<List<String>> getGlobalGroupNames() {
        try {
            setUpPlotBean();
            return ResponseEntity.ok()
                    .body(plotBeanWrapper.getGlobalGroupNames());
        } catch (Exception ex) {
            log.error("Error getting the global group names", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the global group names", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/nrEffectsUsed")
    public synchronized ResponseEntity<NrEffectsUsed> getNrEffectsUsed() {
        try {
            setUpPlotBean();
            plotBeanWrapper.getAstroCalServer();
            return ResponseEntity.ok()
                    .body(plotBeanWrapper.getUsedEffectsTotal());
        } catch (Exception ex) {
            log.error("Error getting the total number of effects", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the total number of effects", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/calibrationPlotsInfo")
    public synchronized ResponseEntity<List<CalibrationEffectInfo>> getCalibrationPlotsInfo(@RequestParam(value = "plotCategory") String plotCategoryString) {
        try {
            setUpPlotBean();
            plotBeanWrapper.getAstroCalServer();
            PlotCategory plotCategory = PlotCategory.valueOf(plotCategoryString);
            FprsDirection scan = plotCategory.equals(PlotCategory.GEN_CALIBRATION_AC) ? FprsDirection.AC : FprsDirection.AL;
            return ResponseEntity.ok()
                    .body(plotBeanWrapper.getCalibrationEffects(scan));
        } catch (Exception ex) {
            log.error("Error getting the calibration effects information", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the calibration effects information", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plot")
    public synchronized ResponseEntity<InputStreamResource> plot(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached,
            @RequestParam(value = "export") short export
    ) {
        try {
            setUpPlotBean();
            PlotCategory plotCategory = PlotCategory.valueOf(plotCategoryString);
            byte[] imgBytes = plotBeanWrapper.plot(plotCategory, iteration, width, cached, export);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imgBytes));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the plot", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the plot", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotCalibration")
    public synchronized ResponseEntity<InputStreamResource> plotCalibration(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "effectId") int effectId,
            @RequestParam(value = "functionId") int functionId,
            @RequestParam(value = "isUpdate") boolean isUpdate,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached,
            @RequestParam(value = "export") short export
    ) {
        try {
            setUpPlotBean();
            plotBeanWrapper.getAstroCalServer();
            PlotCategory plotCategory = PlotCategory.valueOf(plotCategoryString);
            byte[] imgBytes = plotBeanWrapper.plotCalibration(plotCategory, effectId, functionId, iteration, width, cached, export, isUpdate);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imgBytes));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the calibration plot", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the calibration plot", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotMap")
    public synchronized ResponseEntity<InputStreamResource> plotMap(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached,
            @RequestParam(value = "export") short export,
            @RequestParam(value = "min", defaultValue = "0", required = false) short min,
            @RequestParam(value = "max", defaultValue = "0", required = false) short max
            ) {
        try {
            setUpPlotBean();
            PlotCategory plotCategory = PlotCategory.valueOf(plotCategoryString);
            byte[] imgBytes = plotBeanWrapper.plotMap(plotCategory, iteration, width, cached, export, min, max);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imgBytes));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the map plot", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the map plot", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/magBinnedHistogram")
    public synchronized ResponseEntity<InputStreamResource> magBinnedHistogram(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "bin") short bin,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            PlotCategory plotCategory = PlotCategory.valueOf(plotCategoryString);
            boolean isUpdate = !plotCategory.getCode().endsWith(".true");
            byte[] imgBytes = plotBeanWrapper.magBinnedHistogram(plotCategory.getParent(), iteration, width, cached, bin, isUpdate);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imgBytes));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the mag binned histogram", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the mag binned histogram", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotMultiGlobals")
    public synchronized ResponseEntity<InputStreamResource> plotMultiGlobals(
            @RequestParam(value = "globalGroupIndex") short globalGroupIndex,
            @RequestParam(value = "set") short set,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width
    ) {
        try {
            setUpPlotBean();
            byte[] imgBytes = plotBeanWrapper.plotMultiGlobals(globalGroupIndex, set, iteration, width);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(imgBytes));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the multi globals", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the multi globals", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getImagesZipped")
    public synchronized ResponseEntity<byte[]> getImagesZipped() {
        try {
            setUpPlotBean();
            byte[] imgBytes = plotBeanWrapper.imagesToZip();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(imgBytes.length);
            String filename = "AGIS_plots_" + iterationBeanWrapper.getIterId() + ".zip";
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            return ResponseEntity.ok().headers(headers).body(imgBytes);
        } catch (Exception ex) {
            log.error("Error getting the multi globals", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the multi globals", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getAllSummaryCsv")
    public synchronized ResponseEntity<byte[]> getAllSummaryCsv(
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getAllInOneConvSummaryCsv();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting all astrometric summary csv file for all", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting all astrometric summary csv file for all", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotAllSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotAllSummaryPng(
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotAllInOneConvSummaryPng(width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting all astrometric summary png file for all", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting all astrometric summary png file for all", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getSummaryCsv")
    public synchronized ResponseEntity<byte[]> getSummaryCsv(
            @RequestParam(value = "summary") int summary,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getSummaryCsv(summary);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting all astrometric summary csv file for {}", summary, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting all astrometric summary csv file for " + summary, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotSummaryPng(
            @RequestParam(value = "summary") int summary,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotSummaryPng(summary, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting all astrometric summary png file for {}", summary, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting all astrometric summary png file for " + summary, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getRotSummaryCsv")
    public synchronized ResponseEntity<byte[]> getRotSummaryCsv(
            @RequestParam(value = "rot") int rot,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getRotSummaryCsv(rot);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the frame rotation summary csv file for {}", rot, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the frame rotation csv file for " + rot, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotRotSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotRotSummaryPng(
            @RequestParam(value = "rot") int rot,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotRotSummaryPng(rot, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the frame rotation summary png file for {}", rot, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the frame rotation summary png file for " + rot, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getCGSummaryCsv")
    public synchronized ResponseEntity<byte[]> getCGSummaryCsv(
            @RequestParam(value = "cg") int cg,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getCGSummaryCsv(cg);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the CG summary csv file for {}", cg, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the CG summary csv file for " + cg, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotCGSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotCGSummaryPng(
            @RequestParam(value = "cg") int cg,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotCGSummaryPng(cg, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the CG summary png file for {}", cg, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the CG summary png file for " + cg, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getCorrSummaryCsv")
    public synchronized ResponseEntity<byte[]> getCorrSummaryCsv(
            @RequestParam(value = "corr") int corr,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getCorrSummaryCsv(corr);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the Correlation summary csv file for {}", corr, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Correlation summary csv file for " + corr, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotCorrSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotCorrSummaryPng(
            @RequestParam(value = "corr") int corr,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotCorrSummaryPng(corr, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the Correlation summary png file for {}", corr, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the corr summary png file for " + corr, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getAuxSummaryCsv")
    public synchronized ResponseEntity<byte[]> getAuxSummaryCsv(
            @RequestParam(value = "aux") int aux,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getAuxSummaryCsv(aux);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the Auxiliary summary csv file for {}", aux, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Auxiliary summary csv file for " + aux, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotAuxSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotAuxSummaryPng(
            @RequestParam(value = "aux") int aux,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotAuxSummaryPng(aux, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the Auxiliary summary png file for {}", aux, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the auxiliary summary png file for " + aux, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getResSummaryCsv")
    public synchronized ResponseEntity<byte[]> getResSummaryCsv(
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getResSummaryCsv();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the residuals summary csv file", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the residuals summary csv file", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotResSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotResSummaryPng(
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotResSummaryPng(width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the Residuals summary png file", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Residuals summary png file", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/getCalSummaryCsv")
    public synchronized ResponseEntity<byte[]> getCalSummaryCsv(
            @RequestParam(value = "cal") int cal,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getCalSummaryCsv(cal);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the Calibration summary csv file for {}", cal, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Calibration summary csv file for " + cal, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotCalSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotCalSummaryPng(
            @RequestParam(value = "cal") int cal,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotCalSummaryPng(cal, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the calibration summary png file for {}", cal, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the calibration summary png file for " + cal, ex.getMessage()))
                    .build();
        }
    }


    @GetMapping("/getAttSummaryCsv")
    public synchronized ResponseEntity<byte[]> getAttSummaryCsv(
            @RequestParam(value = "att") int att,
            @RequestParam(value = "filename") String filename
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.getAttSummaryCsv(att);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(content.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
            return ResponseEntity.ok().headers(headers).body(content);
        } catch (Exception ex) {
            log.error("Error getting the attitude summary csv file for {}", att, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the attitude summary csv file for " + att, ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/plotAttSummaryPng")
    public synchronized ResponseEntity<InputStreamResource> plotAttSummaryPng(
            @RequestParam(value = "att") int att,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotAttSummaryPng(att, width, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the attitude summary png file for {}", att, ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the attitude summary png file for " + att, ex.getMessage()))
                    .build();
        }
    }
    
    @GetMapping("/plotSrcDist")
    public synchronized ResponseEntity<InputStreamResource> plotSrcDist(
            @RequestParam(value = "width") int width,
            @RequestParam(value = "srcDist") short srcDist,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "isUpdate") boolean isUpdate,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
            setUpPlotBean();
            byte[] content = plotBeanWrapper.plotSourceDistribution(srcDist, iteration, width, isUpdate, cached);
            InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(content));
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Cache-Control", "no-cache")
                    .body(inputStreamResource);
        } catch (Exception ex) {
            log.error("Error getting the source distribution plot", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the source distribution plot", ex.getMessage()))
                    .build();
        }
    }
    
    private void setUpPlotBean() throws Exception {
        plotBeanWrapper.setRun(AgisUtils.getCurrRunId());
        plotBeanWrapper.setRetrievalMode(false);
    }
}