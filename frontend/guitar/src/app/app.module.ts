import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PlectrumConfigComponent } from './plectrum-config/plectrum-config.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { FredConfigComponent } from './fred-config/fred-config.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';

const appRoutes: Routes = [
{ path: 'welcome',      component: WelcomeComponent },
{ path: 'config/plectrum',      component: PlectrumConfigComponent },
{ path: 'config/fred',      component: FredConfigComponent },
{ path: '',
redirectTo: '/welcome',
pathMatch: 'full'
},
{ path: '**', component: PageNotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    PlectrumConfigComponent,
    PageNotFoundComponent,
    WelcomeComponent,
    FredConfigComponent
  ],
  imports: [
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: false }
    ),
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  schemas: [
   NO_ERRORS_SCHEMA
 ]
})
export class AppModule { }
