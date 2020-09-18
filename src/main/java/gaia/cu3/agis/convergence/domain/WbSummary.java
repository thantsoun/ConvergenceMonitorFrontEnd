package gaia.cu3.agis.convergence.domain;

import gaia.cu1.tools.dal.table.GaiaTable;
import gaia.cu1.tools.exception.GaiaDataAccessException;

import java.util.ArrayList;
import java.util.List;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class WbSummary {

    public static final String[] STATUS_TEXT = {"Submitted", "Published", "Preloading", "Assigned", "Running", "Paused", "Killed", "Succeeded", "Aborted", "Failed"};
    public static final String[] STATUS_COLOUR = {"FFFF00", "C0C0C0", "800080", "0000FF", "CD8500", "00FFFF", "808080", "008000", "A9A9A9", "FF0000"};
    
    public final String timestamp;
    public final String iterIdDecoded;
    public final long totalCount;
    public final List<JobStatusSummary> jobs = new ArrayList<>();
    
    public WbSummary(String timestamp, String iterIdDecoded, long totalCount) {
        this.timestamp = timestamp;
        this.totalCount = totalCount;
        this.iterIdDecoded = iterIdDecoded;
    }
    
    public void addJobStatusSummary(GaiaTable gaiaTable) throws GaiaDataAccessException {
        while (gaiaTable.next()) {
            int ordinal = gaiaTable.getInt(0);
            long count = gaiaTable.getLong(1);
            jobs.add(new JobStatusSummary(STATUS_TEXT[ordinal], STATUS_COLOUR[ordinal], count, (double) count / (double) totalCount));
        }
    }
    
    public static class JobStatusSummary {
        
        public final String status;
        public final String color;
        public final long count;
        public final double percentage;

        public JobStatusSummary(String status, String color, long count, double percentage) {
            this.status = status;
            this.color = color;
            this.count = count;
            this.percentage = percentage;
        }
    }
}
