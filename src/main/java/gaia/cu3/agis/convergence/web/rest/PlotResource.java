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

import gaia.cu1.tools.dal.DatabaseStore;
import gaia.cu1.tools.dal.table.GaiaTable;
import gaia.cu3.agis.convergence.domain.AllRuns;
import gaia.cu3.agis.convergence.service.IterationBeanWrapper;
import gaia.cu3.agis.convergence.service.PlotBeanWrapper;
import gaia.cu3.agis.convergence.web.rest.util.DetailedHeaderUtil;
import gaia.cu3.agis.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;

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
    
    private String formatGaiaTableValue(GaiaTable tb, int index) {
        StringWriter writer = new StringWriter();
        iterationBeanWrapper.format(writer, tb, index);
        return writer.toString();
    }

    @GetMapping("/allRuns")
    public ResponseEntity<AllRuns> getAllRuns() {
        try {
            AllRuns allRuns = new AllRuns();
            DatabaseStore st = DbUtils.getNamedStore(DbUtils.convStore);
            GaiaTable tb = st.executeQueryGT(allRuns.query, null);
            while (tb.next()) {
                AllRuns.Run run = new AllRuns.Run(String.valueOf(tb.getLong("RunId")), String.format("%02d", tb.getInt("numIterations")), tb, 
                        (table, index) -> formatGaiaTableValue(table, index));
                allRuns.addRun(run);
            }
            return ResponseEntity
                    .ok(allRuns);
        } catch (Exception ex) {
            log.error("Error getting all the runs", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting all the runs", ex.getMessage()))
                    .build();
        }
    }
}