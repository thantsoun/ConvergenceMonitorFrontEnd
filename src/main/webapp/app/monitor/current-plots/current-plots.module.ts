import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvergenceMonitorFrontEndSharedModule } from 'app/shared/shared.module';

import { CurrentPlotsComponent } from './current-plots.component';

import { currentPlotsRoute } from './current-plots.route';
import { SourcePlotsTopLevelComponent } from './source-plots-top-level.component';
import { CalibrationPlotsComponent } from './calibration-plots.component';
import { SourcePlotsBottomLevelComponent } from './source-plots-bottom-level.component';
import { AttitudePlotsComponent } from './attitude-plots.component';
import { GlobalPlotsComponent } from './global-plots.component';

@NgModule({
  imports: [ConvergenceMonitorFrontEndSharedModule, RouterModule.forChild([currentPlotsRoute])],
  declarations: [
    CurrentPlotsComponent,
    SourcePlotsTopLevelComponent,
    CalibrationPlotsComponent,
    SourcePlotsBottomLevelComponent,
    AttitudePlotsComponent,
    GlobalPlotsComponent,
  ],
})
export class CurrentPlotsModule {}
