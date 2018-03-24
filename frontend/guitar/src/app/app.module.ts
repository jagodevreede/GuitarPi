import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PlectrumConfigComponent } from './plectrum-config/plectrum-config.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { WelcomeComponent } from './welcome/welcome.component';

const appRoutes: Routes = [
{ path: 'welcome',      component: WelcomeComponent },
{ path: 'config/plectrum',      component: PlectrumConfigComponent },
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
    WelcomeComponent
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
  bootstrap: [AppComponent]
})
export class AppModule { }
