export class PlotsTreeNode {
  constructor(
    public code: string,
    public description: string,
    public rawEnum: string,
    public children: PlotsTreeNode[],
    public parents: String[]
  ) {}
}

export class CurrentPlotsUtil {
  constructor(
    public nrParamSolved: number,
    public currentIter: number,
    public runId: string,
    public iterId: string,
    public plotCategory: PlotsTreeNode
  ) {}
}

export class BinHistogramInfo {
  constructor(public info: string, public isEmpty: boolean, public isUpdate: boolean) {}
}

export interface IterationToBinHistInfo {
  [iteration: number]: BinHistogramInfo[];
}

export interface PlotCatToBinHistInfo {
  [rawEnum: string]: IterationToBinHistInfo;
}

export interface IterationToPlot {
  [iteration: number]: any;
}

export interface PlotCatToPlot {
  [rawEnum: string]: IterationToPlot;
}

export class IterationHeader {
  constructor(public header: string, public nr: number) {}
}

export function createPlotsUtilFromNode(plotsTreeNode: PlotsTreeNode, plotsUtil: CurrentPlotsUtil): CurrentPlotsUtil {
  return new CurrentPlotsUtil(plotsUtil.nrParamSolved, plotsUtil.currentIter, plotsUtil.runId, plotsUtil.iterId, plotsTreeNode);
}
