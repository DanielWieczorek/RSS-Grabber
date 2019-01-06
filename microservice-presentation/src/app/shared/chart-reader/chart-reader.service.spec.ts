import { TestBed, inject } from '@angular/core/testing';

import { ChartReaderService } from './rss-reader.service';

describe('ChartReaderService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ChartReaderService]
    });
  });

  it('should be created', inject([ChartReaderService], (service: RssReaderService) => {
    expect(service).toBeTruthy();
  }));
});
