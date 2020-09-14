package gaia.cu3.agis.convergence.domain;

import gaia.cu1.tools.dal.table.GaiaTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class AllRuns {

    public final String query = "select a.runId, a.description, a.startTime, a.endTime,"
            +" a.numIterations from dpccu3agiswbRun a where (SELECT max(a.numiterations) FROM dpccu3agiswbrun) = a.numiterations order by a.startTime desc";
    public final String runIdHeader = "RUNID [YY-MM-DD.Run#]";
    public final String numIterationsHeader = "NUMITERATIONS";
    public final String descriptionHeader = "DESCRIPTION";
    public final String startTimeHeader = "START TIME";
    public final String endTimeHeader = "END TIME";
    public final List<Run> runs = new ArrayList<>();
    
    public void addRun(Run run) {
        runs.add(run);
    }

    public static class Run {
        
        public final String runIdRaw;
        public final String numIterationsRaw;
        public String runId;
        public String numIterations;
        public String description;
        public String startTime;
        public String endTime;

        public Run(String runIdRaw, String numIterationsRaw, GaiaTable tb, BiFunction<GaiaTable, Integer, String> valueFormatter) {
            this.runIdRaw = runIdRaw;
            this.numIterationsRaw = numIterationsRaw;

            for (int i = 0; i < tb.getNumCols(); i++) {
                String value = valueFormatter.apply(tb, i);
                String columnName = tb.getColumnName(i);
                switch (columnName) {
                    case "RUNID":
                        this.runId = value;
                        break;
                    case "NUMITERATIONS":
                        this.numIterations = value;
                        break;
                    case "DESCRIPTION":
                        this.description = value;
                        break;
                    case "STARTTIME":
                        this.startTime = value;
                        break;
                    case "ENDTIME":
                        this.endTime = value;
                        break;
                }

            }
        }
    }
}
