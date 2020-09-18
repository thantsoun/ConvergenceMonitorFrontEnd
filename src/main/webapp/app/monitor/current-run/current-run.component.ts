import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { showError } from '../../shared/util/funtions.util';
import { Summary } from './summary.model';
import { WbSummary } from './wb-summary.model';
// @ts-ignore
import Plotly from 'plotly.js-dist';
import { WbDetails } from './wb-details.model';

@Component({
  selector: 'jhi-current-run',
  templateUrl: './current-run.component.html',
})
export class CurrentRunComponent implements OnInit {
  summary = new Summary(BigInt(0), BigInt(0), '', '');
  wbSummary = new WbSummary('', '', BigInt(0), []);
  wbDetails = new WbDetails('', BigInt(0), new Map());
  jobTabs: string[] = [];

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.retrieveJobSummary();
    this.retrieveWbSummary();
    this.retrieveWbDetails();
  }

  private retrieveWbSummary(): void {
    this.httpClient
      .get<WbSummary>(SERVER_API_URL + 'api/wbSummary')
      .toPromise()
      .then(response => {
        this.wbSummary = response;
        this.plotPieChart();
      })
      .catch(error => {
        const title = error.headers.get('convergenceMonitorFrontEndApp-error-title');
        const msg = error.headers.get('convergenceMonitorFrontEndApp-error-details-001');
        showError(title, msg);
      });
  }

  private retrieveJobSummary(): void {
    this.httpClient
      .get<Summary>(SERVER_API_URL + 'api/jobSummary')
      .toPromise()
      .then(response => {
        this.summary = response;
      })
      .catch(error => {
        const title = error.headers.get('convergenceMonitorFrontEndApp-error-title');
        const msg = error.headers.get('convergenceMonitorFrontEndApp-error-details-001');
        showError(title, msg);
      });
  }

  private retrieveWbDetails(): void {
    this.httpClient
      .get<WbDetails>(SERVER_API_URL + 'api/wbDetails')
      .toPromise()
      .then(response => {
        this.jobTabs = [];
        this.wbDetails = response;
        const keys = Object.keys(this.wbDetails.jobs);
        keys.forEach(key => {
          this.jobTabs.push(key);
        });
      })
      .catch(error => {
        const title = error.headers.get('convergenceMonitorFrontEndApp-error-title');
        const msg = error.headers.get('convergenceMonitorFrontEndApp-error-details-001');
        showError(title, msg);
      });
  }

  private plotPieChart(): void {
    const valuesArray: bigint[] = [];
    const labelsArray: string[] = [];
    const colorsArray: string[] = [];
    const titleText = 'Iter ID: ' + this.wbSummary.iterIdDecoded + ' - Summary of ' + this.wbSummary.totalCount + ' jobs';

    this.wbSummary.jobs.forEach(job => {
      valuesArray.push(job.count);
      colorsArray.push(job.color);
      labelsArray.push(job.status);
    });

    const data = [
      {
        values: valuesArray,
        labels: labelsArray,
        marker: {
          colors: colorsArray,
        },
        type: 'pie',
        textinfo: 'label+percent',
        textposition: 'outside',
      },
    ];

    const layout = {
      title: {
        text: titleText,
        font: {
          size: 12,
        },
        xref: 'paper',
        x: -0.05,
      },
    };

    // @ts-ignore
    Plotly.newPlot('pieChart', data, layout);
  }
}
