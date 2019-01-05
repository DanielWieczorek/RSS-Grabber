import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { RssClassificationComponent } from './rss-classification/rss-classification.component';
import { RssInsightComponent } from './rss-insight/rss-insight.component';
import { MicroserviceStatusComponent } from './microservice-status/microservice-status.component';
import { TradingSimulationComponent } from './trading-simulation/trading-simulation.component';
import { RouterModule, Routes } from '@angular/router';
import { IntroductionComponent } from './introduction/introduction.component';

const appRoutes: Routes = [
                           { path: '', redirectTo: '/introduction', pathMatch: 'full' },
                           { path: 'microservice-status', component: MicroserviceStatusComponent },
                           { path: 'rss-classification', component: RssClassificationComponent },
                           { path: 'rss-insight', component: RssInsightComponent},
                           { path: 'trading-simulation', component: TradingSimulationComponent},
                           { path: 'introduction', component: IntroductionComponent}
                           ];


@NgModule({
  declarations: [
    AppComponent,
    RssClassificationComponent,
    RssInsightComponent,
    MicroserviceStatusComponent,
    TradingSimulationComponent,
    IntroductionComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
        RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
