import { TestBed, inject } from '@angular/core/testing';

import { MicroserviceStatusService } from './microservice-status.service';

describe('MicroserviceStatusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MicroserviceStatusService]
    });
  });

  it('should be created', inject([MicroserviceStatusService], (service: MicroserviceStatusService) => {
    expect(service).toBeTruthy();
  }));
});
