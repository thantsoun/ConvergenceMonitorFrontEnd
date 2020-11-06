import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CurrentPlotsUtil, PlotsTreeNode } from './current-plots-util.model';
import { SERVER_API_URL } from '../../app.constants';
import { showError } from '../../shared/util/funtions.util';
import { generateNextLevelPlotsUtil, handleHttpRequestError } from './function-utils';

@Component({
  selector: 'jhi-current-plots',
  templateUrl: './current-plots.component.html',
})
export class CurrentPlotsComponent implements OnInit {
  private emptyTreeNode = new PlotsTreeNode('', '', '', [], []);
  currentPlotsUtil = new CurrentPlotsUtil(0, 0, '', '', this.emptyTreeNode);
  nextPlotsUtil: CurrentPlotsUtil[] = [];
  startIter = 0;
  endIter = 1;
  startIterForm = 1;
  endIterForm = 0;
  activeTab = '';
  defaultIterSpread = 5;

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.httpClient
      .get<CurrentPlotsUtil>(SERVER_API_URL + 'api/currentPlotsUtil')
      .toPromise()
      .then(this.handleSuccess)
      .catch(handleHttpRequestError);
  }

  refresh(): void {
    const errors: string[] = [];
    if (this.startIterForm < 1) {
      errors.push('Start iteration is less than 1');
    } else if (this.startIterForm > this.currentPlotsUtil.currentIter) {
      errors.push('Start iteration is more than the last iteration: ' + this.currentPlotsUtil.currentIter);
    }
    if (this.endIterForm < 1) {
      errors.push('End iteration is less than 1');
    } else if (this.endIterForm > this.currentPlotsUtil.currentIter) {
      errors.push('End iteration is more than the last iteration: ' + this.currentPlotsUtil.currentIter);
    }
    if (errors.length === 0) {
      this.startIter = this.startIterForm;
      this.endIter = this.endIterForm;
    } else {
      showError('Values out of range', ...errors);
    }
  }

  private handleSuccess = (response: CurrentPlotsUtil) => {
    [this.currentPlotsUtil, this.nextPlotsUtil, this.activeTab] = generateNextLevelPlotsUtil(response);
    this.startIter = this.currentPlotsUtil.currentIter;
    if (this.endIter < this.startIter - this.defaultIterSpread + 1) {
      this.endIter = this.startIter - this.defaultIterSpread + 1;
    }
    this.endIterForm = this.endIter;
    this.startIterForm = this.startIter;
  };
}
