import { Route } from '@angular/router';

import { CurrentPlotsComponent } from './current-plots.component';

export const currentPlotsRoute: Route = {
  path: '',
  component: CurrentPlotsComponent,
  data: {
    pageTitle: 'Current Plots',
  },
};
