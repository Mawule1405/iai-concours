import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RightModalTemplateComponent } from './right-modal-template.component';

describe('ModalTemplateComponent', () => {
  let component: RightModalTemplateComponent;
  let fixture: ComponentFixture<RightModalTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RightModalTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RightModalTemplateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
