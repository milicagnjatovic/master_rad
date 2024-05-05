import { TestBed } from '@angular/core/testing';

import { TaskSubmissionsService } from './task-submissions.service';

describe('TaskSubmissionsService', () => {
  let service: TaskSubmissionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaskSubmissionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
