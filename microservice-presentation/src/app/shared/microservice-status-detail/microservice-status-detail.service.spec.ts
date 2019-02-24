import { TestBed, inject } from '@angular/core/testing';

import { MicroserviceStatusDetailService } from './microservice-status-detail.service';

describe('MicroserviceStatusService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MicroserviceStatusDetailService]
    });
  });

  it('should be created', inject([MicroserviceStatusDetailService], (service: MicroserviceStatusDetailService) => {
    expect(service).toBeTruthy();
  }));
});
