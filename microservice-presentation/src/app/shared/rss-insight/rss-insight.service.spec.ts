import { TestBed, inject } from '@angular/core/testing';

import { RssInsightService } from './rss-insight.service';

describe('RssInsightService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RssInsightService]
    });
  });

  it('should be created', inject([RssInsightService], (service: RssInsightService) => {
    expect(service).toBeTruthy();
  }));
});
