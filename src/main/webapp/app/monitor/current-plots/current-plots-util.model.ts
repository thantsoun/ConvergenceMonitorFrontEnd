export class PlotsTreeNode {
  constructor(public code: string, public description: string, public rawEnum: string, public children: PlotsTreeNode[]) {}
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

export function createPlotsUtilFromNode(plotsTreeNode: PlotsTreeNode, plotsUtil: CurrentPlotsUtil): CurrentPlotsUtil {
  return new CurrentPlotsUtil(plotsUtil.nrParamSolved, plotsUtil.currentIter, plotsUtil.runId, plotsUtil.iterId, plotsTreeNode);
}

export function generateNextLevelPlotsUtil(currentLevelPlotsUtil: CurrentPlotsUtil): [CurrentPlotsUtil, CurrentPlotsUtil[], string] {
  let activeTab = '';
  const bottomLevelPlotsUtil: CurrentPlotsUtil[] = [];
  const createBottomLevelPlotsUtil = (plotsTreeNode: PlotsTreeNode) => {
    bottomLevelPlotsUtil.push(createPlotsUtilFromNode(plotsTreeNode, currentLevelPlotsUtil));
  };
  if (currentLevelPlotsUtil.plotCategory.children.length > 0) {
    currentLevelPlotsUtil.plotCategory.children.forEach(createBottomLevelPlotsUtil);
    activeTab = currentLevelPlotsUtil.plotCategory.children[0].rawEnum;
  }
  return [currentLevelPlotsUtil, bottomLevelPlotsUtil, activeTab];
}
