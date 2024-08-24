import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfessorMessagesPageComponent } from './professor-messages-page.component';

describe('ProfessorMessagesPageComponent', () => {
  let component: ProfessorMessagesPageComponent;
  let fixture: ComponentFixture<ProfessorMessagesPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProfessorMessagesPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfessorMessagesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
