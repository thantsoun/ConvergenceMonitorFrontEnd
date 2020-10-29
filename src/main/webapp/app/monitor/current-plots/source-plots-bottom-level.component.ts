import { Component, Input, OnInit } from '@angular/core';
import { CurrentPlotsUtil, generateNextLevelPlotsUtil } from './current-plots-util.model';

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
  iterations: { header: string; nr: number }[] = [];
  initialized = false;

  constructor() {}

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
    this.createIterationHeaders();
  }

  private createIterationHeaders(): void {
    this.iterations = [];
    let currentIteration = this.startIter;
    let next = function (i: number): number {
      return i + 1;
    };
    let finished = function (i1: number, i2: number): boolean {
      return i1 > i2;
    };
    if (this.startIter > this.endIter) {
      next = function (i: number): number {
        return i - 1;
      };
      finished = function (i1: number, i2: number): boolean {
        return i1 < i2;
      };
    }
    while (!finished(currentIteration, this.endIter)) {
      let headerString = currentIteration + ': ' + this.currentPlotsUtil.runId + '.' + currentIteration;
      if (currentIteration !== this.currentPlotsUtil.currentIter) {
        headerString += ' - DONE';
      } else {
        headerString += ' - Ongoing';
      }
      this.iterations.push({ header: headerString, nr: currentIteration });
      currentIteration = next(currentIteration);
    }
  }
}
