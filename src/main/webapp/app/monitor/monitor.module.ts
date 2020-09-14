import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'all-runs',
        loadChildren: () => import('./all-runs/all-runs.module').then(m => m.AllRunsModule),
      },
      {
        path: 'current-run',
        loadChildren: () => import('./current-run/current-run.module').then(m => m.CurrentRunModule),
      },
      {
        path: 'properties',
        loadChildren: () => import('./properties/properties.module').then(m => m.PropertiesModule),
      },
      {
        path: 'current-plots',
        loadChildren: () => import('./current-plots/current-plots.module').then(m => m.CurrentPlotsModule),
      },
    ]),
  ],
})
export class MonitorModule {}
