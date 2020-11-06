import { Component, Input, OnInit } from '@angular/core';
import {
  BinHistogramInfo,
  CurrentPlotsUtil,
  IterationHeader,
  PlotCatToBinHistInfo,
  PlotCatToPlot,
  PlotsTreeNode,
} from './current-plots-util.model';
import { HttpClient } from '@angular/common/http';
import {
  createIterationHeaders,
  generateNextLevelPlotsUtilWithFilter,
  getBinHistogramInfo,
  getMagBinnedHistogram,
  getOpenImgInNewTabCallback,
  getPlot,
  getPlotMap,
  isDerivedFrom,
} from './function-utils';

@Component({
  selector: 'jhi-source-plots-bottom-level',
  templateUrl: './source-plots-bottom-level.component.html',
})
export class SourcePlotsBottomLevelComponent implements OnInit {
  currentPlotsUtil!: CurrentPlotsUtil;
  nextPlotsUtil!: CurrentPlotsUtil[];
  startIter = 1;
  endIter = 0;
  activeTab = '';
  iterationHeaders: IterationHeader[] = [];
  initialized = false;
  plotCatToBinHistInfo!: PlotCatToBinHistInfo;
  plotCatToPlot!: PlotCatToPlot;
  plotWidth = 400;
  plotWidthExtra = 1200;
  extrasCheck: { [rawEnum: string]: boolean } = {};

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.initialized = true;
    this.createPlots();
  }

  @Input()
  set plotsUtil(currentPlotsUtil: CurrentPlotsUtil) {
    [this.currentPlotsUtil, this.nextPlotsUtil, this.activeTab] = generateNextLevelPlotsUtilWithFilter(
      currentPlotsUtil,
      node =>
        node.rawEnum !== 'SOURCE_DISTRIBUTION' &&
        node.rawEnum !== 'SOURCE_DISTRIBUTION_ERROR' &&
        node.rawEnum !== 'SOURCE_RADIAL_PROPER_MOTION'
    );
    if (this.initialized) {
      this.createPlots();
    }
  }

  @Input()
  set startIteration(startIter: number) {
    this.startIter = startIter;
    if (this.initialized) {
      this.createPlots();
    }
  }

  @Input()
  set endIteration(endIter: number) {
    this.endIter = endIter;
    if (this.initialized) {
      this.createPlots();
    }
  }

  private createPlots(): void {
    this.createIterations();
    this.getImagesAndLinks();
  }

  private getImagesAndLinks(): void {
    this.plotCatToBinHistInfo = {};
    this.plotCatToPlot = {};
    this.extrasCheck = {};
    for (const parent of this.nextPlotsUtil) {
      for (const child of parent.plotCategory.children) {
        this.extrasCheck[child.rawEnum] = true;
        for (const iteration of this.iterationHeaders) {
          getPlot(this.httpClient, child, iteration.nr, this.plotWidth, true, this.getHandlePlotSuccess(child, iteration.nr));
          if (this.hasBinnedHistogram(child)) {
            getBinHistogramInfo(this.httpClient, child, iteration.nr, this.getHandleBinHistSuccess(child, iteration.nr));
          }
        }
      }
    }
  }

  private createIterations(): void {
    this.iterationHeaders = createIterationHeaders(
      this.startIter,
      this.endIter,
      this.currentPlotsUtil.runId,
      this.currentPlotsUtil.currentIter
    );
  }

  hasPlotMap(plotCat: PlotsTreeNode): boolean {
    return (
      !isDerivedFrom(plotCat, 'MEAN_RESIDUALS') &&
      !isDerivedFrom(plotCat, 'EX_NOISE_EXTRA_PLOTS') &&
      this.isNotCorrelationNormErrOrDist(plotCat)
    );
  }

  isNotCorrelationNormErrOrDist(plotCat: PlotsTreeNode): boolean {
    const code = plotCat.code;
    return !code.includes('correlation') && !code.includes('normerr') && !code.includes('dist');
  }

  isUpdate(plotCat: PlotsTreeNode): boolean {
    return !plotCat.code.endsWith('.true');
  }

  hasBinnedHistogram(plotCat: PlotsTreeNode): boolean {
    return isDerivedFrom(plotCat, 'ASTROMETRIC_SOURCE') && this.isNotCorrelationNormErrOrDist(plotCat);
  }

  hasBinnedHistogramReady(plotCatEnum: string, iteration: number): boolean {
    return plotCatEnum in this.plotCatToBinHistInfo && iteration in this.plotCatToBinHistInfo[plotCatEnum];
  }

  hasPlotReady(plotCatEnum: string, iteration: number): boolean {
    return plotCatEnum in this.plotCatToPlot && iteration in this.plotCatToPlot[plotCatEnum];
  }

  private getHandleBinHistSuccess(plotCategory: PlotsTreeNode, iter: number): (response: BinHistogramInfo[] | null) => void {
    return response => {
      if (response !== null) {
        if (!(plotCategory.rawEnum in this.plotCatToBinHistInfo)) {
          this.plotCatToBinHistInfo[plotCategory.rawEnum] = {};
        }
        this.plotCatToBinHistInfo[plotCategory.rawEnum][iter] = response;
      }
    };
  }

  private getHandlePlotSuccess(plotCategory: PlotsTreeNode, iteration: number): (imageBlob: any) => void {
    return imageBlob => {
      if (!(plotCategory.rawEnum in this.plotCatToPlot)) {
        this.plotCatToPlot[plotCategory.rawEnum] = {};
      }
      const reader = new FileReader();
      reader.addEventListener(
        'load',
        () => {
          this.plotCatToPlot[plotCategory.rawEnum][iteration] = reader.result;
        },
        false
      );
      if (imageBlob) {
        reader.readAsDataURL(imageBlob);
      }
    };
  }

  private getHandleImageInNewTabSuccess(title: string): (imageBlob: any) => void {
    return imageBlob => {
      const reader = new FileReader();
      reader.addEventListener(
        'load',
        () => {
          getOpenImgInNewTabCallback(title)(reader.result);
        },
        false
      );
      if (imageBlob) {
        reader.readAsDataURL(imageBlob);
      }
    };
  }

  binHistogramInfoFor(rawEnum: string, iteration: number): BinHistogramInfo[] {
    if (this.hasBinnedHistogramReady(rawEnum, iteration)) {
      return this.plotCatToBinHistInfo[rawEnum][iteration];
    } else {
      return [];
    }
  }

  loadPlotBig(plotCategoryParert: PlotsTreeNode, plotCategory: PlotsTreeNode, iteration: number): void {
    const title = plotCategoryParert.description + '[' + plotCategory.description + '] Plot';
    getPlot(this.httpClient, plotCategory, iteration, this.plotWidthExtra, false, this.getHandleImageInNewTabSuccess(title));
  }

  loadPlotMap(plotCategoryParert: PlotsTreeNode, plotCategory: PlotsTreeNode, iteration: number): void {
    const title = plotCategoryParert.description + '[' + plotCategory.description + '] Map';
    getPlotMap(this.httpClient, plotCategory, iteration, this.plotWidthExtra, false, this.getHandleImageInNewTabSuccess(title));
  }

  loadMagBinnedHistogram(plotCategory: PlotsTreeNode, iteration: number, bin: number, binInfo: string): void {
    const title = binInfo + ' Histogram';
    getMagBinnedHistogram(
      this.httpClient,
      plotCategory,
      iteration,
      this.plotWidthExtra,
      false,
      bin,
      this.getHandleImageInNewTabSuccess(title)
    );
  }
}
