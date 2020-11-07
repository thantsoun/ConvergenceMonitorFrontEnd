import { Component, Input, OnInit } from '@angular/core';
import { CurrentPlotsUtil, IterationHeader, PlotCatToPlot, PlotsTreeNode } from './current-plots-util.model';
import { HttpClient } from '@angular/common/http';
import {
  createIterationHeaders,
  generateNextLevelPlotsUtil,
  getHandleImageInNewTabSuccess,
  getHandlePlotSuccess,
  getPlot,
} from './function-utils';

@Component({
  selector: 'jhi-attitude-plots',
  templateUrl: './attitude-plots.component.html',
})
export class AttitudePlotsComponent implements OnInit {
  currentPlotsUtil!: CurrentPlotsUtil;
  nextPlotsUtil!: CurrentPlotsUtil[];
  startIter = 1;
  endIter = 0;
  activeTab = '';
  iterationHeaders: IterationHeader[] = [];
  initialized = false;

  plotCatToPlot!: PlotCatToPlot;
  plotWidth = 400;
  plotWidthExtra = 1200;

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.initialized = true;
    this.createPlots();
  }

  @Input()
  set plotsUtil(currentPlotsUtil: CurrentPlotsUtil) {
    [this.currentPlotsUtil, this.nextPlotsUtil, this.activeTab] = generateNextLevelPlotsUtil(currentPlotsUtil);
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
    this.plotCatToPlot = {};
    for (const parent of this.nextPlotsUtil) {
      for (const child of parent.plotCategory.children) {
        for (const iteration of this.iterationHeaders) {
          getPlot(
            this.httpClient,
            child,
            iteration.nr,
            this.plotWidth,
            true,
            getHandlePlotSuccess(child, iteration.nr, this.plotCatToPlot)
          );
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

  hasPlotReady(plotCatEnum: string, iteration: number): boolean {
    return plotCatEnum in this.plotCatToPlot && iteration in this.plotCatToPlot[plotCatEnum];
  }

  loadPlotBig(plotCategoryParert: PlotsTreeNode, plotCategory: PlotsTreeNode, iteration: number): void {
    const title = plotCategoryParert.description + '[' + plotCategory.description + '] Plot';
    getPlot(this.httpClient, plotCategory, iteration, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  isNotHistorical(plotCat: PlotsTreeNode): boolean {
    const rawEnum = plotCat.rawEnum;
    return rawEnum !== 'TIME_ATTITUDE_X' && rawEnum !== 'TIME_ATTITUDE_Y' && rawEnum !== 'TIME_ATTITUDE_Z';
  }

  filterNonHistorical(nodes: PlotsTreeNode[]): PlotsTreeNode[] {
    return nodes.filter(node => this.isNotHistorical(node));
  }
}
