package gaia.cu3.agis.convergence.domain;

import gaia.cu3.agis.algo.gis.convergence.source.MagnitudeBinHistoImpl;

import java.util.Objects;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class BinHistogramInfo {

    public final String info;
    public final boolean isEmpty;
    public final boolean isUpdate;

    private BinHistogramInfo(String info, boolean isEmpty, boolean isUpdate) {
        this.info = info;
        this.isEmpty = isEmpty;
        this.isUpdate = isUpdate;
    }
    
    public static BinHistogramInfo getBinHistogramInfo(MagnitudeBinHistoImpl magnitudeBinHisto, boolean isUpdate) {
        return new BinHistogramInfo(magnitudeBinHisto.getInfo(), Objects.isNull(magnitudeBinHisto.getHisto()), isUpdate);
    }
}
