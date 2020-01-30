import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { RssClassificationComponent } from './rss-classification/rss-classification.component';
import { RssInsightComponent } from './rss-insight/rss-insight.component';
import { MicroserviceStatusComponent } from './microservice-status/microservice-status.component';
import { TradingSimulationComponent } from './trading-simulation/trading-simulation.component';
import { TradingLiveComponent } from './trading-live/trading-live.component';

import { ChartMetricComponent } from './chart-metric/chart-metric.component';
import { RouterModule, Routes } from '@angular/router';
import { IntroductionComponent } from './introduction/introduction.component';
import { ChartReaderService } from './shared/chart-reader/chart-reader.service'
import { ChartMetricService } from './shared/chart-metric/chart-metric.service'
import { ChartAdvisorService } from './shared/chart-advisor/chart-advisor.service'

import { TraderSimulationService } from './shared/trader-simulation/trader-simulation.service'
import { TraderLiveService } from './shared/trader-live/trader-live.service'

import { RssInsightService } from './shared/rss-insight/rss-insight.service'
import { RssClassificationService } from './shared/rss-classification/rss-classification.service'
import { MicroserviceStatusService } from './shared/microservice-status/microservice-status.service'
import { AuthenticationGuard } from './common/authentication/authentication.guard';
import { AuthenticationService } from './common/authentication/authentication.service';
import { MicroserviceAuthenticationService } from './shared/microservice-authentication/microservice-authentication.service'


import { LoginComponent } from './login/login.component'
import { ReactiveFormsModule } from '@angular/forms';
import { MicroserviceStatusDetailComponent } from './microservice-status-detail/microservice-status-detail.component';
import { MicroserviceStatusDetailService } from './shared/microservice-status-detail/microservice-status-detail.service';


const appRoutes: Routes = [
                           { path: '', redirectTo: '/introduction', pathMatch: 'full'},
                           { path: 'microservice-status', component: MicroserviceStatusComponent,canActivate: [AuthenticationGuard] },
                           { path: 'microservice-status/:name', component: MicroserviceStatusDetailComponent,canActivate: [AuthenticationGuard] },
                           { path: 'rss-classification', component: RssClassificationComponent,canActivate: [AuthenticationGuard] },
                           { path: 'rss-insight', component: RssInsightComponent,canActivate: [AuthenticationGuard]},
                           { path: 'trading-simulation', component: TradingSimulationComponent,canActivate: [AuthenticationGuard]},
                           { path: 'trading-live', component: TradingLiveComponent,canActivate: [AuthenticationGuard]},
                           { path: 'chart-metric', component: ChartMetricComponent,canActivate: [AuthenticationGuard]},
                           { path: 'introduction', component: IntroductionComponent,canActivate: [AuthenticationGuard]},
                           { path: 'login', component: LoginComponent}
                           ];


@NgModule({
  declarations: [
    AppComponent,
    RssClassificationComponent,
    RssInsightComponent,
    MicroserviceStatusComponent,
    ChartMetricComponent,
    TradingSimulationComponent,
    IntroductionComponent,
    LoginComponent,
    MicroserviceStatusDetailComponent,
    TradingLiveComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
        RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
  ],
  providers: [
      ChartReaderService,
      TraderSimulationService,
      TraderLiveService,
      RssInsightService,
      RssClassificationService,
      MicroserviceStatusService,
      AuthenticationService,
      AuthenticationGuard,
      MicroserviceAuthenticationService,
      MicroserviceStatusDetailService,
      ChartMetricService,
      ChartAdvisorService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
