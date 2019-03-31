import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpHelperService {

  private protocol = "http"

    
  constructor() { }
  
  
  
  buildPath( path: string): string {
      return `${this.protocol}://${environment.backendHostname}:${environment.backendPort}/${path}`
  }
  
  handleError( error: HttpErrorResponse ) {
      if ( error.error instanceof ErrorEvent ) {
          // A client-side or network error occurred. Handle it accordingly.
          console.error( 'An error occurred:', error.error.message );
          return throwError(
          'An error occurred while accessing the backend service' );
      } else {
          // The backend returned an unsuccessful response code.
          // The response body may contain clues as to what went wrong,
          console.error(
              `Backend returned code ${error.status}, ` +
              `body was: ${error.error}` );
          
          return throwError(
          'Could not reach backend service' );
      }
      
  };
}
