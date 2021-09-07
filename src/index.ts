import { registerPlugin } from '@capacitor/core';

import type { ExternalFilesPlugin } from './definitions';

const ExternalFiles = registerPlugin<ExternalFilesPlugin>('ExternalFiles', {
  web: () => import('./web').then(m => new m.ExternalFilesWeb()),
});

export * from './definitions';
export { ExternalFiles };
