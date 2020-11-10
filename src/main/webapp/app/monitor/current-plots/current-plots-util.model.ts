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
    public iterIdDecoded: string,
    public iterId: number,
    public plotCategory: PlotsTreeNode
  ) {}
}

export class BinHistogramInfo {
  constructor(public info: string, public isEmpty: boolean, public isUpdate: boolean) {}
}

export class NrEffectsUsed {
  constructor(public ac: number, public al: number) {}
}

export class CalibrationEffectInfo {
  constructor(public name: string, public id: number, public description: string, public functions: CalibrationEffectInfo[]) {}
}

export class PlotSummaryInfo {
  constructor(
    public nrSummaryAstro: number,
    public nrSummaryAtt: number,
    public nrSummaryCal: number,
    public nrSummaryRot: number,
    public nrSummaryCg: number,
    public nrSummaryCorr: number,
    public nrSummaryAux: number,
    public nrSummaryRes: number
  ) {}
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
  return new CurrentPlotsUtil(
    plotsUtil.nrParamSolved,
    plotsUtil.currentIter,
    plotsUtil.runId,
    plotsUtil.iterIdDecoded,
    plotsUtil.iterId,
    plotsTreeNode
  );
}
