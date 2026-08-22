import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CenterModalTemplateComponent } from './center-modal-template.component';

describe('ModalTemplateComponent', () => {
  let component: CenterModalTemplateComponent;
  let fixture: ComponentFixture<CenterModalTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CenterModalTemplateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CenterModalTemplateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
