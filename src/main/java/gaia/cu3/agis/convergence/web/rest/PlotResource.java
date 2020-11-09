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

import gaia.cu3.agis.convergence.domain.BinHistogramInfo;
import gaia.cu3.agis.convergence.domain.CurrentPlotsUtil;
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
    public ResponseEntity<CurrentPlotsUtil> getCurrentPlotsUtil() {
        try {
            plotBeanWrapper.setRun(AgisUtils.getCurrRunId());
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

    @GetMapping("/binHistogramInfo")
    public ResponseEntity<List<BinHistogramInfo>> getBinHistogramInfo(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration) {
        try {
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

    @GetMapping("/plot")
    public ResponseEntity<InputStreamResource> plot(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached,
            @RequestParam(value = "export") short export
    ) {
        try {
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

    @GetMapping("/plotMap")
    public ResponseEntity<InputStreamResource> plotMap(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "cached") boolean cached,
            @RequestParam(value = "export") short export,
            @RequestParam(value = "min", defaultValue = "0", required = false) short min,
            @RequestParam(value = "max", defaultValue = "0", required = false) short max
            ) {
        try {
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
    public ResponseEntity<InputStreamResource> magBinnedHistogram(
            @RequestParam(value = "plotCategory") String plotCategoryString,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width,
            @RequestParam(value = "bin") short bin,
            @RequestParam(value = "cached") boolean cached
    ) {
        try {
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

    @GetMapping("/getGlobalGroupNames")
    public ResponseEntity<List<String>> getGlobalGroupNames() {
        try {
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

    @GetMapping("/plotMultiGlobals")
    public ResponseEntity<InputStreamResource> plotMultiGlobals(
            @RequestParam(value = "globalGroupIndex") short globalGroupIndex,
            @RequestParam(value = "set") short set,
            @RequestParam(value = "iteration") short iteration,
            @RequestParam(value = "width") int width
    ) {
        try {
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
    public ResponseEntity<byte[]> getImagesZipped() {
        try {
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
}