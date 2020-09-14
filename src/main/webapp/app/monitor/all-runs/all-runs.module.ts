import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvergenceMonitorFrontEndSharedModule } from 'app/shared/shared.module';

import { AllRunsComponent } from './all-runs.component';

import { allRunsRoute } from './all-runs.route';

@NgModule({
  imports: [ConvergenceMonitorFrontEndSharedModule, RouterModule.forChild([allRunsRoute])],
  declarations: [AllRunsComponent],
})
export class AllRunsModule {}
