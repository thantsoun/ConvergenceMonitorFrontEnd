package gaia.cu3.agis.convergence.domain;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class JobSummary {
    
    public final long runId;
    public final long iterId;
    public final String iterIdDecoded;
    public final String summaryString;

    public JobSummary(long runId, long iterId, String iterIdDecoded, String summaryString) {
        this.iterIdDecoded = iterIdDecoded;
        this.runId = runId;
        this.iterId = iterId;
        this.summaryString = summaryString;
    }
}
