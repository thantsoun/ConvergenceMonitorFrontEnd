import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'jhi-current-run',
  templateUrl: './current-run.component.html',
})
export class CurrentRunComponent implements OnInit {
  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {}
}
