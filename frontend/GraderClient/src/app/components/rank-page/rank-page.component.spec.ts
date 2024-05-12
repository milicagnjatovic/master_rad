import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankPageComponent } from './rank-page.component';

describe('RankPageComponent', () => {
  let component: RankPageComponent;
  let fixture: ComponentFixture<RankPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RankPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RankPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
