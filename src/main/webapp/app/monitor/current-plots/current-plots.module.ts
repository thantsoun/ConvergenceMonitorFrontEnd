import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvergenceMonitorFrontEndSharedModule } from 'app/shared/shared.module';

import { CurrentPlotsComponent } from './current-plots.component';

import { currentPlotsRoute } from './current-plots.route';

@NgModule({
  imports: [ConvergenceMonitorFrontEndSharedModule, RouterModule.forChild([currentPlotsRoute])],
  declarations: [CurrentPlotsComponent],
})
export class CurrentPlotsModule {}
