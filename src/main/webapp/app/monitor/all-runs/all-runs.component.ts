import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AllRuns } from './all-runs.model';
import { SERVER_API_URL } from '../../app.constants';
import { sort } from 'app/shared/util/sort-util';
import { showError } from '../../shared/util/funtions.util';

@Component({
  selector: 'jhi-all-runs',
  templateUrl: './all-runs.component.html',
})
export class AllRunsComponent implements OnInit {
  allRuns!: AllRuns;
  predicate = 'runId';
  ascending = true;

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.httpClient
      .get<AllRuns>(SERVER_API_URL + 'api/allRuns')
      .toPromise()
      .then(response => {
        this.allRuns = response;
        this.sortTable();
      })
      .catch(error => {
        const title = error.headers.get('convergenceMonitorFrontEndApp-error-title');
        const msg = error.headers.get('convergenceMonitorFrontEndApp-error-details-001');
        showError(title, msg);
      });
  }

  sortTable(): void {
    this.allRuns.runs = sort(this.allRuns.runs, this.predicate, !this.ascending);
  }
}
