import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlectrumConfigComponent } from './plectrum-config.component';

describe('PlectrumConfigComponent', () => {
  let component: PlectrumConfigComponent;
  let fixture: ComponentFixture<PlectrumConfigComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlectrumConfigComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlectrumConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
