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

@NgModule({
  declarations: [
    AppComponent,
    RssClassificationComponent,
    RssInsightComponent,
    MicroserviceStatusComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    MatCardModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatListModule,
    MatExpansionModule,
    MatTabsModule,
    BrowserAnimationsModule,
    NgDragDropModule.forRoot()
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
