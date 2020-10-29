package gaia.cu3.agis.convergence.domain;

import gaia.cu3.agis.plotting.PlotCategory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * $Author$
 * $Date$
 * $Id$
 * $Rev$
 */
public class CurrentPlotsUtil {

    public final int nrParamSolved;
    public final int currentIter;
    public final String runId;
    public final String iterId;
    public final Node plotCategory = new Node();

    public CurrentPlotsUtil(PlotCategory[] plotCategoriesArray, int nrParamSolved, int currentIter, String runId, String iterId) {
        this.nrParamSolved = nrParamSolved;
        this.runId = runId;
        this.iterId = iterId;
        List<PlotCategory> plotCategoryList = new ArrayList<>(Arrays.asList(plotCategoriesArray));
        plotCategoryList.remove(PlotCategory.CONVSUMMARY);
        final List<ProcessItem> toProcess = plotCategoryList.stream()
                .map(plotCategory -> new ProcessItem(this.plotCategory, plotCategory))
                .collect(Collectors.toList());
        final EnumSet<PlotCategory> processed = EnumSet.noneOf(PlotCategory.class);
        while (!toProcess.isEmpty()) {
            List<ProcessItem> copyCollection = new ArrayList<>(toProcess);
            processStream(copyCollection.stream(), toProcess, processed);
            toProcess.removeAll(copyCollection);
        }
        this.currentIter = currentIter;
    }

    private void processStream(Stream<ProcessItem> stream, final List<ProcessItem> toProcess, final EnumSet<PlotCategory> processed) {
        stream
                .filter(Objects::nonNull)
                .forEach(item -> {
                    Node node = new Node(item.plotCategory);
                    item.father.children.add(node);
                    addChildren(toProcess, processed, item.plotCategory, node);
                    processed.add(item.plotCategory);
                });
    }

    private void addChildren(List<ProcessItem> toProcess, EnumSet<PlotCategory> processed, PlotCategory plotCategory, Node father) {
        Arrays.stream(plotCategory.getSubCategories())
                .filter(p -> !processed.contains(p))
                .forEach(p -> toProcess.add(new ProcessItem(father, p)));
    }


    public static class Node {

        public final List<Node> children = new ArrayList<>();
        public final String code;
        public final String description;
        public final String rawEnum;
        
        public Node(PlotCategory plotCategory) {
            this.rawEnum = plotCategory.toString();
            this.code = plotCategory.getCode();
            this.description = plotCategory.getDescription();
        }

        public Node() {
            this.rawEnum = "NONE";
            this.code = "root";
            this.description = "All Plot Categories";
        }
    }
    
    private static class ProcessItem {
        
        private final Node father;
        private final PlotCategory plotCategory;

        public ProcessItem(Node father, PlotCategory plotCategory) {
            this.father = father;
            this.plotCategory = plotCategory;
        }
    }

}
