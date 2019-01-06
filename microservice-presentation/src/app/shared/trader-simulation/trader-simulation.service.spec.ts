import { TestBed, inject } from '@angular/core/testing';

import { TraderSimulationService } from './trader-simulation.service';

describe('TraderSimulationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TraderSimulationService]
    });
  });

  it('should be created', inject([TraderSimulationService], (service: TraderSimulationService) => {
    expect(service).toBeTruthy();
  }));
});
