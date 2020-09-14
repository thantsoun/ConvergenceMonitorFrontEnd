import { Route } from '@angular/router';

import { PropertiesComponent } from './properties.component';

export const propertiesRoute: Route = {
  path: '',
  component: PropertiesComponent,
  data: {
    pageTitle: 'Properties',
  },
};
