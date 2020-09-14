import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvergenceMonitorFrontEndSharedModule } from 'app/shared/shared.module';

import { CurrentRunComponent } from './current-run.component';

import { currentRunRoute } from './current-run.route';

@NgModule({
  imports: [ConvergenceMonitorFrontEndSharedModule, RouterModule.forChild([currentRunRoute])],
  declarations: [CurrentRunComponent],
})
export class CurrentRunModule {}
