import { BinHistogramInfo, createPlotsUtilFromNode, CurrentPlotsUtil, IterationHeader, PlotsTreeNode } from './current-plots-util.model';
import { showError } from '../../shared/util/funtions.util';
import { HttpClient, HttpParams } from '@angular/common/http';
import { createRequestOption } from '../../shared/util/request-util';
import { SERVER_API_URL } from '../../app.constants';

export function generateNextLevelPlotsUtilWithFilter(
  currentLevelPlotsUtil: CurrentPlotsUtil,
  childrenFilter: (node: PlotsTreeNode) => boolean
): [CurrentPlotsUtil, CurrentPlotsUtil[], string] {
  let activeTab = '';
  const bottomLevelPlotsUtil: CurrentPlotsUtil[] = [];
  const createBottomLevelPlotsUtil = (plotsTreeNode: PlotsTreeNode) => {
    bottomLevelPlotsUtil.push(createPlotsUtilFromNode(plotsTreeNode, currentLevelPlotsUtil));
  };
  if (currentLevelPlotsUtil.plotCategory.children.length > 0) {
    currentLevelPlotsUtil.plotCategory.children.filter(childrenFilter).forEach(createBottomLevelPlotsUtil);
    activeTab = currentLevelPlotsUtil.plotCategory.children[0].rawEnum;
  }
  return [currentLevelPlotsUtil, bottomLevelPlotsUtil, activeTab];
}

export function generateNextLevelPlotsUtil(currentLevelPlotsUtil: CurrentPlotsUtil): [CurrentPlotsUtil, CurrentPlotsUtil[], string] {
  return generateNextLevelPlotsUtilWithFilter(currentLevelPlotsUtil, ignore => true);
}

export function isDerivedFrom(plotCat: PlotsTreeNode, parent: string): boolean {
  return plotCat.parents.includes(parent);
}

export function createIterationHeaders(startIter: number, endIter: number, runId: string, latestIteration: number): IterationHeader[] {
  const iterations = [];
  let currentIteration = startIter;
  let next = function (i: number): number {
    return i + 1;
  };
  let finished = function (i1: number, i2: number): boolean {
    return i1 > i2;
  };
  if (startIter > endIter) {
    next = function (i: number): number {
      return i - 1;
    };
    finished = function (i1: number, i2: number): boolean {
      return i1 < i2;
    };
  }
  while (!finished(currentIteration, endIter)) {
    let headerString = currentIteration + ': ' + runId + '.' + currentIteration;
    if (currentIteration !== latestIteration) {
      headerString += ' - DONE';
    } else {
      headerString += ' - Ongoing';
    }
    iterations.push({ header: headerString, nr: currentIteration });
    currentIteration = next(currentIteration);
  }
  return iterations;
}

export const handleHttpRequestError = (error: { headers: { get: (arg: string) => any } }) => {
  const title = error.headers.get('convergenceMonitorFrontEndApp-error-title');
  const msg = error.headers.get('convergenceMonitorFrontEndApp-error-details-001');
  showError(title, msg);
};

export function getOpenImgInNewTabCallback(title: string): (readerResult: any) => void {
  return readerResult => {
    if (readerResult) {
      const image = new Image();
      image.src = readerResult;
      const w = window.open('');
      if (w) {
        w.document.write('<title>' + title + '</title>');
        w.document.write(image.outerHTML);
      }
    }
  };
}

export function getBinHistogramInfo(
  httpClient: HttpClient,
  plotCategory: PlotsTreeNode,
  iterationNr: number,
  successFunction: (response: BinHistogramInfo[] | null) => void
): void {
  const request = {
    plotCategory: plotCategory.rawEnum,
    iteration: iterationNr,
  };
  const params: HttpParams = createRequestOption(request);
  const requestUtl = SERVER_API_URL + 'api/binHistogramInfo';
  httpClient
    .get<BinHistogramInfo[]>(requestUtl, {
      params,
      observe: 'response',
    })
    .toPromise()
    .then(response => successFunction(response.body))
    .catch(handleHttpRequestError);
}

export function getPlot(
  httpClient: HttpClient,
  plotCategory: PlotsTreeNode,
  iterationNr: number,
  plotWidth: number,
  isCached: boolean,
  successFunction: (response: any) => void
): void {
  const request = {
    plotCategory: plotCategory.rawEnum,
    iteration: iterationNr,
    width: plotWidth,
    cached: isCached,
    export: 0,
  };
  const params: HttpParams = createRequestOption(request);
  const requestUtl = SERVER_API_URL + 'api/plot';
  httpClient
    .get(requestUtl, {
      params,
      observe: 'response',
      responseType: 'blob',
    })
    .toPromise()
    .then(response => successFunction(response.body))
    .catch(handleHttpRequestError);
}

export function getPlotMap(
  httpClient: HttpClient,
  plotCategory: PlotsTreeNode,
  iterationNr: number,
  plotWidth: number,
  isCached: boolean,
  successFunction: (response: any) => void
): void {
  const request = {
    plotCategory: plotCategory.rawEnum,
    iteration: iterationNr,
    width: plotWidth,
    cached: isCached,
    export: 0,
  };
  const params: HttpParams = createRequestOption(request);
  const requestUtl = SERVER_API_URL + 'api/plotMap';
  httpClient
    .get(requestUtl, {
      params,
      observe: 'response',
      responseType: 'blob',
    })
    .toPromise()
    .then(response => successFunction(response.body))
    .catch(handleHttpRequestError);
}

export function getMagBinnedHistogram(
  httpClient: HttpClient,
  plotCategory: PlotsTreeNode,
  iterationNr: number,
  plotWidth: number,
  isCached: boolean,
  binNr: number,
  successFunction: (response: any) => void
): void {
  const request = {
    plotCategory: plotCategory.rawEnum,
    iteration: iterationNr,
    width: plotWidth,
    cached: isCached,
    bin: binNr,
  };
  const params: HttpParams = createRequestOption(request);
  const requestUtl = SERVER_API_URL + 'api/magBinnedHistogram';
  httpClient
    .get(requestUtl, {
      params,
      observe: 'response',
      responseType: 'blob',
    })
    .toPromise()
    .then(response => successFunction(response.body))
    .catch(handleHttpRequestError);
}
