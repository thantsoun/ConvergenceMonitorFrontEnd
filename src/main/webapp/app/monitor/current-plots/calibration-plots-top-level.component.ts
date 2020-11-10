import { Component, Input, OnInit } from '@angular/core';
import { createPlotsUtilFromNode, CurrentPlotsUtil, NrEffectsUsed, PlotsTreeNode } from './current-plots-util.model';
import { handleHttpRequestError } from './function-utils';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';

@Component({
  selector: 'jhi-calibration-plots-top-level',
  templateUrl: './calibration-plots-top-level.component.html',
})
export class CalibrationPlotsTopLevelComponent implements OnInit {
  currentPlotsUtil!: CurrentPlotsUtil;
  nrEffectsUsed = new NrEffectsUsed(0, 0);
  startIter = 1;
  endIter = 0;
  activeTab = '';
  tabs: string[] = [];
  tabsPlotUtils: CurrentPlotsUtil[] = [];
  tabsIsUpdate: boolean[] = [];

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.httpClient
      .get<NrEffectsUsed>(SERVER_API_URL + 'api/nrEffectsUsed', { observe: 'response' })
      .toPromise()
      .then(response => {
        if (response.body !== null) {
          this.nrEffectsUsed = response.body;
          this.makeTabs();
        }
      })
      .catch(handleHttpRequestError);
  }

  @Input()
  set plotsUtil(currentPlotsUtil: CurrentPlotsUtil) {
    this.currentPlotsUtil = createPlotsUtilFromNode(currentPlotsUtil.plotCategory.children[0], currentPlotsUtil);
    this.makeTabs();
  }

  @Input()
  set startIteration(startIter: number) {
    this.startIter = startIter;
  }

  @Input()
  set endIteration(endIter: number) {
    this.endIter = endIter;
  }

  private makeTabs(): void {
    this.tabs = [];
    this.tabsPlotUtils = [];
    this.tabsIsUpdate = [];
    if (this.currentPlotsUtil) {
      const childCategories: PlotsTreeNode[] = this.currentPlotsUtil.plotCategory.children;
      childCategories.forEach(this.createMainTabs);
      childCategories.forEach(this.createUpdateTabs);
      this.activeTab = this.tabs[0];
    }
  }

  private createMainTabs = (plotCat: PlotsTreeNode) => {
    if (plotCat.code.endsWith('generic.ac') && this.nrEffectsUsed.ac > 0) {
      this.tabs.push(plotCat.description);
      this.tabsPlotUtils.push(createPlotsUtilFromNode(plotCat, this.currentPlotsUtil));
      this.tabsIsUpdate.push(false);
    } else if (plotCat.code.endsWith('generic.al') && this.nrEffectsUsed.al > 0) {
      this.tabs.push(plotCat.description);
      this.tabsPlotUtils.push(createPlotsUtilFromNode(plotCat, this.currentPlotsUtil));
      this.tabsIsUpdate.push(false);
    }
  };

  private createUpdateTabs = (plotCat: PlotsTreeNode) => {
    if (plotCat.code.endsWith('generic.ac') && this.nrEffectsUsed.ac > 0) {
      this.tabs.push(plotCat.description + ' Updates');
      this.tabsPlotUtils.push(createPlotsUtilFromNode(plotCat, this.currentPlotsUtil));
      this.tabsIsUpdate.push(true);
    } else if (plotCat.code.endsWith('generic.al') && this.nrEffectsUsed.al > 0) {
      this.tabs.push(plotCat.description + ' Updates');
      this.tabsPlotUtils.push(createPlotsUtilFromNode(plotCat, this.currentPlotsUtil));
      this.tabsIsUpdate.push(true);
    }
  };
}
