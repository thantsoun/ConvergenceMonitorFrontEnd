import { Component, Input, OnInit } from '@angular/core';
import { createPlotsUtilFromNode, CurrentPlotsUtil, IterationHeader } from './current-plots-util.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { createIterationHeaders, getHandleImageInNewTabSuccess, handleHttpRequestError } from './function-utils';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared/util/request-util';

@Component({
  selector: 'jhi-global-plots',
  templateUrl: './global-plots.component.html',
})
export class GlobalPlotsComponent implements OnInit {
  currentPlotsUtil!: CurrentPlotsUtil;
  gguGroupNames: string[] = [];
  activeTab = '';
  iterationHeaders: IterationHeader[] = [];
  initialized = false;

  plotIndexesToPlot: Map<number, any>[][] = [];
  plotWidth = 400;
  plotWidthExtra = 1200;

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.initialized = true;
    this.createPlots();
  }

  @Input()
  set plotsUtil(currentPlotsUtil: CurrentPlotsUtil) {
    this.currentPlotsUtil = createPlotsUtilFromNode(currentPlotsUtil.plotCategory.children[0], currentPlotsUtil);
    if (this.initialized) {
      this.createPlots();
    }
  }

  private createPlots(): void {
    this.createIterations();
    this.getGguGroupNames();
  }

  private getImages(): void {
    this.plotIndexesToPlot = [];
    for (let gguGroupIndex = 0; gguGroupIndex < this.gguGroupNames.length; gguGroupIndex++) {
      this.plotIndexesToPlot.push([]);
      for (let plotCategoryIndex = 0; plotCategoryIndex < this.currentPlotsUtil.plotCategory.children.length; plotCategoryIndex++) {
        this.plotIndexesToPlot[gguGroupIndex].push(new Map());
        for (const iteration of this.iterationHeaders) {
          this.getPlotMultiGlobals(
            gguGroupIndex,
            plotCategoryIndex,
            iteration.nr,
            this.plotWidth,
            this.getHandlePlotSuccess(gguGroupIndex, plotCategoryIndex, iteration.nr)
          );
        }
      }
    }
  }

  private createIterations(): void {
    this.iterationHeaders = createIterationHeaders(
      this.currentPlotsUtil.currentIter,
      this.currentPlotsUtil.currentIter,
      this.currentPlotsUtil.runId,
      this.currentPlotsUtil.currentIter
    );
  }

  hasPlotReady(globalIndex: number, plotCategoryIndex: number, iteration: number): boolean {
    return (
      globalIndex < this.plotIndexesToPlot.length &&
      plotCategoryIndex < this.plotIndexesToPlot[globalIndex].length &&
      this.plotIndexesToPlot[globalIndex][plotCategoryIndex].has(iteration)
    );
  }

  loadPlotBig(globalIndex: number, plotCategoryIndex: number, iteration: number): void {
    const title =
      this.gguGroupNames[globalIndex] + '[' + this.currentPlotsUtil.plotCategory.children[plotCategoryIndex].description + '] Plot';
    this.getPlotMultiGlobals(globalIndex, plotCategoryIndex, iteration, this.plotWidthExtra, getHandleImageInNewTabSuccess(title));
  }

  private getGguGroupNames(): void {
    const requestUtl = SERVER_API_URL + 'api/getGlobalGroupNames';
    this.httpClient
      .get<string[]>(requestUtl, { observe: 'response' })
      .toPromise()
      .then(response => {
        if (response.body !== null) {
          this.gguGroupNames = response.body;
          this.getImages();
        } else {
          this.gguGroupNames = [];
        }
      })
      .catch(handleHttpRequestError);
  }

  private getPlotMultiGlobals(
    globalIndex: number,
    plotCategoryIndex: number,
    iterationNr: number,
    plotWidth: number,
    successFunction: (response: any) => void
  ): void {
    const request = {
      globalGroupIndex: globalIndex,
      set: plotCategoryIndex,
      iteration: iterationNr,
      width: plotWidth,
    };
    const params: HttpParams = createRequestOption(request);
    const requestUtl = SERVER_API_URL + 'api/plotMultiGlobals';
    this.httpClient
      .get(requestUtl, {
        params,
        observe: 'response',
        responseType: 'blob',
      })
      .toPromise()
      .then(response => successFunction(response.body))
      .catch(handleHttpRequestError);
  }

  private getHandlePlotSuccess(globalIndex: number, plotCategoryIndex: number, iteration: number): (imageBlob: any) => void {
    return imageBlob => {
      const reader = new FileReader();
      reader.addEventListener(
        'load',
        () => {
          this.plotIndexesToPlot[globalIndex][plotCategoryIndex].set(iteration, reader.result);
        },
        false
      );
      if (imageBlob) {
        reader.readAsDataURL(imageBlob);
      }
    };
  }
}
