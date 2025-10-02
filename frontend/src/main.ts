import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { AppModule } from './app/app.module';

console.log(localStorage.getItem('cartItems') || '[]');
localStorage.removeItem('cartItems');
// localStorage.removeItem("jwt")
platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));
