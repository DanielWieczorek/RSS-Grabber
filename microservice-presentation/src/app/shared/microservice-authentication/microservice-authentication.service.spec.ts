import { TestBed, inject } from '@angular/core/testing';

import { MicroserviceAuthenticationService } from './microservice-authentication.service';

describe('MicroserviceAuthenticationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MicroserviceAuthenticationService]
    });
  });

  it('should be created', inject([MicroserviceAuthenticationService], (service: MicroserviceAuthenticationService) => {
    expect(service).toBeTruthy();
  }));
});
