import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { MicroserviceStatusService } from '../shared/microservice-status/microservice-status.service'
import { MicroserviceStatusDetailService } from '../shared/microservice-status-detail/microservice-status-detail.service'
import { RecalculationStatus } from '../shared/microservice-status-detail/recalculation-status'

import { MicroserviceStatus } from '../shared/microservice-status/microservice-status'
import 'rxjs/Rx';



@Component( {
    selector: 'app-microservice-status-detail',
    templateUrl: './microservice-status-detail.component.html',
    styleUrls: ['./microservice-status-detail.component.css']
} )
export class MicroserviceStatusDetailComponent implements OnInit {

    data: MicroserviceStatus;
    recalculation: RecalculationStatus;

    constructor( private microserviceStatus: MicroserviceStatusService,
        private microserviceStatusDetail: MicroserviceStatusDetailService,
        private route: ActivatedRoute ) { }

    ngOnInit() {
        this.route.params
            .subscribe( params => this.microserviceStatus.getCachedData()
                .subscribe( data => { 
                    this.data = data.find( item => item.name === params['name'] );
                 console.log( this.data );
                 if(this.data.features.some(x => x.type == 'RECALCULATION')) {
                    this.recalculationStatus();
                 }
                } ) );
    }


    performExport() {
        const dataObservable = this.microserviceStatusDetail.performGetAction( `routing/${this.data.name}/feature/export`);
        dataObservable.subscribe( data => {

            const blob = new Blob( [JSON.stringify( data )], { type: 'text/json' } );
            const url = window.URL.createObjectURL( blob );
            var link = document.createElement( 'a' );
            // Browsers that support HTML5 download attribute
            if ( link.download !== undefined ) {
                link.setAttribute( 'href', url );
                link.setAttribute( 'download', `${this.data.name}-export.json` );
                link.style.visibility = 'hidden';
                document.body.appendChild( link );
                link.click();
                document.body.removeChild( link );
            }
        } );
    }

    performImport( files: FileList ) {
        let fileReader = new FileReader();
        fileReader.onload = ( e ) => {
            console.log( fileReader.result );
            this.microserviceStatusDetail.performPostAction( `routing/${this.data.name}/feature/import`, fileReader.result as string)
                .subscribe( x => {
                    console.log( x );
                } );
        }
        console.log( files[0] );
        fileReader.readAsText( files[0] );

    }

    startRecalculation() {
        this.microserviceStatusDetail.performGetAction( `routing/${this.data.name}/recalculation/start`)
                .subscribe( x => {
                    this.recalculationStatus();
                } );
    }

    stopRecalculation() {
        this.microserviceStatusDetail.performGetAction( `routing/${this.data.name}/recalculation/stop`)
                .subscribe( x => {
                    this.recalculationStatus();
                } );
    }

    recalculationStatus() {
        this.microserviceStatusDetail.performGetAction( `routing/${this.data.name}/recalculation/status`)
                .subscribe( x => {
                   this.recalculation = x;
                } );
    }

    getDateString () : string{
         let jsdate = new Date( this.recalculation.lastDate[0], this.recalculation.lastDate[1], this.recalculation.lastDate[2], this.recalculation.lastDate[3], this.recalculation.lastDate[4] );
        return  jsdate.toLocaleTimeString( 'en', { year: 'numeric', month: 'short', day: 'numeric' } );
    }



}
