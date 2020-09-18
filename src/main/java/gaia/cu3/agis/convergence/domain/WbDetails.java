package gaia.cu3.agis.convergence.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class WbDetails {

    public final String iterIdDecoded;
    public final long totalCount;
    public final Map<String, List<JobStatusDetails>> jobs = new HashMap<>();
    
    public WbDetails(String iterIdDecoded, long totalCount) {
        this.totalCount = totalCount;
        this.iterIdDecoded = iterIdDecoded;
    }
    
    public void addJobStatusDetails(JobStatusDetails jobStatusDetails) {
        if (!jobs.containsKey(jobStatusDetails.status)) {
            jobs.put(jobStatusDetails.status, new ArrayList<>());
        }
        jobs.get(jobStatusDetails.status).add(jobStatusDetails);
    }
    
    public static class JobStatusDetails {

        public final long id;
        public final long runId;
        public final int priority;
        public final String type;
        public final String creationTime;
        public final String status;
        public final String color;
        public final String startTime;
        public final String endTime;
        public final String duration;
        public final String worker;
        public final String message;

        public JobStatusDetails(long id, long runId, int priority, String type, String creationTime, String status, String color, String startTime, String endTime, String duration, String worker, String message) {
            this.id = id;
            this.runId = runId;
            this.priority = priority;
            this.type = type;
            this.creationTime = creationTime;
            this.status = status;
            this.color = color;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
            this.worker = worker;
            this.message = message;
        }
    }
}
