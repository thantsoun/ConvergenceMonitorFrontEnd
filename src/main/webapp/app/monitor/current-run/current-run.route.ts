import { Route } from '@angular/router';

import { CurrentRunComponent } from './current-run.component';

export const currentRunRoute: Route = {
  path: '',
  component: CurrentRunComponent,
  data: {
    pageTitle: 'Current Run',
  },
};
