import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FredConfigComponent } from './fred-config.component';

describe('FredConfigComponent', () => {
  let component: FredConfigComponent;
  let fixture: ComponentFixture<FredConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FredConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FredConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
