import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'jhi-current-plots',
  templateUrl: './current-plots.component.html',
})
export class CurrentPlotsComponent implements OnInit {
  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {}
}
