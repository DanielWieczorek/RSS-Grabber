import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { MatButtonModule, MatButtonToggleModule, MatTabsModule} from '@angular/material'
import { NgDragDropModule } from 'ng-drag-drop';
import { MatCardModule } from '@angular/material/card';
import { MatListModule,MatExpansionModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RssClassificationComponent } from './rss-classification/rss-classification.component';
import { RssInsightComponent } from './rss-insight/rss-insight.component';
import { MicroserviceStatusComponent } from './microservice-status/microservice-status.component';
import { TradingSimulationComponent } from './trading-simulation/trading-simulation.component';
import { RouterModule, Routes } from '@angular/router';
import { ChartsModule } from 'ng2-charts';
import { IntroductionComponent } from './introduction/introduction.component';

const appRoutes: Routes = [
                           { path: '', redirectTo: '/introduction', pathMatch: 'full' },
                           { path: 'microservice-status', component: MicroserviceStatusComponent },
                           { path: 'rss-classification', component: RssClassificationComponent },
                           { path: 'rss-insight', component: RssInsightComponent},
                           { path: 'trading-simulation', component: TradingSimulationComponent}
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
    ChartsModule,
    BrowserModule,
   
    HttpClientModule,
    MatCardModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatListModule,
    MatExpansionModule,
    MatTabsModule,
    BrowserAnimationsModule,
    NgDragDropModule.forRoot(),
        RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
