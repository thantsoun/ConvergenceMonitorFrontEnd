import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { ConvergenceMonitorFrontEndSharedModule } from 'app/shared/shared.module';
import { ConvergenceMonitorFrontEndCoreModule } from 'app/core/core.module';
import { ConvergenceMonitorFrontEndAppRoutingModule } from './app-routing.module';
import { ConvergenceMonitorFrontEndHomeModule } from './home/home.module';
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    ConvergenceMonitorFrontEndSharedModule,
    ConvergenceMonitorFrontEndCoreModule,
    ConvergenceMonitorFrontEndHomeModule,
    ConvergenceMonitorFrontEndAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class ConvergenceMonitorFrontEndAppModule {}
