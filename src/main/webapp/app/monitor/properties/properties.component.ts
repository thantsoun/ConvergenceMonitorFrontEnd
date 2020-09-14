import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'jhi-properties',
  templateUrl: './properties.component.html',
})
export class PropertiesComponent implements OnInit {
  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {}
}
