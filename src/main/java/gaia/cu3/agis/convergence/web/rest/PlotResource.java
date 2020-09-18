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

import gaia.cu1.gaiatools.wb.dm.SimpleJob;
import gaia.cu1.tools.dal.DatabaseStore;
import gaia.cu1.tools.dal.ObjectFactory;
import gaia.cu1.tools.dal.table.GaiaTable;
import gaia.cu1.tools.exception.GaiaDataAccessException;
import gaia.cu1.tools.exception.GaiaException;
import gaia.cu1.tools.util.GaiaFactory;
import gaia.cu1.tools.util.WhiteboardUtil;
import gaia.cu3.agis.convergence.domain.AllRuns;
import gaia.cu3.agis.convergence.domain.JobSummary;
import gaia.cu3.agis.convergence.domain.WbDetails;
import gaia.cu3.agis.convergence.domain.WbSummary;
import gaia.cu3.agis.convergence.service.IterationBeanWrapper;
import gaia.cu3.agis.convergence.service.PlotBeanWrapper;
import gaia.cu3.agis.convergence.web.rest.util.DetailedHeaderUtil;
import gaia.cu3.agis.infra.AgisWhiteboard;
import gaia.cu3.agis.util.AgisUtils;
import gaia.cu3.agis.util.DbUtils;
import gaia.cu3.agis.util.RunIterIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            GaiaTable tb = st.executeQueryGT(allRuns.query);
            while (tb.next()) {
                AllRuns.Run run = new AllRuns.Run(String.valueOf(tb.getLong("RunId")), String.format("%02d", tb.getInt("numIterations")), tb,
                        this::formatGaiaTableValue);
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

    @GetMapping("/properties")
    public ResponseEntity<Map<String, String>> getProperties() {
        try {
            Map<String, String> propsMap = new TreeMap<>();
            Properties props = plotBeanWrapper.getPastRunPropsObject();
            Set<Map.Entry<Object, Object>> entries = props.entrySet();
            String cu3AgisPrefix = "gaia.cu3.agis";
            String cu1ToolsPrefix = "gaia.cu1.tools";
            String cu1MdbPrefix = "gaia.cu1.mdb.cu1";
            String javaPrefix = "java";
            propsMap.put("user.name", System.getProperty("user.name"));
            for (Map.Entry<Object,Object> entry : entries) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (key.startsWith(cu3AgisPrefix) || key.startsWith(cu1ToolsPrefix) || key.startsWith(cu1MdbPrefix) || key.startsWith(javaPrefix)) {
                    propsMap.put(key, key.toLowerCase().contains("password") ? "########" : value);
                }
            }
            return ResponseEntity
                    .ok(propsMap);
        } catch (Exception ex) {
            log.error("Error getting the properties", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the properties", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/jobSummary")
    public ResponseEntity<JobSummary> getJobSummary() {
        try {
            //WB specific query
            AgisWhiteboard wb = (AgisWhiteboard) GaiaFactory.getWhiteboard();
            long currentIterId = AgisUtils.getCurrIterId();
            long currentRunId = AgisUtils.getCurrRunId();
            String iterIdDecoded = RunIterIdentifier.decodeIterId(currentIterId);
            String summary = wb.getSummary();
            return ResponseEntity.ok(new JobSummary(currentRunId, currentIterId, iterIdDecoded, summary));
        } catch (Exception ex) {
            log.error("Error getting the current job summary", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the Job Summary", ex.getMessage()))
                    .build();
        }
    }

    @GetMapping("/wbSummary")
    public ResponseEntity<WbSummary> getWbSummary() {

        DatabaseStore databaseStore = null;
        GaiaTable gaiaTable = null;

        try {
            String nowString = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);

            long totalJobs = 0;
            long currentIterId = AgisUtils.getCurrIterId();
            String iterIdDecoded = RunIterIdentifier.decodeIterId(currentIterId);

            databaseStore = DbUtils.getNamedStore(DbUtils.convStore);
            //WB specific query
            String table = databaseStore.getTableName(SimpleJob.class);
            String totalJobsQuery = "select count(*) from " + table + "  where solutionId=?";
            String jobsPerStatusQuery = "select status, count(*) from " + table + " where solutionId=? group by status";

            Object[] params = { currentIterId };
            // get total nr of jobs
            gaiaTable = databaseStore.executeQueryGT(totalJobsQuery, params);
            while (gaiaTable.next()) {
                totalJobs = gaiaTable.getLong(0);
            }
            gaiaTable.close();
            //create the wb summary obj
            WbSummary wbSummary = new WbSummary(nowString, iterIdDecoded, totalJobs);
            //get jobs per status
            gaiaTable = databaseStore.executeQueryGT(jobsPerStatusQuery, params);
            wbSummary.addJobStatusSummary(gaiaTable);
            gaiaTable.close();

            return ResponseEntity.ok(wbSummary);
        } catch (Exception ex) {
            log.error("Error getting the WhiteBoard Jobs Summary", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the WhiteBoard Jobs Summary", ex.getMessage()))
                    .build();
        } finally {
            closeTable(gaiaTable);
            closeStore(databaseStore);
        }
    }

    @GetMapping("/wbDetails")
    public ResponseEntity<WbDetails> getWbDetails() {

        DatabaseStore databaseStore = null;
        GaiaTable gaiaTable = null;

        try {
            long totalJobs = 0;
            long currentIterId = AgisUtils.getCurrIterId();
            String iterIdDecoded = RunIterIdentifier.decodeIterId(currentIterId);
            Object[] params = { currentIterId };
            
            databaseStore = DbUtils.getNamedStore(DbUtils.convStore);
            String tableName = databaseStore.getTableName(SimpleJob.class);

            String totalJobsQuery = "select count(*) from " + tableName + "  where solutionid=?";
            gaiaTable = databaseStore.executeQueryGT(totalJobsQuery, params);
            while (gaiaTable.next()) {
                totalJobs = gaiaTable.getLong(0);
            }
            gaiaTable.close();
            WbDetails wbDetails = new WbDetails(iterIdDecoded, totalJobs);
            
            for (int i = 0; i < WbSummary.STATUS_TEXT.length; i++) {
                getJobDetailsPerStatus(databaseStore, i, wbDetails, tableName, params);
            }
            return ResponseEntity.ok(wbDetails);
        } catch (Exception ex) {
            log.error("Error getting the WhiteBoard Jobs Details", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(DetailedHeaderUtil.createDetailedError(applicationName, "Error getting the WhiteBoard Jobs Details", ex.getMessage()))
                    .build();
        } finally {
            closeTable(gaiaTable);
            closeStore(databaseStore);
        }
    }

    private void getJobDetailsPerStatus(DatabaseStore store, int statusOrdinal, WbDetails wbDetails, String tableName, Object[] params) throws GaiaException {
        GaiaTable table = null;
        try {
            String jobsDetailsPerStatusQuery = "select * from " + tableName + "  where solutionid=? and status=" + statusOrdinal + " order by solutionid, starttime desc";
            ObjectFactory<SimpleJob> objFac = new ObjectFactory<>(SimpleJob.class);
            List<SimpleJob> jobList = new ArrayList<>();
            table = store.executeQueryGT(jobsDetailsPerStatusQuery, params);//params
            while (table.next()) {
                SimpleJob job = objFac.getObject(table);
                jobList.add(job);
            }
            WhiteboardUtil whiteboardUtil = new WhiteboardUtil();
            if (!jobList.isEmpty()) {
                for (SimpleJob simpleJob : jobList) {

                    createJobDetail(wbDetails, whiteboardUtil, simpleJob);
                }
            }
            table.close();
        } finally {
            closeTable(table);
        }
    }

    private void createJobDetail(WbDetails wbDetails, WhiteboardUtil whiteboardUtil, SimpleJob simpleJob) {
        long id = simpleJob.getId();
        String message = simpleJob.getMessage();
        String status = whiteboardUtil.getStatusText(simpleJob.getStatus());
        String color = whiteboardUtil.getStatusColour(simpleJob.getStatus());
        long runId = simpleJob.getSolutionId();
        int priority = simpleJob.getPriority();
        String type = simpleJob.getTaskType();
        String workerRaw = simpleJob.getWorker();
        String worker = workerRaw;
        if (workerRaw != null && !workerRaw.equals("")) {
            String[] split = workerRaw.split("_");
            if (split.length == 3) {
                worker = "server=" + split[0] + ", node=" + split[1] + ", nodeId=" + split[2];
            }
        }

        long startTime = simpleJob.getStartTime();
        long endTime = simpleJob.getEndTime();
        long creationTime = simpleJob.getCreationTime();
        String startTimeString = startTime > 0 ? LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME) : "";
        String endTimeString = endTime > 0 ? LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME) : "";
        String creationTimeString = creationTime > 0 ? LocalDateTime.ofInstant(Instant.ofEpochMilli(creationTime), ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME) : "";
        String duration = (startTime > 0 && endTime > 0) ? TimeUnit.MILLISECONDS.toMinutes(endTime - startTime) + "m" : "";

        wbDetails.addJobStatusDetails(new WbDetails.JobStatusDetails(
                id,
                runId,
                priority,
                type,
                creationTimeString,
                status,
                color, 
                startTimeString,
                endTimeString,
                duration,
                worker,
                message
        ));
    }

    private void closeTable(GaiaTable table) {
        if (table != null) {
            try {
                table.close();
            } catch (GaiaDataAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeStore(DatabaseStore store) {
        if (store != null) {
            try {
                DbUtils.releaseNamedStore(DbUtils.convStore, store);
            } catch (GaiaDataAccessException e) {
                e.printStackTrace();
            }
        }
    }
}