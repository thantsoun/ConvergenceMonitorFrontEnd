import { Route } from '@angular/router';

import { AllRunsComponent } from './all-runs.component';

export const allRunsRoute: Route = {
  path: '',
  component: AllRunsComponent,
  data: {
    pageTitle: 'All Runs',
  },
};
