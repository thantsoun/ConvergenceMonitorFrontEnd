import { Component, Input, OnInit } from '@angular/core';
import { CurrentPlotsUtil, generateNextLevelPlotsUtil } from './current-plots-util.model';

@Component({
  selector: 'jhi-source-plots-top-level',
  templateUrl: './source-plots-top-level.component.html',
})
export class SourcePlotsTopLevelComponent implements OnInit {
  currentPlotsUtil!: CurrentPlotsUtil;
  nextPlotsUtil!: CurrentPlotsUtil[];
  startIter = 1;
  endIter = 0;
  activeTab = '';

  constructor() {}

  ngOnInit(): void {}

  @Input()
  set plotsUtil(currentPlotsUtil: CurrentPlotsUtil) {
    [this.currentPlotsUtil, this.nextPlotsUtil, this.activeTab] = generateNextLevelPlotsUtil(currentPlotsUtil);
  }

  @Input()
  set startIteration(startIter: number) {
    this.startIter = startIter;
  }

  @Input()
  set endIteration(endIter: number) {
    this.endIter = endIter;
  }
}
