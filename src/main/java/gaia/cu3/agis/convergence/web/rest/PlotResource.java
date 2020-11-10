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
import gaia.cu3.agis.util.RunIterIdentifier;
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
            int currentIter = plotBeanWrapper.getIterationCount();
            plotBeanWrapper.setRetrievalMode(false);
            PlotCategory[] plotCategories = plotBeanWrapper.getConvergencePlotCategories();
            int nrParamSolved = plotBeanWrapper.getNparamSolved();
            long iterId = iterationBeanWrapper.getIterId();
            String iterIdDecoded = RunIterIdentifier.decodeIterId(iterId);
            String runIdDecoded = RunIterIdentifier.decodeRunId(iterationBeanWrapper.getRunId());
            CurrentPlotsUtil currentPlotsUtil = new CurrentPlotsUtil(plotCategories, nrParamSolved, currentIter, runIdDecoded, iterIdDecoded, iterId);
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
    
    private void setUpPlotBean() throws Exception {
        plotBeanWrapper.setRun(AgisUtils.getCurrRunId());
        plotBeanWrapper.setRetrievalMode(false);
    }
}