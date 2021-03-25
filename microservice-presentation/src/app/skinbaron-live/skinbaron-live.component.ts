import { Component, OnInit, AfterViewInit } from '@angular/core';
import {Observable} from 'rxjs';
import { Stock } from '../shared/skinbaron-live/stock';
import { SkinbaronLiveService } from '../shared/skinbaron-live/skinbaron-live.service';


@Component({
    selector: 'app-skinbaron-live',
    templateUrl: './skinbaron-live.component.html',
    styleUrls: ['./skinbaron-live.component.css']
})
export class SkinbaronLiveComponent {

    title = 'app';

    constructor(private skinbaronService: SkinbaronLiveService) {

    }
   
    data: Stock[];

    ngOnInit(): void {
        this.skinbaronService.getAllStock().subscribe(res => {
            if(res.length > 0){
                this.data = res;
            }
        });
    }

}
