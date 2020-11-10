import { Component, Input, OnInit } from '@angular/core';
import { CalibrationEffectInfo, CurrentPlotsUtil, IterationHeader } from './current-plots-util.model';
import {
  createIterationHeaders,
  getHandleImageInNewTabSuccess,
  getOpenImgInNewTabCallback,
  handleHttpRequestError,
} from './function-utils';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../../shared/util/request-util';

@Component({
  selector: 'jhi-calibration-plots-bottom-level',
  templateUrl: './calibration-plots-bottom-level.component.html',
})
export class CalibrationPlotsBottomLevelComponent implements OnInit {
  currentPlotsUtil!: CurrentPlotsUtil;
  update = false;
  startIter = 1;
  endIter = 0;
  activeTab = '';
  iterationHeaders: IterationHeader[] = [];
  initialized = false;
  calibrationEffectInfo: CalibrationEffectInfo[] = [];

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
    this.currentPlotsUtil = currentPlotsUtil;
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

  @Input()
  set isUpdate(update: boolean) {
    this.update = update;
    if (this.initialized) {
      this.createPlots();
    }
  }

  private createPlots(): void {
    this.createIterations();
    this.createTabs();
  }

  private createTabs(): void {
    const requestUtl = SERVER_API_URL + 'api/calibrationPlotsInfo';
    const request = {
      plotCategory: this.currentPlotsUtil.plotCategory.rawEnum,
    };
    const params: HttpParams = createRequestOption(request);
    this.calibrationEffectInfo = [];
    this.httpClient
      .get<CalibrationEffectInfo[]>(requestUtl, {
        params,
        observe: 'response',
      })
      .toPromise()
      .then(response => {
        if (response.body !== null) {
          this.calibrationEffectInfo = response.body;
          this.activeTab = this.calibrationEffectInfo[0].name;
          this.getImages();
        }
      })
      .catch(handleHttpRequestError);
  }

  private getImages(): void {
    this.plotIndexesToPlot = [];
    for (let effectIndex = 0; effectIndex < this.calibrationEffectInfo.length; effectIndex++) {
      this.plotIndexesToPlot.push([]);
      for (let functionIndex = 0; functionIndex < this.calibrationEffectInfo[effectIndex].functions.length; functionIndex++) {
        this.plotIndexesToPlot[effectIndex].push(new Map());
        for (const iteration of this.iterationHeaders) {
          this.getPlotCalibration(
            effectIndex,
            functionIndex,
            iteration.nr,
            this.plotWidth,
            true,
            this.getHandlePlotSuccess(effectIndex, functionIndex, iteration.nr)
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

  hasPlotReady(effectIndex: number, functionIndex: number, iteration: number): boolean {
    return (
      effectIndex < this.plotIndexesToPlot.length &&
      functionIndex < this.plotIndexesToPlot[effectIndex].length &&
      this.plotIndexesToPlot[effectIndex][functionIndex].has(iteration)
    );
  }

  loadPlotBig(effectIndex: number, functionIndex: number, iteration: number): void {
    const title =
      this.calibrationEffectInfo[effectIndex].description +
      '[' +
      this.calibrationEffectInfo[effectIndex].functions[functionIndex].description +
      '] Plot';
    this.getPlotCalibration(effectIndex, functionIndex, iteration, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  private getPlotCalibration(
    effectIndex: number,
    functionIndex: number,
    iterationNr: number,
    plotWidth: number,
    isCached: boolean,
    successFunction: (response: any) => void
  ): void {
    const request = {
      plotCategory: this.currentPlotsUtil.plotCategory.rawEnum,
      iteration: iterationNr,
      effectId: effectIndex,
      functionId: functionIndex,
      isUpdate: this.update,
      width: plotWidth,
      cached: isCached,
      export: 0,
    };
    const params: HttpParams = createRequestOption(request);
    const requestUtl = SERVER_API_URL + 'api/plotCalibration';
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

  private getHandlePlotSuccess(effectIndex: number, functionIndex: number, iteration: number): (imageBlob: any) => void {
    return imageBlob => {
      const reader = new FileReader();
      reader.addEventListener(
        'load',
        () => {
          this.plotIndexesToPlot[effectIndex][functionIndex].set(iteration, reader.result);
        },
        false
      );
      if (imageBlob) {
        reader.readAsDataURL(imageBlob);
      }
    };
  }
}
