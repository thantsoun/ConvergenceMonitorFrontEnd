import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../app.constants';
import { showError } from '../../shared/util/funtions.util';
import { Prop, PropsMap } from './properties.model';

@Component({
  selector: 'jhi-properties',
  templateUrl: './properties.component.html',
})
export class PropertiesComponent implements OnInit {
  propsMap!: PropsMap;
  props: Prop[] = [];
  filterText = '';

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.httpClient
      .get<PropsMap>(SERVER_API_URL + 'api/properties')
      .toPromise()
      .then(response => {
        this.propsMap = response;
        this.filter();
      })
      .catch(error => {
        const title = error.headers.get('convergenceMonitorFrontEndApp-error-title');
        const msg = error.headers.get('convergenceMonitorFrontEndApp-error-details-001');
        showError(title, msg);
      });
  }

  filter(): void {
    this.props = [];
    const keys = Object.keys(this.propsMap);
    keys.forEach(key => {
      if (key.toLowerCase().includes(this.filterText.toLocaleLowerCase())) {
        this.props.push(new Prop(key, this.propsMap[key]));
      }
    });
  }
}
