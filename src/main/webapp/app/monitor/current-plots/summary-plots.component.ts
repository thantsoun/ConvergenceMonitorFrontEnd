import { Component, OnInit } from '@angular/core';
import { PlotSummaryInfo } from './current-plots-util.model';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { downLoadFile, getHandleImageInNewTabSuccess, handleHttpRequestError } from './function-utils';
import { createRequestOption } from '../../shared/util/request-util';

@Component({
  selector: 'jhi-summary-plots',
  templateUrl: './summary-plots.component.html',
})
export class SummaryPlotsComponent implements OnInit {
  plotSummaryInfo: PlotSummaryInfo = new PlotSummaryInfo(0, 0, 0, 0, 0, 0, 0, 0);
  plotWidth = 400;
  plotWidthExtra = 1200;
  summaryPlotAll: any = null;
  summaryPlot!: Map<number, any>;
  refPlot!: Map<number, any>;
  cgPlot!: Map<number, any>;
  corrPlot!: Map<number, any>;
  auxPlot!: Map<number, any>;
  resPlot: any = null;

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.createPlots();
  }

  private createPlots(): void {
    this.httpClient
      .get<PlotSummaryInfo>(SERVER_API_URL + 'api/plotSummaryInfo')
      .toPromise()
      .then(response => {
        this.plotSummaryInfo = response;
        this.getPlots();
      })
      .catch(handleHttpRequestError);
  }

  private getPlots(): void {
    this.summaryPlotAll = null;
    this.resPlot = null;
    this.summaryPlot = new Map();
    this.refPlot = new Map();
    this.cgPlot = new Map();
    this.corrPlot = new Map();
    this.auxPlot = new Map();
    this.getSummaryPlotAll(
      this.plotWidth,
      true,
      this.getHandlePlotSuccess(img => (this.summaryPlotAll = img))
    );
    for (let i = 0; i < this.plotSummaryInfo.nrSummaryAstro; i++) {
      this.getSummaryPlot(
        i,
        this.plotWidth,
        true,
        this.getHandlePlotSuccess(img => this.summaryPlot.set(i, img))
      );
    }
    for (let i = 0; i < this.plotSummaryInfo.nrSummaryRot; i++) {
      this.getRefPlot(
        i,
        this.plotWidth,
        true,
        this.getHandlePlotSuccess(img => this.refPlot.set(i, img))
      );
    }
    for (let i = 0; i < this.plotSummaryInfo.nrSummaryCg; i++) {
      this.getCGPlot(
        i,
        this.plotWidth,
        true,
        this.getHandlePlotSuccess(img => this.cgPlot.set(i, img))
      );
    }
    for (let i = 0; i < this.plotSummaryInfo.nrSummaryCorr; i++) {
      this.getCorrPlot(
        i,
        this.plotWidth,
        true,
        this.getHandlePlotSuccess(img => this.corrPlot.set(i, img))
      );
    }
    for (let i = 0; i < this.plotSummaryInfo.nrSummaryAux; i++) {
      this.getAuxPlot(
        i,
        this.plotWidth,
        true,
        this.getHandlePlotSuccess(img => this.auxPlot.set(i, img))
      );
    }
    if (this.plotSummaryInfo.nrSummaryRes > 0) {
      this.getResPlot(
        this.plotWidth,
        true,
        this.getHandlePlotSuccess(img => (this.resPlot = img))
      );
    }
  }

  counter(size: number): number[] {
    return new Array(size).fill(0).map((x, i) => i);
  }

  hasSummaryPlotReady(summary: number): boolean {
    return this.summaryPlot.has(summary);
  }

  hasRefPlotReady(ref: number): boolean {
    return this.refPlot.has(ref);
  }

  hasCGPlotReady(cg: number): boolean {
    return this.cgPlot.has(cg);
  }

  hasAuxPlotReady(aux: number): boolean {
    return this.auxPlot.has(aux);
  }

  hasCorrPlotReady(corr: number): boolean {
    return this.corrPlot.has(corr);
  }

  hasSummaryAllPlotReady(): boolean {
    return this.summaryPlotAll !== null;
  }

  hasResPlotReady(): boolean {
    return this.resPlot !== null;
  }

  loadSummaryPlotBig(summary: number): void {
    const title = 'Astrometric Summary ' + summary;
    this.getSummaryPlot(summary, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  loadRefPlotBig(ref: number): void {
    const title = 'Frame rotation summary ' + ref;
    this.getRefPlot(ref, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  loadCorrPlotBig(corr: number): void {
    const title = 'Correlation summary ' + corr;
    this.getCorrPlot(corr, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  loadAuxPlotBig(aux: number): void {
    const title = 'Auxiliary summary ' + aux;
    this.getAuxPlot(aux, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  loadResPlotBig(): void {
    const title = 'Residuals summary';
    this.getResPlot(this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  loadCGPlotBig(cg: number): void {
    const title = 'Solution Method Scalars ' + cg;
    this.getCGPlot(cg, this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  loadSummaryAllPlotBig(): void {
    const title = 'Astrometric Summary All';
    this.getSummaryPlotAll(this.plotWidthExtra, false, getHandleImageInNewTabSuccess(title));
  }

  exportSummaryAll(): void {
    const request = {
      filename: 'summary_all.csv',
    };
    this.exportCsv(request, 'api/getAllSummaryCsv');
  }

  exportSummary(summaryNumber: number): void {
    const request = {
      filename: 'summary_' + summaryNumber + '.csv',
      summary: summaryNumber,
    };
    this.exportCsv(request, 'api/getSummaryCsv');
  }

  exportRef(rotNumber: number): void {
    const request = {
      filename: 'frame_reference_' + rotNumber + '.csv',
      rot: rotNumber,
    };
    this.exportCsv(request, 'api/getRotSummaryCsv');
  }

  exportCG(cgNumber: number): void {
    const request = {
      filename: 'solution_method_scalars_' + cgNumber + '.csv',
      cg: cgNumber,
    };
    this.exportCsv(request, 'api/getCGSummaryCsv');
  }

  exportRes(): void {
    const request = {
      filename: 'residuals.csv',
    };
    this.exportCsv(request, 'api/getResSummaryCsv');
  }

  exportCorr(corrNumber: number): void {
    const request = {
      filename: 'correlation_' + corrNumber + '.csv',
      corr: corrNumber,
    };
    this.exportCsv(request, 'api/getCorrSummaryCsv');
  }

  exportAux(auxNumber: number): void {
    const request = {
      filename: 'auxiliary_' + auxNumber + '.csv',
      aux: auxNumber,
    };
    this.exportCsv(request, 'api/getAuxSummaryCsv');
  }

  private getSummaryPlotAll(plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotAllSummaryPng', successFunction);
  }

  private getSummaryPlot(summaryIndex: number, plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      summary: summaryIndex,
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotSummaryPng', successFunction);
  }

  private getRefPlot(rotNumber: number, plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      rot: rotNumber,
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotRotSummaryPng', successFunction);
  }

  private getCGPlot(cgNumber: number, plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      cg: cgNumber,
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotCGSummaryPng', successFunction);
  }

  private getAuxPlot(auxNumber: number, plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      aux: auxNumber,
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotAuxSummaryPng', successFunction);
  }

  private getCorrPlot(corrNumber: number, plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      corr: corrNumber,
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotCorrSummaryPng', successFunction);
  }

  private getResPlot(plotWidth: number, isCached: boolean, successFunction: (response: any) => void): void {
    const request = {
      width: plotWidth,
      cached: isCached,
    };
    this.getPlot(request, 'api/plotResSummaryPng', successFunction);
  }

  private getPlot(request: any, endpoint: string, successFunction: (response: any) => void): void {
    const params: HttpParams = createRequestOption(request);
    this.httpClient
      .get(SERVER_API_URL + endpoint, {
        params,
        observe: 'response',
        responseType: 'blob',
      })
      .toPromise()
      .then(response => successFunction(response.body))
      .catch(handleHttpRequestError);
  }

  private exportCsv(request: any, endpoint: string): void {
    const params: HttpParams = createRequestOption(request);
    this.httpClient
      .get(SERVER_API_URL + endpoint, {
        params,
        observe: 'response',
        responseType: 'blob',
      })
      .toPromise()
      .then(response => {
        downLoadFile(response);
      })
      .catch(handleHttpRequestError);
  }

  private getHandlePlotSuccess(callback: (imgBlob: any) => void): (imageBlob: any) => void {
    return imageBlob => {
      const reader = new FileReader();
      reader.addEventListener(
        'load',
        () => {
          callback(reader.result);
        },
        false
      );
      if (imageBlob) {
        reader.readAsDataURL(imageBlob);
      }
    };
  }
}
