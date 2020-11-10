package gaia.cu3.agis.convergence.domain;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class PlotSummaryInfo {
    
    public final int nrSummaryAstro;
    public final int nrSummaryAtt;
    public final int nrSummaryCal;
    public final int nrSummaryRot;
    public final int nrSummaryCg;
    public final int nrSummaryCorr;
    public final int nrSummaryAux;
    public final int nrSummaryRes;
    
    public PlotSummaryInfo(int nrSummaryAstro, int nrSummaryAtt, int nrSummaryCal, int nrSummaryRot, int nrSummaryCg, int nrSummaryCorr, int nrSummaryAux, int nrSummaryRes) {
        this.nrSummaryAstro = nrSummaryAstro;
        this.nrSummaryAtt = nrSummaryAtt;
        this.nrSummaryCal = nrSummaryCal;
        this.nrSummaryRot = nrSummaryRot;
        this.nrSummaryCg = nrSummaryCg;
        this.nrSummaryCorr = nrSummaryCorr;
        this.nrSummaryAux = nrSummaryAux;
        this.nrSummaryRes = nrSummaryRes;
    }
}
