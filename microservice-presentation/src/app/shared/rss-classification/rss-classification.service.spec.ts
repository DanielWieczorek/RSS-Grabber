import { TestBed, inject } from '@angular/core/testing';

import { RssClassificationService } from './rss-classification.service';

describe('RssClassificationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RssClassificationService]
    });
  });

  it('should be created', inject([RssClassificationService], (service: RssClassificationService) => {
    expect(service).toBeTruthy();
  }));
});
