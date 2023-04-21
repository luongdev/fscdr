import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    iconComponent: { name: 'cil-speedometer' },
  },
  {
    name: 'Call Detail Records',
    url: '/cdr',
    iconComponent: { name: 'cil-user' },
  },
];
