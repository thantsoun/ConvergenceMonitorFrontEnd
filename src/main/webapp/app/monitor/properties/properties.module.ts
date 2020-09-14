import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ConvergenceMonitorFrontEndSharedModule } from 'app/shared/shared.module';

import { PropertiesComponent } from './properties.component';

import { propertiesRoute } from './properties.route';

@NgModule({
  imports: [ConvergenceMonitorFrontEndSharedModule, RouterModule.forChild([propertiesRoute])],
  declarations: [PropertiesComponent],
})
export class PropertiesModule {}
